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

class Registro : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    private var bd = FirebaseFirestore.getInstance()

    private lateinit var auth: FirebaseAuth

    private lateinit var User: Usuario

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

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
                showAlert("El registro a fallado!")
            }
        }

    }

    private fun register(email: String, password: String) {
        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            anadirUsuario(email, password)
                            showAlert("Se ha creado la cuenta con éxito!!!")
                        } else {
                            showAlert("Se ha producido un error registrado al usuario")
                        }
                    }
            } else {
                showAlert("El format de Contrasenya és invalid. " +
                        "La contrasenya ha de contenir entre 6 i 16 valors, una majuscula, una minuscula, " +
                        "un numero i un caracter que no sigui alfanumeric.")
            }
        } else {
            showAlert("El format del Email és invalid")
        }

    }

    //metodo que devuelve un objeto de tipo Usuario, dentro de este se leen de los editText
    private fun llegirDades(Uid: String): Usuario {

        val email = binding.EditTextRegMail.text.toString()
        val nombre = binding.EditTextRegName.text.toString()

        return Usuario(
            Uid,
            email,
            nombre,
            null,
            null,
            null
        )
    }

    private fun anadirUsuario(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    User = llegirDades(email)
                    bd.collection("Usuarios").document(email).set(User)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else {
                    showAlert("El Usuari no s'ha afegit")
                }
            }
    }

    private fun checkEmpty(email: String, password: String, repetirContrasenya: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && repetirContrasenya.isNotEmpty()
    }

    private fun showAlert(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR") //siempre sale error tener en cuenta
        builder.setMessage(mensaje)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    var email_Param = Pattern.compile("^[_a-z0-9]+(.[_a-z0-9]+)*@[a-z0-9]+(.[a-z0-9]+)*(.[a-z]{2,4})\$")
    fun isValidEmail(email: CharSequence?): Boolean {
        return if (email == null) false else email_Param.matcher(email).matches()
    }

    //La contraseña entre 6 y 16 caracteres, al menos un dígito, al menos una minúscula, al menos una mayúscula y al menos un caracter no alfanumérico.
    var password_Param =
        Pattern.compile("^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{6,16}\$")
    fun isValidPassword(password: CharSequence?): Boolean {
        return if (password == null) false else password_Param.matcher(password).matches()
    }
}