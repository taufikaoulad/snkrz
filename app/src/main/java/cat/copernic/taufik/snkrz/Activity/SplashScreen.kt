package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import cat.copernic.taufik.snkrz.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

/**
 * Clase SplashScreen que muestra una pantalla de presentación al iniciar la aplicación.
 */
class SplashScreen : AppCompatActivity() {
    /**
     * Método onCreate que se ejecuta al crear la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Ocultar la app bar
        supportActionBar?.hide()

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.asset_fade_in)
        val splashAnimation2 = AnimationUtils.loadAnimation(this, R.anim.asset_fade_in_2)
        //comentar
        LetrasSNKRZ.startAnimation(splashAnimation)
        byTaufik.startAnimation(splashAnimation)
        LogoSnkr.startAnimation(splashAnimation2)
        LogoSnkrTransparente.startAnimation(splashAnimation)
        progressBar.startAnimation(splashAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            //launch the main activity
            startActivity(
                Intent(this@SplashScreen, Login::class.java)
            )
            finish()
        }, 3550)
    }
}