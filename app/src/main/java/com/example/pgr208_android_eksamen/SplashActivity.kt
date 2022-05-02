package com.example.pgr208_android_eksamen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    private lateinit var ivlogo:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        ivlogo=findViewById(R.id.iv_logo)
        ivlogo.visibility= View.VISIBLE
        ivlogo.startAnimation(AnimationUtils.loadAnimation(
            this@SplashActivity,android.R.anim.fade_in))

        Handler().postDelayed(
            {
                startActivity(Intent(this@SplashActivity,
                    MainActivity::class.java))

                overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out)
                finish()
            }, 2000
        )


    }
}