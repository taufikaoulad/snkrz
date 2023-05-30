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
import cat.copernic.taufik.snkrz.databinding.FragmentCrearSneakerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_crear_sneaker.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Clase CrearSneaker que representa un Fragmento para crear una nueva sneaker.
 */
class CrearSneaker : Fragment() {

    private var _binding: FragmentCrearSneakerBinding? = null
    private val binding get() = _binding!!

    private var bd = FirebaseFirestore.getInstance()

    private val storageRef = FirebaseStorage.getInstance().reference
    private var selectedImageUri: Uri? = null

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
        _binding = FragmentCrearSneakerBinding.inflate(inflater, container, false)
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

        binding.btnAddImage.setOnClickListener {
            getContent.launch("*/*")
        }


        binding.CancelarDatosCrearSneaker.setOnClickListener {
            findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
        }

        binding.GuardarDatosCrearSneaker.setOnClickListener {
            val Sneaker: sneaker = llegirDades()

            if (checkEmpty(Sneaker)) {
                anadirSneaker(Sneaker)
            } else {
                Utils.mostrarMensaje(getString(R.string.CrearSneaker1), binding.root)
            }
        }

    }

    /**
     * Método que lee los datos ingresados en los campos de texto y retorna una instancia de sneaker.
     *
     * @return Una instancia de sneaker con los datos ingresados.
     */
    fun llegirDades(): sneaker {
        val codigoRef = binding.editTextCodRefSnkr.text.toString()
        val modeloSneaker = binding.editTextModeloSneaker.text.toString()
        val nombreSneaker = binding.edittextNombreSneaker.text.toString()
        val precio = binding.editTextPrecioCrearSneaker.text.toString()
        val descripcion = binding.editTextdescripcionSneaker.text.toString()

        // Obtenemos la fecha seleccionada del DatePicker
        val datePicker = binding.fechaEvento
        val year = datePicker.year
        val month = datePicker.month + 1
        val day = datePicker.dayOfMonth

        // Construimos la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
        val fechaLanz = String.format("%02d/%02d/%04d", day, month, year)

        return sneaker(
            codigoRef,
            modeloSneaker,
            nombreSneaker,
            precio,
            fechaLanz,
            descripcion,
            null
        )
    }

    /**
     * Método que añade una sneaker a la base de datos.
     *
     * @param snkr La sneaker a añadir.
     */
    fun anadirSneaker(snkr: sneaker) {

        val codigoRef = snkr.CodigoReferencia
        val delayMillis = 2100L

        bd.collection("Sneakers").document(codigoRef).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // La sneaker ya existe en la base de datos
                    Utils.mostrarMensaje(getString(R.string.CrearSneaker2), binding.root)
                } else {
                    if (codigoRef != null) {
                        if (selectedImageUri != null) {
                            bd.collection("Sneakers").document(editTextCodRefSnkr.text.toString())
                                .set(snkr)
                                .addOnSuccessListener {
                                    // Sneaker añadida correctamente
                                    Utils.mostrarMensaje(getString(R.string.CrearSneaker3), binding.root)

                                    // Guardar la imagen en el almacenamiento usando el código de referencia
                                    uploadImage(selectedImageUri!!, codigoRef)
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        // Código para realizar la navegación aquí
                                        findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
                                    }, delayMillis)
                                }
                                .addOnFailureListener {
                                    // Error al añadir la sneaker a Firestore
                                    Utils.mostrarMensaje(getString(R.string.CrearSneaker4), binding.root)
                                }
                        }else{
                            Utils.mostrarMensaje(getString(R.string.CrearSneaker5), binding.root)
                        }
                    } else {
                        Utils.mostrarMensaje(getString(R.string.CrearSneaker6), binding.root)
                    }
                }
            }.addOnFailureListener {
                Utils.mostrarMensaje(getString(R.string.CrearSneaker7), binding.root)
            }
    }

    /**
     * Método que verifica si los campos de la sneaker están vacíos.
     *
     * @param Sneaker La sneaker a verificar.
     * @return true si los campos no están vacíos, false de lo contrario.
     */
    fun checkEmpty(Sneaker: sneaker): Boolean {
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
                Utils.mostrarMensaje(getString(R.string.CrearSneaker8), binding.root)
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
                val imageRef = storageRef.child("imagen/sneaker/").child(codigoRef)
                val uploadTask = imageRef.putFile(uri)
                uploadTask.await()

                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.CrearSneaker9), binding.root)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Utils.mostrarMensaje(getString(R.string.CrearSneaker10), binding.root)
                }
            }
        }
    }
}