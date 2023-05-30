package cat.copernic.taufik.snkrz.Activity

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.Utils.Utils
import cat.copernic.taufik.snkrz.Utils.Utils.Companion.showAlert
import cat.copernic.taufik.snkrz.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Clase Login que extiende AppCompatActivity.
 */
class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    /**
     * Método onCreate que se llama cuando se crea la actividad.
     * @param savedInstanceState Objeto Bundle que contiene el estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Ocultar la app bar
        supportActionBar?.hide()

        binding.linkRecuperarContrasena.setOnClickListener{
            startActivity(Intent(this, RecuperarContrasena::class.java))
            finish()
        }

        binding.linkGoSing.setOnClickListener{
            startActivity(Intent(this, Registro::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener{
            val email = binding.LoginEmail.text.toString().replace( " ","")
            val password = binding.LoginPassword.text.toString().replace( " ","")
            if(checkEmpty(email, password)){
                login(email, password)
            }else{
                Utils.showAlert2(getString(R.string.LoginTexto1), this)
            }
        }

    }

    /**
     * Método para realizar el inicio de sesión.
     * @param email Dirección de correo electrónico.
     * @param password Contraseña.
     */
    private fun login(email: String, password: String){

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Utils.showAlert2(getString(R.string.LoginTexto2), this)
                    }
                }
    }

    /**
     * Método para verificar si el email y la contraseña no están vacíos.
     * @param email Dirección de correo electrónico.
     * @param password Contraseña.
     * @return Devuelve true si tanto el email como la contraseña no están vacíos, de lo contrario, devuelve false.
     */
    private fun checkEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Método onStart que se llama cuando la actividad se vuelve visible para el usuario.
     */
    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}