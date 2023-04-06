package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.linkRecuperarContrasena.setOnClickListener{
            startActivity(Intent(this, RecuperarContrasena::class.java))
            finish()
        }

        binding.linkGoSing.setOnClickListener{
            startActivity(Intent(this, Registro::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}