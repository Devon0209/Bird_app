package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gomaps_btn: Button = findViewById(R.id.gomaps_btn)
        gomaps_btn.setOnClickListener {
            // Create an Intent to open the destination Activity
            val intent = Intent (this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }

        val signup_btn: Button = findViewById(R.id.signup_btn)
        signup_btn.setOnClickListener {
            // Create an Intent to open the destination Activity
            val intent = Intent (this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
        }


    }
}