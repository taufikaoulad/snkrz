package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import cat.copernic.taufik.snkrz.databinding.FragmentPantallaPrincipalSneakerListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.taufik.snkrz.Adapter.CustomAdapter
import cat.copernic.taufik.snkrz.Provider.Provaider
import cat.copernic.taufik.snkrz.R
import kotlinx.android.synthetic.main.card_layout_sneakers.view.*


class PantallaPrincipalSneakerList : Fragment() {

    private var _binding: FragmentPantallaPrincipalSneakerListBinding?  = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPantallaPrincipalSneakerListBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView(view)
    }

    private fun initRecyclerView(view: View){
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = CustomAdapter(Provaider.SneakerList)

    }
}