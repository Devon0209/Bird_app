package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

try {
    val signup_btn: Button = findViewById(R.id.signup_btn)
    signup_btn.setOnClickListener {
        // Create an Intent to open the destination Activity
        val intent = Intent(this@MainActivity, SignInActivity::class.java)
        startActivity(intent)
    }
}catch(e: Exception){
    Toast.makeText (this, e.message, Toast.LENGTH_SHORT).show();
}



    }
}