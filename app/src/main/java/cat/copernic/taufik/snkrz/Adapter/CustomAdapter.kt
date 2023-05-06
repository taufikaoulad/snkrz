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
    inner class ViewHolder(val binding: CardLayoutSneakersBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val binding = CardLayoutSneakersBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        with(holder){
            with(SneakerList[position]){

                val maxLength = 18 // Número máximo de caracteres permitidos
                val ellipsis = "..." // Texto de tres puntos

                val nombreSneaker = if (this.NombreSneaker.length > maxLength) {
                    this.NombreSneaker.substring(0, maxLength - ellipsis.length) + ellipsis
                } else {
                    this.NombreSneaker
                }
                binding.textNombreSneaker.text = nombreSneaker
                binding.textModeloSneaker.text = this.ModelSneaker
                binding.txtFechaLanzamiento.text = this.FechaLanzamiento

                // Configurar el clic en el elemento del RecyclerView
                binding.CardViewSneaker.setOnClickListener { view ->
                    safeargs(SneakerList.get(position), view)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return SneakerList.size
    }

    fun safeargs(args: sneaker, view : View){
        var action = PantallaPrincipalSneakerListDirections.actionPantallaPrincipalSneakerListToInformacionSneaker(args)
        view.findNavController().navigate(action)
    }
}