package cat.copernic.taufik.snkrz.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Clase que representa una sneaker.
 *
 * @property CodigoReferencia El código de referencia de la sneaker.
 * @property NombreSneaker El nombre de la sneaker.
 * @property ModelSneaker El modelo de la sneaker.
 * @property Precio El precio de la sneaker.
 * @property FechaLanzamiento La fecha de lanzamiento de la sneaker.
 * @property Descripcion La descripción de la sneaker.
 * @property meGusta La lista de "me gusta" asociados a la sneaker.
 */
@Parcelize //la classe sneaker se puede convertir en un objeto parceable, esto permite transferir objetos complejos entre componentes
data class sneaker (
    val CodigoReferencia: String,
    val NombreSneaker: String,
    val ModelSneaker: String,
    val Precio: String,
    val FechaLanzamiento: String,
    val Descripcion: String,
    val meGusta: @RawValue List<meGusta>? = null) : Parcelable