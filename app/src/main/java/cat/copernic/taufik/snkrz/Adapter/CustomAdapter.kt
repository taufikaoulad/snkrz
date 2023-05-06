package cat.copernic.taufik.snkrz.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.taufik.snkrz.Model.Sneaker
import cat.copernic.taufik.snkrz.databinding.CardLayoutSneakersBinding

class CustomAdapter(val SneakerList:List<Sneaker>): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
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
                binding.imagenSneaker.setImageResource(this.imagenSneaker)
            }
        }
    }

    override fun getItemCount(): Int {
        return SneakerList.size
    }
}