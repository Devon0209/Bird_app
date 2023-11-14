package com.example.intakab1.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.intakab1.MenuActivity
import com.example.intakab1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        emailEditText = findViewById(R.id.email_edtxt)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_btn)
        signUpButton = findViewById(R.id.signup_btn)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Authentication successful
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            // Retrieve user data from Firestore
                            retrieveUserDataFromFirestore(currentUser.uid)
                        }
                    } else {
                        Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveUserDataFromFirestore(uid: String) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User data exists in Firestore
                    val userData = document.toObject(User::class.java)
                    if (userData != null) {
                        // User data retrieved, customize the user experience as needed
                    }
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error reading Firestore data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


