package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import cat.copernic.taufik.snkrz.databinding.FragmentInformacionSneakerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_crear_sneaker.*


class InformacionSneaker : Fragment() {

    private var _binding: FragmentInformacionSneakerBinding? = null
    private val binding get() = _binding!!

    private val args: InformacionSneakerArgs by navArgs()

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

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

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        meGustaDocument = bd.collection("Sneakers")
            .document(args.sneaker.CodigoReferencia)
            .collection("MeGusta")
            .document(auth.currentUser?.uid.toString())

        // Verificar si el usuario actual ya dio "Me gusta"
        meGustaDocument.get().addOnSuccessListener { documentSnapshot ->
            meGusta = documentSnapshot.exists()
        }

        binding.likeBtn.setOnClickListener {
            if (meGusta) {
                // El usuario ya ha dado "Me gusta", así que lo quitamos
                meGustaDocument.delete().addOnSuccessListener {
                    meGusta = false
                    Toast.makeText(requireContext(), "Me gusta eliminado", Toast.LENGTH_SHORT).show()
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
                    }.addOnFailureListener { exception ->
                        // Error al agregar, maneja el error apropiadamente
                        Toast.makeText(requireContext(), "Error al agregar el Me gusta", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}