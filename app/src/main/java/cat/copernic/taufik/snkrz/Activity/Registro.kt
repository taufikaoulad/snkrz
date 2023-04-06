package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.databinding.ActivityLoginBinding
import cat.copernic.taufik.snkrz.databinding.ActivityRegistroBinding

class Registro : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.linkGoLog.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        binding.btnRegistro.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}