package cat.copernic.taufik.snkrz.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import cat.copernic.taufik.snkrz.databinding.FragmentPantallaPrincipalSneakerListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.taufik.snkrz.Adapter.CustomAdapter
import cat.copernic.taufik.snkrz.Model.meGusta
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.Provider.list_sneakers
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PantallaPrincipalSneakerList : Fragment() {

    private var _binding: FragmentPantallaPrincipalSneakerListBinding? = null
    private val binding get() = _binding!!
    private val bd = FirebaseFirestore.getInstance()
    private val listMutable: MutableList<sneaker> = mutableListOf()
    private lateinit var adapter: CustomAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPantallaPrincipalSneakerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CustomAdapter(listMutable)
        initRecyclerView()
        listenForDataChanges()
    }

    private fun initRecyclerView() {
        if (listMutable.isEmpty()) {
            recycleServicios()
        } else {
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun listenForDataChanges() {
        val dataChangeU = bd.collection("Sneakers")
        dataChangeU.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (dc in snapshot!!.documentChanges) {
                listMutable.clear()
                recycleServicios()
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun recycleServicios() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                bd.collection("Sneakers").get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val wallItem = sneaker(
                            CodigoReferencia = document["codigoReferencia"] as? String ?: "",
                            NombreSneaker = document["nombreSneaker"] as? String ?: "",
                            ModelSneaker = document["modelSneaker"] as? String ?: "",
                            Precio = document["precio"] as? String ?: "",
                            FechaLanzamiento = document["fechaLanzamiento"] as? String ?: "",
                            Descripcion = document["descripcion"] as? String ?: "",
                            meGusta = document["MeGusta"] as? List<meGusta> ?: emptyList()
                        )
                        if (listMutable.none { it.CodigoReferencia == wallItem.CodigoReferencia }) {
                            listMutable.add(wallItem)
                        }
                    }
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = adapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}