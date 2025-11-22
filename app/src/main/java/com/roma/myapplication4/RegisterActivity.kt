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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.roma.myapplication4.data.User
import com.roma.myapplication4.viewmodel.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnRegisterNow = findViewById<Button>(R.id.btnRegisterNow)
        val tvGoLogin = findViewById<TextView>(R.id.tvGoLogin)
        val btnBack = findViewById<ImageButton>(R.id.btnBackRegister)

        btnRegisterNow.setOnClickListener {
            val name = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(baseContext, "Барлық өрістерді толтырыңыз.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, get user ID from Auth
                        val firebaseUser = auth.currentUser
                        val userId = firebaseUser?.uid ?: return@addOnCompleteListener

                        // Create user object with the correct ID
                        val newUser = User(id = userId, name = name, email = email, password = "") // Never store plaintext passwords

                        // Save additional user info to Realtime Database
                        Firebase.database.getReference("Users").child(userId).setValue(newUser).addOnSuccessListener {
                            // After saving to Firebase, also save to local Room database
                            userViewModel.addUser(newUser)

                            // Create session
                            val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            prefs.edit().apply {
                                putString("name", newUser.name)
                                putString("email", newUser.email)
                                apply()
                            }

                            // Go to HomeActivity
                            val homeIntent = Intent(this@RegisterActivity, HomeActivity::class.java)
                            startActivity(homeIntent)
                            finishAffinity()
                        }.addOnFailureListener {
                             Toast.makeText(baseContext, "Realtime Database-те сақтау мүмкін болмады.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // If sign in fails, provide a specific error message.
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> "Құпия сөз кемінде 6 таңбадан тұруы керек."
                            is FirebaseAuthInvalidCredentialsException -> "Email адресі қате форматта."
                            is FirebaseAuthUserCollisionException -> "Бұл email адресі тіркелген."
                            else -> "Тіркелу сәтсіз аяқталды: ${task.exception?.localizedMessage}"
                        }
                        Toast.makeText(baseContext, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
        }

        tvGoLogin.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
