package cat.copernic.taufik.snkrz.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize //la classe sneaker se puede convertir en un objeto parceable, esto permite transferir objetos complejos entre componentes
data class sneaker (
    val CodigoReferencia: String,
    val NombreSneaker: String,
    val ModelSneaker: String,
    val Precio: String,
    val FechaLanzamiento: String,
    val Descripcion: String) : Parcelable