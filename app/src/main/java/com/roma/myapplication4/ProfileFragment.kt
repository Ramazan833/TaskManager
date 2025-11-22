package com.roma.myapplication4

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val profileCard = view.findViewById<ConstraintLayout>(R.id.profileCard)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogoutProfile)
        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)

        // Background animation
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(2000)
        animDrawable.setExitFadeDuration(4000)
        animDrawable.start()

        // --- PERFORMANCE FIX: Use ViewPropertyAnimator for smooth animations ---
        // Hide views initially and set them up for animation
        profileCard.alpha = 0f
        profileCard.translationY = 50f
        btnLogout.alpha = 0f
        btnLogout.translationY = 50f

        // Animate views
        profileCard.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(300).start()
        btnLogout.animate().alpha(1f).translationY(0f).setDuration(600).setStartDelay(500).start()

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
