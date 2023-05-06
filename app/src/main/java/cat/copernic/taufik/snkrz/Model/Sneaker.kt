package cat.copernic.taufik.snkrz.Model

import java.time.LocalDate

data class Sneaker (
    val NombreSneaker: String,
    val ModelSneaker: String,
    val Precio: String,
    val CodigoReferencia: String,
    val FechaLanzamiento: String,
    val Descripcion: String,
    val imagenSneaker: Int)