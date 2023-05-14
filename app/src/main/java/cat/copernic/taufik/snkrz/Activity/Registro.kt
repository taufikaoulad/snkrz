package cat.copernic.taufik.snkrz.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.Model.Usuario
import cat.copernic.taufik.snkrz.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

/**
 * Clase Registro que extiende AppCompatActivity.
 */
class Registro : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    private var bd = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    private lateinit var User: Usuario

    @SuppressLint("MissingInflatedId")
    /**
     * Método onCreate que se llama cuando se crea la actividad.
     * @param savedInstanceState Objeto Bundle que contiene el estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //Ocultar la app bar
        supportActionBar?.hide()


        binding.linkGoLog.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        binding.btnRegistro.setOnClickListener{

            val email = binding.EditTextRegMail.text.toString()
            val password = binding.EditTextRegPassw.text.toString()
            val repetirContrasenya = binding.EditTextRegPasswRep.text.toString()

            //Sentencia if para verificar que el paswword es igual al password que ponemos al repetir la contraseña
            //Tambien verifica que los campos no esten bacios
            //en caso contrario mustra el mensaje del fallo (else)
            if (password.equals(repetirContrasenya) && checkEmpty(email, password, repetirContrasenya)
            ) {
                register(email, password)
            } else {
                showAlert("El email, la contraseña y la confirmacion de la contraseña, no pueden ser campos vacíos. " ,"ERROR")
            }
        }

    }

    /**
     * Método para registrar un nuevo usuario.
     * @param email Dirección de correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    private fun register(email: String, password: String) {
        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            anadirUsuario(email, password)
                            showAlert("Se ha creado la cuenta con éxito!!!", "Creación Exitosa")
                        } else {
                            showAlert("Se ha producido un error registrado al usuario","ERROR")
                        }
                    }
            } else {
                showAlert("El format de Contrasenya és invalid. " +
                        "La contrasenya ha de contenir entre 6 i 16 valors, una majuscula, una minuscula, " +
                        "un numero i un caracter que no sigui alfanumeric.", "ERROR")
            }
        } else {
            showAlert("El format del Email és invalid","ERROR")
        }

    }

    /**
     * Método para leer los datos del usuario.
     * @param Uid Identificador único del usuario.
     * @return Objeto de tipo Usuario con los datos leídos.
     */
    private fun llegirDades(Uid: String): Usuario {

        val email = binding.EditTextRegMail.text.toString()
        val nombre = binding.EditTextRegName.text.toString()

        return Usuario(
            Uid,
            email,
            nombre,
            null,
            null,
            null,
            false
        )
    }

    /**
     * Método para añadir un usuario a la base de datos.
     * @param email Dirección de correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    private fun anadirUsuario(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    User = llegirDades(email)
                    bd.collection("Usuarios").document(email).set(User)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else {
                    showAlert("El Usuari no s'ha afegit","ERROR")
                }
            }
    }


    /**
     * Método para verificar si los campos de email, contraseña y repetir contraseña no están vacíos.
     * @param email Dirección de correo electrónico.
     * @param password Contraseña.
     * @param repetirContrasenya Confirmación de la contraseña.
     * @return true si los campos no están vacíos, false de lo contrario.
     */
    private fun checkEmpty(email: String, password: String, repetirContrasenya: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && repetirContrasenya.isNotEmpty()
    }


    /**
     * Método para mostrar una alerta con un mensaje.
     * @param mensaje Mensaje a mostrar en la alerta.
     * @param mensaje2 Título de la alerta.
     */
    private fun showAlert(mensaje: String, mensaje2: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(mensaje2) //siempre sale error tener en cuenta
        builder.setMessage(mensaje)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    /**
     * Método para verificar si una dirección de correo electrónico es válida.
     * @param email Dirección de correo electrónico a verificar.
     * @return true si el email es válido, false de lo contrario.
     */
    var email_Param = Pattern.compile("^[_a-z0-9]+(.[_a-z0-9]+)*@[a-z0-9]+(.[a-z0-9]+)*(.[a-z]{2,4})\$")
    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email == null) false else email_Param.matcher(email).matches()
    }

    /**
     * Método para verificar si una contraseña cumple con los requisitos.
     * La contraseña debe tener entre 6 y 16 caracteres, al menos un dígito,
     * al menos una minúscula, al menos una mayúscula y al menos un caracter no alfanumérico.
     * @param password Contraseña a verificar.
     * @return true si la contraseña es válida, false de lo contrario.
     */
    var password_Param =
        Pattern.compile("^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{6,16}\$")
    fun isValidPassword(password: CharSequence?): Boolean {
        return if (password == null) false else password_Param.matcher(password).matches()
    }
}