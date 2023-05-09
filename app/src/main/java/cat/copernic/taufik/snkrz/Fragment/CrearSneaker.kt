package cat.copernic.taufik.snkrz.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentCrearSneakerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_crear_sneaker.*
import kotlinx.android.synthetic.main.fragment_crear_sneaker.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException


class CrearSneaker : Fragment() {

    private var _binding: FragmentCrearSneakerBinding? = null
    private val binding get() = _binding!!

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    val user = Firebase.auth.currentUser

    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var imageUri: Uri

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _binding = FragmentCrearSneakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnAddImage.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.eliminarDatosCrearSneaker.setOnClickListener {
            val codigoRef = binding.editTextCodRefSnkr.text.toString()
            eliminarSneaker(codigoRef)
            findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
        }

        binding.CancelarDatosCrearSneaker.setOnClickListener {
            findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
        }

        binding.GuardarDatosCrearSneaker.setOnClickListener {

            val Sneaker : sneaker = llegirDades()
            if (checkEmpty(Sneaker)){
                anadirSneaker(Sneaker)
            }else{
                mostrarMensaje("La sneaker no se ha guardado, contiene campos vacios!!!")
            }
        }

    }

    fun llegirDades(): sneaker{
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

        return sneaker(codigoRef, modeloSneaker, nombreSneaker, precio, fechaLanz, descripcion, null)
    }

    fun anadirSneaker(snkr: sneaker){
        bd.collection("Sneakers").document(editTextCodRefSnkr.text.toString()).set(snkr)
            .addOnSuccessListener { //S'ha modificat la sneaker...
                mostrarMensaje("La sneaker s'ha añadido correctamente")
                findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
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

    fun eliminarSneaker(codigoRef: String) {
        bd.collection("Sneakers").document(codigoRef).delete()
            .addOnSuccessListener {
                mostrarMensaje("La sneaker se ha eliminado correctamente")
            }
            .addOnFailureListener {
                mostrarMensaje("No se ha podido eliminar la sneaker")
            }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    val fileName = binding.editTextCodRefSnkr.text.toString() + ".jpg"
                    val imageRef = storageRef.child("imagen/sneaker/").child(fileName)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.await()

                    withContext(Dispatchers.Main) {
                        // Actualizar la imagen de la sneaker en la interfaz de usuario si es necesario
                        Toast.makeText(requireContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun mostrarMensaje(mensaje: String) {
        val snackbar = Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

}