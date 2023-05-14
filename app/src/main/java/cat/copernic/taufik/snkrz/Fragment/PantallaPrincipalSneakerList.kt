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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Fragmento que muestra la información de una sneakers.
 */
class PantallaPrincipalSneakerList : Fragment() {

    private var _binding: FragmentPantallaPrincipalSneakerListBinding? = null
    private val binding get() = _binding!!
    private val bd = FirebaseFirestore.getInstance()
    private val listMutable: MutableList<sneaker> = mutableListOf()
    private lateinit var adapter: CustomAdapter
    private var isRecyclerViewInitialized = false

    /**
     * Método que se llama al crear la vista del fragmento.
     *
     * @param inflater El LayoutInflater utilizado para inflar la vista.
     * @param container El contenedor padre en el que se infla la vista.
     * @param savedInstanceState El estado previamente guardado del fragmento.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPantallaPrincipalSneakerListBinding.inflate(inflater, container, false)
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
        adapter = CustomAdapter(listMutable)
        initRecyclerView()
        listenForDataChanges()
    }

    /**
     * Inicializa el RecyclerView con su respectivo LayoutManager y Adapter.
     */
    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        isRecyclerViewInitialized = true
    }

    /**
     * Escucha los cambios en los datos de la colección "Sneakers" y actualiza la lista de elementos en el RecyclerView.
     */
    private fun listenForDataChanges() {
        val dataChangeU = bd.collection("Sneakers")
        dataChangeU.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            for (dc in snapshot!!.documentChanges) {
                if (isRecyclerViewInitialized) {
                    listMutable.clear()
                    recycleServicios()
                }
            }
        }
    }

    /**
     * Obtiene los documentos de la colección "Sneakers" y actualiza la lista de elementos en el RecyclerView.
     */
    private fun recycleServicios() {
        lifecycleScope.launch(Dispatchers.IO) {
            val documents = withContext(Dispatchers.IO) {
                bd.collection("Sneakers").get().await()
            }
            val newItems = mutableListOf<sneaker>()
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
            withContext(Dispatchers.Main) {
                listMutable.addAll(newItems)
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Limpia la vista y libera los recursos cuando se destruye el fragmento.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}