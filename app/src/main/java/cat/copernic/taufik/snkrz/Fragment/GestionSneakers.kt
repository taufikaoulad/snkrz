package cat.copernic.taufik.snkrz.Fragment

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.Utils.Utils
import cat.copernic.taufik.snkrz.databinding.FragmentGestionSneakersBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_gestion_sneakers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase para gestionar sneakers.
 * Extiende de Fragment.
 */
class GestionSneakers : Fragment() {

    private var _binding: FragmentGestionSneakersBinding? = null
    private val binding get() = _binding!!

    private var bd = FirebaseFirestore.getInstance()

    private val storageRef = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

    /**
     * Crea y devuelve la vista asociada al fragmento.
     *
     * @param inflater El LayoutInflater utilizado para inflar la vista.
     * @param container El contenedor padre en el cual se debe insertar la vista.
     * @param savedInstanceState Los datos de estado previamente guardados del fragmento (si los hay).
     * @return La vista creada o null si ocurre un error.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentGestionSneakersBinding.inflate(inflater, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Inflate the layout for this fragment
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

        val args = GestionSneakersArgs.fromBundle(requireArguments())

        val nombreSneaker = args.sneaker.NombreSneaker
        val modeloSneaker = args.sneaker.ModelSneaker
        val fechaLanzamientoStr = args.sneaker.FechaLanzamiento
        val precio = args.sneaker.Precio
        val descripcion = args.sneaker.Descripcion
        val codigoReferencia = args.sneaker.CodigoReferencia

        //Asignamos los valores de los argumentos a los campos de la vista utilizando binding
        binding.editTextModeloGestio.setText(modeloSneaker)
        binding.edittextNombreGestio.setText(nombreSneaker)
        binding.editTextPrecioCrearGestio.setText(precio.toString())
        binding.editTextCodRefGestio.setText(codigoReferencia)
        binding.editTextdescripcionGestio.setText(descripcion)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaLanzamientoDate = dateFormat.parse(fechaLanzamientoStr)

        // Obtenemos el año, mes y día de la fecha
        val calendar = Calendar.getInstance()
        calendar.time = fechaLanzamientoDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Establecemos la fecha en el DatePicker
        binding.fechaEventoGestio.updateDate(year, month, day)

        binding.eliminarDatosGestionaSneaker.setOnClickListener {
            val codigoRef = binding.editTextCodRefGestio.text.toString()
            eliminarSneaker(codigoRef)
        }

        binding.GuardarDatosGestionaSneaker.setOnClickListener {
            val Sneaker : sneaker = llegirDades()

            if (checkEmpty(Sneaker)){
                anadirSneaker(Sneaker)
            }else{
                Utils.mostrarMensaje(getString(R.string.GestionarSneaker), binding.root)
            }
        }

        binding.btnAddImageGestio.setOnClickListener {
            getContent.launch("image/*")
        }

    }

    /**
     * Lee los datos de la vista y crea un objeto de tipo sneaker con los valores obtenidos.
     *
     * @return Un objeto sneaker con los datos leídos de la vista.
     */
    fun llegirDades(): sneaker{
        val codigoRef = binding.editTextCodRefGestio.text.toString()
        val modeloSneaker = binding.editTextModeloGestio.text.toString()
        val nombreSneaker = binding.edittextNombreGestio .text.toString()
        val precio = binding.editTextPrecioCrearGestio.text.toString()
        val descripcion = binding.editTextdescripcionGestio.text.toString()

        // Obtenemos la fecha seleccionada del DatePicker
        val datePicker = binding.fechaEventoGestio
        val year = datePicker.year
        val month = datePicker.month + 1
        val day = datePicker.dayOfMonth

        // Construimos la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
        val fechaLanz = String.format("%02d/%02d/%04d", day, month, year)

        return sneaker(codigoRef, modeloSneaker, nombreSneaker, precio, fechaLanz, descripcion, null)
    }

    /**
     * Añade una sneaker a la base de datos.
     *
     * @param snkr El objeto sneaker a añadir.
     */
    fun anadirSneaker(snkr: sneaker){
        val codigoRef = snkr.CodigoReferencia
        val delayMillis = 2100L
        if (codigoRef != null) {
                bd.collection("Sneakers").document(editTextCodRefGestio.text.toString()).set(snkr)
                    .addOnSuccessListener { //S'ha modificat la sneaker...
                        Utils.mostrarMensaje(getString(R.string.GestionarSneaker2), binding.root)

                        // Guardar la imagen en el almacenamiento usando el código de referencia
                        if (selectedImageUri != null) {
                            uploadImage(selectedImageUri!!, codigoRef)
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            // Código para realizar la navegación aquí
                            findNavController().navigate(R.id.action_gestionSneakers_to_pantallaPrincipalSneakerList)
                        }, delayMillis)
                    }
                    .addOnFailureListener { //No s'ha afegit el departament...
                        Utils.mostrarMensaje(getString(R.string.GestionarSneaker3), binding.root)
                    }
        } else {
            Utils.mostrarMensaje(getString(R.string.GestionarSneaker5), binding.root)
        }
    }

    /**
     * Verifica si los campos del objeto sneaker no están vacíos.
     *
     * @param Sneaker El objeto sneaker a verificar.
     * @return true si todos los campos están llenos, false de lo contrario.
     */
    fun checkEmpty(Sneaker:sneaker): Boolean{
        return Sneaker.NombreSneaker.isNotEmpty() && Sneaker.ModelSneaker.isNotEmpty() &&
                Sneaker.Precio.isNotEmpty() && Sneaker.CodigoReferencia.isNotEmpty() &&
                Sneaker.Descripcion.isNotEmpty() && Sneaker.FechaLanzamiento.isNotEmpty()
    }

    /**
     * Contrato de actividad utilizado para obtener contenido seleccionado por el usuario, en este caso, una imagen.
     * Si se selecciona una imagen, se asigna a la variable `selectedImageUri`. Si no se selecciona ninguna imagen,
     * se muestra un mensaje de error.
     */
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
            } else {
                Utils.mostrarMensaje(getString(R.string.GestionarSneaker6), binding.root)
            }
        }

    /**
     * Sube una imagen a Firebase Storage.
     *
     * @param uri La ubicación de la imagen a subir.
     * @param codigoRef El código de referencia para la imagen.
     */
    private fun uploadImage(uri: Uri, codigoRef: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val imageRef =
                    storageRef.child("imagen/sneaker/").child(codigoRef)
                val uploadTask = imageRef.putFile(uri)
                uploadTask.await()

                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.GestionarSneaker7), binding.root)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.GestionarSneaker8), binding.root)
                }
            }
        }
    }

    /**
     * Método que se ejecuta al destruir la vista del fragmento.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Elimina una sneaker de la base de datos.
     *
     * @param codigoRef El código de referencia de la sneaker a eliminar.
     */
    fun eliminarSneaker(codigoRef: String) {
        val delayMillis = 1500L

        bd.collection("Sneakers").document(codigoRef).delete()
            .addOnSuccessListener {
                Utils.mostrarMensaje(getString(R.string.GestionarSneaker9), binding.root)
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_gestionSneakers_to_pantallaPrincipalSneakerList)
                }, delayMillis)
            }
            .addOnFailureListener {
                Utils.mostrarMensaje(getString(R.string.GestionarSneaker10), binding.root)
            }
    }

}