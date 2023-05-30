package cat.copernic.taufik.snkrz.Fragment

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.Model.Usuario
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.Utils.Utils
import cat.copernic.taufik.snkrz.databinding.FragmentEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import java.util.regex.Pattern

/**
 * Fragmento para editar el perfil de usuario.
 */
class EditarPerfil : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    private val storageRef = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    val email = Firebase.auth.currentUser?.email
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    //val user = Firebase.auth.currentUser
    val user = Firebase.auth.currentUser
    var tipoUsuario = false


    /**
     * Método que se llama al crear la vista del fragmento.
     *
     * @param inflater El LayoutInflater utilizado para inflar la vista.
     * @param container El contenedor padre en el que se infla la vista.
     * @param savedInstanceState El estado previamente guardado del fragmento.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return binding.root
    }

    /**
     * Método que se llama una vez que la vista del fragmento ha sido creada.
     *
     * @param view La vista del fragmento.
     * @param savedInstanceState El estado previamente guardado del fragmento.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        carregarImatge()


        binding.CancelarDatosPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_editarPerfil_to_perfil)
        }

        binding.txtCambiarFoto.setOnClickListener {
            getContent.launch("image/*")
        }

        val email = FirebaseAuth.getInstance().currentUser!!.email

        lifecycleScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    db.collection("Usuarios").document(email.toString()).get().await()
                }
                val data = snapshot.data
                if (data != null) {

                    val nombre = data["nombre"]?.toString()
                    val apellido = data["apellido"]?.toString()
                    val dni = data["dni"]?.toString()
                    val telefono = data["telefono"]?.toString()

                    val esAdmin = data["esAdmin"] as? Boolean ?: false
                    tipoUsuario = esAdmin
                    llegirDades(esAdmin)

                    binding.NombreEditarPerfil.text = Editable.Factory.getInstance().newEditable(nombre)
                    binding.ApellidoEditarPerfil.text = Editable.Factory.getInstance().newEditable(apellido)
                    binding.DNIEditarPerfil.text = Editable.Factory.getInstance().newEditable(dni)
                    binding.TelefonoEditarPerfil.text = Editable.Factory.getInstance().newEditable(telefono)

                }
            } catch (e: Exception) {
                Utils.mostrarMensaje(getString(R.string.fail), binding.root)
            }
        }

        binding.GuardarDatosPerfil.setOnClickListener {

            val telefono = binding.TelefonoEditarPerfil.text.toString()
            val dni = binding.DNIEditarPerfil.text.toString()

            if (isValidTelefono(telefono)) {
                if (isValidDni(dni)) {
                    if (user != null) {
                        // Modifiquem el usuari mitjançant la funció modificarUsuario creada per nosaltres
                        modificarUsuario(llegirDades(tipoUsuario), telefono, dni)

                    } else {
                        Utils.mostrarMensaje(getString(R.string.EditarPerfil1), binding.root)
                    }
                } else {
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil2), binding.root)
                }
            } else {
                Utils.mostrarMensaje(getString(R.string.EditarPerfil3), binding.root)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Lee los datos del formulario y crea un objeto Usuario.
     *
     * @param esAdmin indica si el usuario es un administrador
     * @return objeto Usuario con los datos del formulario
     */
    fun llegirDades(esAdmin: Boolean): Usuario {
        var Email = user?.email.toString()
        var nombre = binding.NombreEditarPerfil.text.toString()
        var apellido = binding.ApellidoEditarPerfil.text.toString()
        var dni = binding.DNIEditarPerfil.text.toString()
        val telefonoText = binding.TelefonoEditarPerfil.text.toString()
        val telefono = if (telefonoText.isNotEmpty()) telefonoText.toInt() else 0

        return Usuario(email.toString(), Email, nombre, apellido, dni, telefono, esAdmin)
    }

    /**
     * Modifica el usuario en la base de datos.
     *
     * @param user objeto Usuario con los datos a modificar
     */
    fun modificarUsuario(user: Usuario, telefono: String, dni: String) {

        if (email != null) {
            db.collection("Usuarios").document(email).set(user)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_editarPerfil_to_perfil)
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil5), binding.root)
                }
                .addOnFailureListener {
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil6), binding.root)
                }
        }
    }

    /**
     * Contrato de actividad utilizado para obtener contenido seleccionado por el usuario, en este caso, una imagen.
     * Si se selecciona una imagen, se asigna a la variable `selectedImageUri` y se llama a la función `uploadImage`
     * con la URI de la imagen y un código de referencia. Si no se selecciona ninguna imagen, se muestra un mensaje de error.
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                uploadImage(uri, "codigoRef")
            } else {
                Utils.mostrarMensaje(getString(R.string.EditarPerfil7), binding.root)
            }
        }

    /**
     * Sube una imagen al perfil de usuario en Firebase Storage.
     *
     * @param uri La ubicación de la imagen a subir.
     * @param codigoRef El código de referencia para la imagen.
     */
    private fun uploadImage(uri: Uri, codigoRef: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val imageRef =
                    storageRef.child("imagen/perfil/${user?.uid}")
                val uploadTask = imageRef.putFile(uri)
                uploadTask.await()

                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil8), binding.root)
                    binding.imgPerfilPerfil.setImageURI(selectedImageUri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil9), binding.root)
                }
            }
        }
    }

    /**
     * Carga la imagen de perfil del usuario desde Firebase Storage.
     */
    private fun carregarImatge() {
        auth = Firebase.auth

        var adrecaImatge = storageRef.child("imagen/perfil/${user?.uid}")

        val fitxerTemporal = File.createTempFile("temp", null)

        lifecycleScope.launch(Dispatchers.IO) { // Ejecuta las tareas en un hilo de fondo
            try {
                adrecaImatge.getFile(fitxerTemporal)
                    .await() // Espera a que se complete la descarga del archivo
                val mapaBits = BitmapFactory.decodeFile(fitxerTemporal.absolutePath)

                withContext(Dispatchers.Main) { // Actualiza la interfaz de usuario en el hilo principal
                    binding.imgPerfilPerfil.setImageBitmap(mapaBits)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(context, "La carrega de la imatge ha fallado o no existe imagen.", Toast.LENGTH_LONG).show()
                    Utils.mostrarMensaje(getString(R.string.EditarPerfil10), binding.root)
                }
            }
        }
    }

    /**
     * Verifica si un número de teléfono es válido según el formato especificado.
     *
     * @param telefono El número de teléfono a verificar.
     * @return `true` si el número de teléfono es válido, `false` de lo contrario.
     */
    var telefono_Param = Pattern.compile("^\\d{9}\$")
    fun isValidTelefono(telefono: CharSequence?): Boolean {
        return if (telefono == null) false else telefono_Param.matcher(telefono).matches()
    }

    /**
     * Verifica si un número de DNI es válido según el formato especificado.
     *
     * @param dni El número de DNI a verificar.
     * @return `true` si el número de DNI es válido, `false` de lo contrario.
     */
    var dni_Param = Pattern.compile("^\\d{8}[A-Z]\$")
    fun isValidDni(dni: CharSequence?): Boolean {
        return if (dni == null) false else dni_Param.matcher(dni).matches()
    }
}