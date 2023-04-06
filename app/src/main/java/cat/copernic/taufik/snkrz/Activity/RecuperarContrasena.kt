package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.ActivityRecuperarContrasenaBinding
import cat.copernic.taufik.snkrz.databinding.ActivityRegistroBinding

class RecuperarContrasena : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperarContrasenaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecuperarContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.volverLogin.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}