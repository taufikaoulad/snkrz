package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cat.copernic.taufik.snkrz.databinding.FragmentPantallaPrincipalSneakerListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.taufik.snkrz.Adapter.CustomAdapter
import cat.copernic.taufik.snkrz.Provider.Provaider


class PantallaPrincipalSneakerList : Fragment() {

    private var _binding: FragmentPantallaPrincipalSneakerListBinding?  = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPantallaPrincipalSneakerListBinding.inflate(inflater,container,false)

        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRecyclerView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = CustomAdapter(Provaider.SneakerList)
    }

}