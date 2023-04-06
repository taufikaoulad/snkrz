package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import cat.copernic.taufik.snkrz.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.asset_fade_in)
        //comentar
        LogoSnkr.startAnimation(splashAnimation)
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