package cat.copernic.taufik.snkrz.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentGestionSneakersBinding
import cat.copernic.taufik.snkrz.databinding.FragmentInformacionSneakerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_crear_sneaker.*
import kotlinx.android.synthetic.main.fragment_gestion_sneakers.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class GestionSneakers : Fragment() {

    private var _binding: FragmentGestionSneakersBinding? = null
    private val binding get() = _binding!!

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private var storage = FirebaseStorage.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentGestionSneakersBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
    }

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
                mostrarMensaje("La sneaker no se ha guardado, contiene campos vacios!!!")
            }
        }

    }

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

    fun anadirSneaker(snkr: sneaker){
        bd.collection("Sneakers").document(editTextCodRefGestio.text.toString()).set(snkr)
            .addOnSuccessListener { //S'ha modificat la sneaker...
                mostrarMensaje("La sneaker s'ha añadido correctamente")
                findNavController().navigate(R.id.action_gestionSneakers_to_pantallaPrincipalSneakerList)
            }
            .addOnFailureListener { //No s'ha afegit el departament...
                mostrarMensaje("La sneaker no s'ha añadido")
            }
    }

    fun checkEmpty(Sneaker:sneaker): Boolean{
        return Sneaker.NombreSneaker.isNotEmpty() && Sneaker.ModelSneaker.isNotEmpty() &&
                Sneaker.Precio.isNotEmpty() && Sneaker.CodigoReferencia.isNotEmpty() &&
                Sneaker.Descripcion.isNotEmpty() && Sneaker.FechaLanzamiento.isNotEmpty()
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    val fileName = binding.GuardarDatosGestionaSneaker.text.toString() + ".jpg"
                    val imageRef = storageRef.child("imagen/sneaker/").child(fileName)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.await()

                    withContext(Dispatchers.Main) {
                        // Actualizar la imagen de la sneaker en la interfaz de usuario si es necesario
                        mostrarMensaje("Imagen subida correctamente")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun eliminarSneaker(codigoRef: String) {
        bd.collection("Sneakers").document(codigoRef).delete()
            .addOnSuccessListener {
                mostrarMensaje("La sneaker se ha eliminado correctamente")
                findNavController().navigate(R.id.action_gestionSneakers_to_pantallaPrincipalSneakerList)
            }
            .addOnFailureListener {
                mostrarMensaje("No se ha podido eliminar la sneaker")
            }
    }

    fun mostrarMensaje(mensaje: String) {
        val snackbar = Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

}