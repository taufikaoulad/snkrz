package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.FragmentCrearSneakerBinding


class CrearSneaker : Fragment() {

    private var _binding: FragmentCrearSneakerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        _binding = FragmentCrearSneakerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.CancelarDatosCrearSneaker.setOnClickListener {
            findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
        }

        binding.GuardarDatosCrearSneaker.setOnClickListener {
            findNavController().navigate(R.id.action_crearSneaker_to_pantallaPrincipalSneakerList)
        }

    }

}