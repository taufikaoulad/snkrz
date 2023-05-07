package cat.copernic.taufik.snkrz.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.taufik.snkrz.Fragment.PantallaPrincipalSneakerListDirections
import cat.copernic.taufik.snkrz.Model.sneaker
import cat.copernic.taufik.snkrz.databinding.CardLayoutSneakersBinding

class CustomAdapter(val SneakerList:List<sneaker>): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
    // Clase ViewHolder que contiene los elementos de la vista del elemento de lista
    inner class ViewHolder(val binding: CardLayoutSneakersBinding): RecyclerView.ViewHolder(binding.root)

    // Método que se llama cuando se necesita crear una nueva vista para un elemento de lista
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        // Inflar el diseño del elemento de lista
        val binding = CardLayoutSneakersBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    // Método que se llama para vincular los datos a la vista del elemento de lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        with(holder){
            with(SneakerList[position]){

                val maxLength = 18 // Número máximo de caracteres permitidos
                val ellipsis = "..." // Texto de tres puntos

                // Recortar el nombre de la sneaker si excede la longitud máxima y agregar puntos suspensivos
                val nombreSneaker = if (this.NombreSneaker.length > maxLength) {
                    this.NombreSneaker.substring(0, maxLength - ellipsis.length) + ellipsis
                } else {
                    this.NombreSneaker
                }
                binding.edittextNombreSneaker .text = nombreSneaker
                binding.textModeloSneaker.text = this.ModelSneaker
                binding.txtFechaLanzamiento.text = this.FechaLanzamiento

                // Configurar el clic en el elemento del RecyclerView
                binding.CardViewSneaker.setOnClickListener { view ->
                    safeargs(SneakerList.get(position), view) // Pasar los datos de la sneaker al método safeargs
                }
            }
        }
    }

    // Método que devuelve la cantidad de elementos en la lista
    override fun getItemCount(): Int {
        return SneakerList.size
    }

    // Método que se llama cuando se hace clic en el elemento del RecyclerView
    fun safeargs(args: sneaker, view : View){
        // Crear la acción de navegación con los datos de la sneaker
        var action = PantallaPrincipalSneakerListDirections.actionPantallaPrincipalSneakerListToInformacionSneaker(args)
        view.findNavController().navigate(action) // Navegar a la pantalla de información de la sneaker
    }
}