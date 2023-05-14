package cat.copernic.taufik.snkrz.Model

/**
 * Clase de datos que representa un "me gusta" dado por un usuario.
 *
 * @param correoUsuario El correo del usuario que ha dado el "me gusta".
 *                      El ID se considerará el UID del usuario autenticado.
 */
data class meGusta(
    val correoUsuario: String) //El id sera el uid del usuario Autentificado