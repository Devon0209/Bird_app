package com.example.myapplication.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R


import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.MenuActivity
import com.example.myapplication.SignInActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
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
                        // Save user data to Firebase Realtime Database
                        saveUserDataToDatabase(uid, firstName, lastName, email)

                        val intent = Intent (this@RegistrationActivity, MenuActivity::class.java)
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

    private fun saveUserDataToDatabase(uid: String, firstName: String, lastName: String, email: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users").child(uid)
        val userData = User(firstName, lastName, email)

        reference.setValue(userData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration and data storage complete
                    Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()
                    // Redirect to the main activity or login page
                } else {
                    Toast.makeText(this, "Data storage failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
