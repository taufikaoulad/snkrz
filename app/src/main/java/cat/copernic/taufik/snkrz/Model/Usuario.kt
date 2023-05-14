package cat.copernic.taufik.snkrz.Model

/**
 * Clase que representa un usuario.
 *
 * @property ID El ID del usuario.
 * @property Email El correo electrónico del usuario.
 * @property Nombre El nombre del usuario.
 * @property Apellido El apellido del usuario.
 * @property DNI El número de DNI del usuario.
 * @property Telefono El número de teléfono del usuario.
 * @property esAdmin Indica si el usuario es administrador o no. Por defecto es falso.
 */
data class Usuario (
    val ID: String,
    val Email : String,
    val Nombre : String?,
    val Apellido : String?,
    val DNI : String? ,
    val Telefono : Int?,
    val esAdmin: Boolean = false)
