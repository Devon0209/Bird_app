package com.example.intakab1.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.intakab1.R


import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.intakab1.MenuActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firstNameEditText = findViewById(R.id.lfirstname)
        lastNameEditText = findViewById(R.id.lastname)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        signUpButton = findViewById(R.id.login_btn)

        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid ?: ""
                        // Save user data to Firestore
                        saveUserDataToFirestore(uid, firstName, lastName, email, password)

                        val intent = Intent(this@RegistrationActivity, MenuActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun saveUserDataToFirestore(uid: String, firstName: String, lastName: String, email: String, password:String) {
        val user = User(firstName, lastName, email, password)

        firestore.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                // Registration and data storage successful
                Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Data storage failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}

