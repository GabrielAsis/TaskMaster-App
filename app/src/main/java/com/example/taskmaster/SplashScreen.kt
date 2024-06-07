package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.logging.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)


        val imageView = findViewById<ImageView>(R.id.imageView)

        val zoomInAnimation = ScaleAnimation(
            0.0f, 1.0f,
            0.0f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        zoomInAnimation.duration = 1500
        zoomInAnimation.fillAfter = true
        zoomInAnimation.interpolator = OvershootInterpolator()

        imageView.startAnimation(zoomInAnimation)

        android.os.Handler().postDelayed({
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}