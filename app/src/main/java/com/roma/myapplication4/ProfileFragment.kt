package com.roma.myapplication4

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnLogoutProfile)
        val rootLayout = view.findViewById<CoordinatorLayout>(R.id.profile_root_layout) // Corrected to CoordinatorLayout

        // Start the background animation
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        // Load user data from SharedPreferences
        val prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val name = prefs.getString("name", "Nurtas Nurtas")
        val email = prefs.getString("email", "email@example.com")

        // Set data to views
        tvUserName.text = name
        tvUserEmail.text = email

        // Set logout button click listener
        btnLogout.setOnClickListener {
            // Clear session
            prefs.edit().clear().apply()

            // Go back to MainActivity
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}
