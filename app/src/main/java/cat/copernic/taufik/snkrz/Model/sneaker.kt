package cat.copernic.taufik.snkrz.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class sneaker (
    val NombreSneaker: String,
    val ModelSneaker: String,
    val Precio: String,
    val CodigoReferencia: String,
    val FechaLanzamiento: String,
    val Descripcion: String) : Parcelable