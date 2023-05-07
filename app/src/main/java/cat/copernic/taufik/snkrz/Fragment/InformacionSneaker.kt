package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import cat.copernic.taufik.snkrz.databinding.FragmentInformacionSneakerBinding



class InformacionSneaker : Fragment() {

    private var _binding: FragmentInformacionSneakerBinding? = null
    private val binding get() = _binding!!

    private val args: InformacionSneakerArgs by navArgs()

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
}