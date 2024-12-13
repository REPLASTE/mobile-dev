package com.bangkit.replaste.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.R
import com.bangkit.replaste.helper.SecurePreferences

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoView = findViewById<ImageView>(R.id.logoImageView)
        logoView.alpha = 0f
        logoView.animate()
            .alpha(1f)
            .setDuration(1500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    checkJwtToken()
                }
            })
    }


    private fun checkJwtToken() {
        val securePreferences = SecurePreferences(this)
        val jwtToken = securePreferences.getJwtToken()

        if (jwtToken.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
