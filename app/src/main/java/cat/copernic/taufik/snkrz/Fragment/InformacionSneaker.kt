package cat.copernic.taufik.snkrz.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentInformacionSneakerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_informacion_sneaker.*
import kotlinx.android.synthetic.main.fragment_informacion_sneaker.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class InformacionSneaker : Fragment() {

    private var _binding: FragmentInformacionSneakerBinding? = null
    private val binding get() = _binding!!

    private val args: InformacionSneakerArgs by navArgs()

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private val storageRef = FirebaseStorage.getInstance().reference

    private var meGusta = false
    private lateinit var meGustaDocument: DocumentReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentInformacionSneakerBinding.inflate(inflater, container, false)

        binding.edittextNombreSneaker.setText(args.sneaker.NombreSneaker)
        binding.sneakerModel.setText(args.sneaker.ModelSneaker)
        binding.fechaLanzamiento.setText(args.sneaker.FechaLanzamiento)
        binding.precio.setText(args.sneaker.Precio)
        binding.editTextdescripcionSneaker.setText(args.sneaker.Descripcion)
        binding.Identificador.setText(args.sneaker.CodigoReferencia)

        auth = Firebase.auth

        var adrecaImatge = storageRef.child("imagen/sneaker/${args.sneaker.CodigoReferencia}.jpg")
        var fitxerTemporal = File.createTempFile("temp",null)

        adrecaImatge.getFile(fitxerTemporal).addOnSuccessListener { //La carrega a tingut èxit..

            val mapaBits = BitmapFactory.decodeFile(fitxerTemporal.absolutePath)

            binding.imagenSneaker.setImageBitmap(mapaBits)

        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //Codigo para recupear el valor esAdmin para luego gestionar las vistas
        val email = FirebaseAuth.getInstance().currentUser!!.email
        lifecycleScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    bd.collection("Usuarios").document(email.toString()).get().await()
                }
                val data = snapshot.data
                if (data != null) {

                    val esAdmin = data["esAdmin"] as? Boolean ?: false

                    //gestionamos la vista, solo el admin podra acceder a Gestion de Senakers
                    if (esAdmin == true) {
                        // Si esAdmin es verdadero, mostrar el ImageView
                        binding.cardViewInfromacion3.visibility = View.VISIBLE
                    } else {
                        // Si esAdmin es falso, ocultar el ImageView
                        binding.cardViewInfromacion3.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        meGustaDocument = bd.collection("Sneakers")
            .document(args.sneaker.CodigoReferencia)
            .collection("MeGusta")
            .document(auth.currentUser?.uid.toString())


        meGustaDocument.get().addOnSuccessListener { documentSnapshot ->
            meGusta = documentSnapshot.exists()
            obtenerNumeroMeGustas() // Obtener el número de "me gusta" inicial
        }

        binding.btnGestionarSneaker.setOnClickListener {
            val action = InformacionSneakerDirections.actionInformacionSneakerToGestionSneakers(args.sneaker)
            findNavController().navigate(action)
        }

        binding.likeBtn.setOnClickListener {
            if (meGusta) {
                // El usuario ya ha dado "Me gusta", así que lo quitamos
                meGustaDocument.delete().addOnSuccessListener {
                    meGusta = false
                    Toast.makeText(requireContext(), "Me gusta eliminado", Toast.LENGTH_SHORT).show()
                    obtenerNumeroMeGustas()
                }.addOnFailureListener { exception ->
                    // Error al eliminar, maneja el error apropiadamente
                    Toast.makeText(requireContext(), "Error al eliminar el Me gusta", Toast.LENGTH_SHORT).show()
                }
            } else {
                // El usuario no ha dado "Me gusta", así que lo agregamos
                meGustaDocument.set(hashMapOf("Email" to auth.currentUser?.email.toString()))
                    .addOnSuccessListener {
                        meGusta = true
                        Toast.makeText(requireContext(), "Me gusta agregado", Toast.LENGTH_SHORT).show()
                        obtenerNumeroMeGustas()
                    }.addOnFailureListener { exception ->
                        // Error al agregar, maneja el error apropiadamente
                        Toast.makeText(requireContext(), "Error al agregar el Me gusta", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun obtenerNumeroMeGustas() {
        bd.collection("Sneakers")
            .document(args.sneaker.CodigoReferencia)
            .collection("MeGusta")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meGustasSize = querySnapshot.size()
                binding.numeroDeMeGustas.text = meGustasSize.toString()
            }
            .addOnFailureListener { exception ->
                // Manejar el error de consulta apropiadamente
                //Toast.makeText(requireContext(), "Error al obtener el número de Me gusta", Toast.LENGTH_SHORT).show()
            }
    }

}