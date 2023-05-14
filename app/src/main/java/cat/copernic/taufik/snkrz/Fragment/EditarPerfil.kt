package cat.copernic.taufik.snkrz.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.Model.Usuario
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentEditarPerfilBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * Fragmento para editar el perfil de usuario.
 */
class EditarPerfil : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    private val storageRef = FirebaseStorage.getInstance().reference

    val email = Firebase.auth.currentUser?.email
    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)

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
                Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        binding.GuardarDatosPerfil.setOnClickListener {

            if (user != null) {
                //Modifiquem el usuari mitjançant la funció modificarUsuario creada per nosaltres
                modificarUsuario(llegirDades(tipoUsuario))

            } else {
                mostrarMensaje("No se ha podido modificar el perfil")
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
    fun modificarUsuario(user: Usuario) {
        if (email != null) {
            db.collection("Usuarios").document(email).set(user)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_editarPerfil_to_perfil)
                    mostrarMensaje("El perfil se ha modificado correctamente")
                }
                .addOnFailureListener{
                    mostrarMensaje("El perfil no se ha modificado")
                }
        }
    }

    /**
     * Función de callback para obtener la imagen seleccionada.
     *
     * @param uri URI de la imagen seleccionada
     */
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    val user = auth.currentUser
                    val fileName = "imagen_perfil.jpg"
                    val imageRef = storageRef.child("imagen/perfil/${user?.uid}/$fileName")
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.await()

                    withContext(Dispatchers.Main) {
                        binding.imgPerfilPerfil.setImageBitmap(bitmap)
                        mostrarMensaje("Imagen subida correctamente")
                        //Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        mostrarMensaje("Error al subir la imagen")
                        //Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Carga la imagen de perfil del usuario desde Firebase Storage.
     */
    private fun carregarImatge(){
        auth = Firebase.auth
        val user = auth.currentUser
        val fileName = "imagen_perfil.jpg"
        var adrecaImatge = storageRef.child("imagen/perfil/${user?.uid}/$fileName")

        val fitxerTemporal = File.createTempFile("temp", null)

        lifecycleScope.launch(Dispatchers.IO) { // Ejecuta las tareas en un hilo de fondo
            try {
                adrecaImatge.getFile(fitxerTemporal).await() // Espera a que se complete la descarga del archivo
                val mapaBits = BitmapFactory.decodeFile(fitxerTemporal.absolutePath)

                withContext(Dispatchers.Main) { // Actualiza la interfaz de usuario en el hilo principal
                    binding.imgPerfilPerfil.setImageBitmap(mapaBits)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(context, "La carrega de la imatge ha fallado o no existe imagen.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Muestra un mensaje en forma de Snackbar.
     *
     * @param mensaje mensaje a mostrar
     */
    fun mostrarMensaje(mensaje: String) {
        val snackbar = Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }
}