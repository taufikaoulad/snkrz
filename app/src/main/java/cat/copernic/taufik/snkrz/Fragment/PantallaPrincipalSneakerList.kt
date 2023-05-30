package cat.copernic.taufik.snkrz.Fragment


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import cat.copernic.taufik.snkrz.databinding.FragmentPantallaPrincipalSneakerListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import cat.copernic.taufik.snkrz.Adapter.CustomAdapter
import cat.copernic.taufik.snkrz.Model.meGusta
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.android.synthetic.main.fragment_informacion_sneaker.*


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

    private lateinit var sharedPreferences: SharedPreferences
    private var isNotificationShown: Boolean = false

    /**
     * Método que se llama al crear la vista del fragmento.
     *
     * @param inflater El LayoutInflater utilizado para inflar la vista.
     * @param container El contenedor padre en el que se infla la vista.
     * @param savedInstanceState El estado previamente guardado del fragmento.
     * @return La vista inflada del fragmento.
     */
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentPantallaPrincipalSneakerListBinding.inflate(inflater, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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


        sharedPreferences = requireContext().getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        isNotificationShown = sharedPreferences.getBoolean("notification_shown", false)

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
     * Escucha los cambios en la colección "Sneakers" y actualiza la lista de elementos y la vista correspondiente.
     * También muestra una notificación para los elementos nuevos que tienen la misma fecha de lanzamiento actual.
     */
    private fun listenForDataChanges() {
        val dataChangeU = bd.collection("Sneakers")
        dataChangeU.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            val newItems = mutableListOf<sneaker>()

            for (dc in snapshot!!.documentChanges) {
                val document = dc.document
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
                    newItems.add(wallItem)
                }
            }

            if (isRecyclerViewInitialized) {
                listMutable.addAll(newItems)
                adapter.notifyDataSetChanged()

                val currentDate = getCurrentDate()

                for (item in newItems) {
                    if (currentDate == item.FechaLanzamiento) {
                        showNotification(item.NombreSneaker, item.ModelSneaker)
                    }
                }
            }
        }
    }

    /**
     * Obtiene la fecha actual en el formato "dd/MM/yyyy".
     *
     * @return La fecha actual como una cadena de texto.
     */
    private fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    /**
     * Muestra una notificación con el nombre y modelo de la sneaker.
     *
     * @param nombreSneaker El nombre de la sneaker.
     * @param modeloSneaker El modelo de la sneaker.
     */
    private fun showNotification(nombreSneaker: String, modeloSneaker: String) {
        val channelId = "sneaker_channel"
        val notificationId = 1

        val context = requireContext().applicationContext // Obtén el contexto de la aplicación

        // Crear un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sneaker Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(getString(R.string.PantallaPrincipalSneakerList1))
            .setContentText("¡Hoy se realiza el lanzamiento de las $nombreSneaker $modeloSneaker!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Mostrar la notificación
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
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