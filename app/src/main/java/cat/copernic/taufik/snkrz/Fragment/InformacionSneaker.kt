package cat.copernic.taufik.snkrz.Fragment

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import cat.copernic.taufik.snkrz.Utils.Utils
import kotlinx.android.synthetic.main.fragment_informacion_sneaker.*
import kotlinx.android.synthetic.main.fragment_informacion_sneaker.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Fragmento que muestra la información de una sneaker específica.
 */
class InformacionSneaker : Fragment() {

    private var _binding: FragmentInformacionSneakerBinding? = null
    private val binding get() = _binding!!

    private val args: InformacionSneakerArgs by navArgs()

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private val storageRef = FirebaseStorage.getInstance().reference

    private var meGusta = false
    private lateinit var meGustaDocument: DocumentReference


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
        _binding = FragmentInformacionSneakerBinding.inflate(inflater, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.edittextNombreSneaker.setText(args.sneaker.NombreSneaker)
        binding.sneakerModel.setText(args.sneaker.ModelSneaker)
        binding.fechaLanzamiento.setText(args.sneaker.FechaLanzamiento)
        binding.precio.setText(args.sneaker.Precio)
        binding.editTextdescripcionSneaker.setText(args.sneaker.Descripcion)
        binding.Identificador.setText(args.sneaker.CodigoReferencia)

        auth = Firebase.auth

        var adrecaImatge = storageRef.child("imagen/sneaker/${args.sneaker.CodigoReferencia}")
        var fitxerTemporal = File.createTempFile("temp",null)

        adrecaImatge.getFile(fitxerTemporal).addOnSuccessListener { //La carrega a tingut èxit..

            val mapaBits = BitmapFactory.decodeFile(fitxerTemporal.absolutePath)

            binding.imagenSneaker.setImageBitmap(mapaBits)

        }

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
                //Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_LONG).show()
                //Utils.mostrarMensaje(getString(R.string.fail), binding.root)
            }
        }

        meGustaDocument = bd.collection("Sneakers")
            .document(args.sneaker.CodigoReferencia)
            .collection("MeGusta")
            .document(auth.currentUser?.uid.toString())


        meGustaDocument.get().addOnSuccessListener { documentSnapshot ->
            meGusta = documentSnapshot.exists()
            obtenerNumeroMeGustas() // Obtener el número de "me gusta" inicial

            if (meGusta) {
                binding.likeBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                binding.likeBtn.clearColorFilter()
            }
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
                    Utils.mostrarMensaje(getString(R.string.InformacionSneaker1), binding.root)
                    //Toast.makeText(requireContext(), "Me gusta eliminado", Toast.LENGTH_SHORT).show()
                    obtenerNumeroMeGustas()
                    binding.likeBtn.clearColorFilter()
                }.addOnFailureListener { exception ->
                    // Error al eliminar, maneja el error apropiadamente
                    Utils.mostrarMensaje(getString(R.string.InformacionSneaker2), binding.root)
                    //Toast.makeText(requireContext(), "Error al eliminar el Me gusta", Toast.LENGTH_SHORT).show()
                }
            } else {
                // El usuario no ha dado "Me gusta", así que lo agregamos
                meGustaDocument.set(hashMapOf("Email" to auth.currentUser?.email.toString()))
                    .addOnSuccessListener {
                        meGusta = true
                        Utils.mostrarMensaje(getString(R.string.InformacionSneaker3), binding.root)
                        //Toast.makeText(requireContext(), "Me gusta agregado", Toast.LENGTH_SHORT).show()
                        obtenerNumeroMeGustas()
                        binding.likeBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red)) // Aplicar el filtro de color rojo
                    }.addOnFailureListener { exception ->
                        // Error al agregar, maneja el error apropiadamente
                        Utils.mostrarMensaje(getString(R.string.InformacionSneaker4), binding.root)
                        //Toast.makeText(requireContext(), "Error al agregar el Me gusta", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    /**
     * Método para obtener el número total de "me gusta" de la sneaker actual.
     */
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
                Utils.mostrarMensaje(getString(R.string.InformacionSneaker5), binding.root)
            }
    }
}