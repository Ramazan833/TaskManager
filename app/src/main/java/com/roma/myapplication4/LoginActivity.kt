package com.roma.myapplication4

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.roma.myapplication4.data.User
import com.roma.myapplication4.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLoginNow = findViewById<Button>(R.id.btnLoginNow)
        val tvGoRegister = findViewById<TextView>(R.id.tvGoRegister)
        val btnBack = findViewById<ImageButton>(R.id.btnBackLogin)

        btnLoginNow.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(baseContext, "Барлық өрістерді толтырыңыз.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Authentication successful, now handle user data
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid ?: return@addOnCompleteListener

                        // Immediately create a session with the data we have (email)
                        // and proceed to HomeActivity. We will fetch the rest of the data in the background.
                        val partialUser = User(id = userId, email = email, name = "") // Name is temporarily empty
                        onLoginSuccess(partialUser)
                        
                        // In the background, fetch full user details and update Room/session
                        Firebase.database.getReference("Users").child(userId).get().addOnSuccessListener {
                            val fullUser = it.getValue(User::class.java)
                            if (fullUser != null) {
                                lifecycleScope.launch {
                                    userViewModel.addUser(fullUser) // Update Room
                                    val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                    prefs.edit().putString("name", fullUser.name).apply() // Update session
                                }
                            }
                        }

                    } else {
                        // If sign in fails, display a specific error message.
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "Мұндай email тіркелмеген."
                            is FirebaseAuthInvalidCredentialsException -> "Құпия сөз қате."
                            else -> "Кіру сәтсіз аяқталды: ${task.exception?.localizedMessage}"
                        }
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun onLoginSuccess(user: User) {
        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", user.name)
            putString("email", user.email)
            apply()
        }
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
