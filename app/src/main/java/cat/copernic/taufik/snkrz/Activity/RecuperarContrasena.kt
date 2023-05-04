package cat.copernic.taufik.snkrz.Activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.ActivityRecuperarContrasenaBinding
import cat.copernic.taufik.snkrz.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecuperarContrasena : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarContrasenaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth
        //Ocultar la app bar
        supportActionBar?.hide()

        binding.volverLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        binding.btnRecuperarContrasena.setOnClickListener {

            var correu = binding.RecNombre.text.toString().replace(" ", "")

            if (correu.isNotEmpty()) {

                restaurarContrasenya(correu)
            }

        }
    }

    private fun restaurarContrasenya(correu: String) {
        auth.setLanguageCode("es")

        auth.sendPasswordResetEmail(correu).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                showAlert("Contrasenya restaurada amb èxit. Rebràs un correu.", "Creación Exitosa")
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }, 2000) // espera de 2 segundos antes de cambiar a la otra pantalla
            } else {
                showAlert("No s'ha pogut restaurar la contrasenya!!", "ERROR")
            }
        }
    }

    private fun showAlert(mensaje: String, mensaje2: String) {
        if (!isFinishing()) {
            val toast = Toast.makeText(this, mensaje, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}