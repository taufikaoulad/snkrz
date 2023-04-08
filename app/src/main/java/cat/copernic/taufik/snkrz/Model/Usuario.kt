package cat.copernic.taufik.snkrz.Model

data class Usuario (
    val ID: String,
    val Email : String,
    val Nombre : String?,
    val Apellido : String?,
    val DNI : String? ,
    val Telefono : Int? )
