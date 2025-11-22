package com.roma.myapplication4

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Find views
        val rootLayout = view.findViewById<RelativeLayout>(R.id.profile_root_layout)
        val profileCard = view.findViewById<ConstraintLayout>(R.id.profileCard) // Corrected to ConstraintLayout
        val avatar = view.findViewById<ShapeableImageView>(R.id.iv_profile_avatar)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogoutProfile)

        // Background animation
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(2000)
        animDrawable.setExitFadeDuration(4000)
        animDrawable.start()

        // Staggered animations
        val fadeInSlideUp = AnimationUtils.loadAnimation(context, R.anim.fade_slide_in_from_bottom)
        
        // Hide views initially
        profileCard.visibility = View.INVISIBLE
        btnLogout.visibility = View.INVISIBLE

        // Start animations with delay
        Handler(Looper.getMainLooper()).postDelayed({
            profileCard.visibility = View.VISIBLE
            profileCard.startAnimation(fadeInSlideUp)
        }, 300)
        
        Handler(Looper.getMainLooper()).postDelayed({
            btnLogout.visibility = View.VISIBLE
            btnLogout.startAnimation(fadeInSlideUp)
        }, 500)

        // Load user data
        val prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "Nurtas Nurtas")
        val email = prefs.getString("email", "email@example.com")

        tvUserName.text = name
        tvUserEmail.text = email

        // Logout listener
        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
