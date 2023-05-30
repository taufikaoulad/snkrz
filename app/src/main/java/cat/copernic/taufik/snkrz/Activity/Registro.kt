package cat.copernic.taufik.snkrz.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.Model.Usuario
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.Utils.Utils
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

    private lateinit var binding: ActivityRegistroBinding // Acceso a las vistas de la actividad "Registro".
    private var bd = FirebaseFirestore.getInstance() // Instancia de Firebase Firestore.
    private lateinit var auth: FirebaseAuth // Instancia de FirebaseAuth para autenticación.
    private lateinit var User: Usuario // Objeto Usuario para almacenar información.
    @SuppressLint("MissingInflatedId") // Supresión de advertencia de que no se encuentr un Id inflado de la vista
    /**
     * Método onCreate que se llama cuando se crea la actividad.
     * @param savedInstanceState Objeto Bundle que contiene el estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            if (checkEmpty(email, password, repetirContrasenya)
            ) {
                if(password.equals(repetirContrasenya)){
                    register(email, password)
                }
                else{
                    Utils.showAlert(getString(R.string.Registro1) ,getString(R.string.Registro2), this)
                }
            } else {
                Utils.showAlert(getString(R.string.Registro) ,getString(R.string.Registro4), this)
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
                            Utils.showAlert(getString(R.string.Registro5), getString(R.string.Registro6),this)
                        } else {
                            Utils.showAlert(getString(R.string.Registro7),getString(R.string.Registro8), this)
                        }
                    }
            } else {
                Utils.showAlert(getString(R.string.Registro9), getString(R.string.Registro10), this)
            }
        } else {
            Utils.showAlert(getString(R.string.Registro11),getString(R.string.Registro12), this)
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
                    Utils.showAlert(getString(R.string.Registro13),getString(R.string.Registro14), this)
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
     * //Pattern.compile("^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{6,16}\$")
     */
    var password_Param =
        Pattern.compile("^\\S{6,16}\$")
    fun isValidPassword(password: CharSequence?): Boolean {
        return if (password == null) false else password_Param.matcher(password).matches()
    }
}