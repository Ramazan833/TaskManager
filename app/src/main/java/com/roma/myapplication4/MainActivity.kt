package com.roma.myapplication4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find Views
        val btnLogin = findViewById<TextView>(R.id.btnLogin) // This is a TextView now
        val btnRegister = findViewById<Button>(R.id.btnRegister) // This is the main Button
        val illustration = findViewById<ImageView>(R.id.iv_welcome_illustration)
        val title = findViewById<TextView>(R.id.tv_welcome_title)
        val subtitle = findViewById<TextView>(R.id.tv_welcome_subtitle)
        val buttonsLayout = findViewById<LinearLayout>(R.id.buttons_layout)

        // Animation
        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        illustration.startAnimation(topAnim)
        title.startAnimation(topAnim)
        subtitle.startAnimation(topAnim)
        buttonsLayout.startAnimation(bottomAnim)

        // Click Listeners
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
