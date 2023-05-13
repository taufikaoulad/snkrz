package cat.copernic.taufik.snkrz.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class Perfil : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnEditarPerfil.setOnClickListener {
            findNavController().navigate(R.id.action_perfil_to_editarPerfil)
        }

        carregarImatge()

        val email = FirebaseAuth.getInstance().currentUser!!.email

        lifecycleScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    db.collection("Usuarios").document(email.toString()).get().await()
                }
                val data = snapshot.data
                if (data != null) {
                    val email = data["email"]?.toString()
                    val nombre = data["nombre"]?.toString()
                    val apellido = data["apellido"]?.toString()
                    val dni = data["dni"]?.toString()
                    val telefono = data["telefono"]?.toString()

                    binding.txtNombreUser.text = nombre.toString()
                    binding.txtApellidosUser.text = apellido.toString()
                    binding.txtDniPerfil.text = dni.toString()
                    binding.txtTelefonoNumeroPerfil.text = telefono.toString()
                    binding.txtMailPerfil.text = email.toString()
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

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
                    //Toast.makeText(context, "La carrega de la imatge ha fallat", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}