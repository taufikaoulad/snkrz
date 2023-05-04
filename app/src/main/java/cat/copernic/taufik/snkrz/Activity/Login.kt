package cat.copernic.taufik.snkrz.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
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
                showAlert("El email y contraseña no pueden ser campos vacíos. ")
            }
        }

    }

    private fun login(email: String, password: String){

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        showAlert("Se ha producido un error autenticando al usuario. Comprueba que el email o la contraseña sean correctos.")
                    }
                }
    }

    private fun checkEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR") //siempre sale error tener en cuenta
        builder.setMessage(mensaje)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}