package cat.copernic.taufik.snkrz.Fragment

import android.graphics.Bitmap
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
import cat.copernic.taufik.snkrz.Model.Usuario
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentEditarPerfilBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.IOException

class EditarPerfil : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    private val storageRef = FirebaseStorage.getInstance().reference

    val email = Firebase.auth.currentUser?.email
    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    val user = Firebase.auth.currentUser


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.GuardarDatosPerfil.setOnClickListener {

            if (user != null) {
                //Modifiquem el usuari mitjançant la funció modificarUsuario creada per nosaltres
                modificarUsuario(llegirDades())

            } else {
                mostrarMensaje("No se ha podido modificar el perfil")
            }
        }

        binding.CancelarDatosPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_editarPerfil_to_perfil)
        }

        binding.txtCambiarFoto.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun llegirDades(): Usuario {
        var Email = user?.email.toString()
        var nombre = binding.NombreEditarPerfil.text.toString()
        var apellido = binding.ApellidoEditarPerfil.text.toString()
        var dni = binding.DNIEditarPerfil.text.toString()
        var telefono = binding.TelefonoEditarPerfil.text.toString().toInt()

        return Usuario(email.toString(), Email, nombre, apellido, dni, telefono)
    }

    fun modificarUsuario(user: Usuario) {
        if (email != null) {
            bd.collection("Usuarios").document(email).set(user)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_editarPerfil_to_perfil)
                    mostrarMensaje("El perfil se ha modificado correctamente")
                }
                .addOnFailureListener{
                    mostrarMensaje("El perfil no se ha modificado")
                }
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    }

                    val user = auth.currentUser
                    val fileName = "imagen_perfil.jpg"
                    val imageRef = storageRef.child("imagene/perfil/${user?.uid}/$fileName")
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.await()

                    withContext(Dispatchers.Main) {
                        binding.imgPerfilPerfil.setImageBitmap(bitmap)
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