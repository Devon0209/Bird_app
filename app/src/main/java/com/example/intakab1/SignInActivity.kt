package com.example.intakab1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        val login2_btn: Button = findViewById(R.id.login2_btn)
        login2_btn.setOnClickListener {
            // Create an Intent to open the destination Activity
            val intent = Intent (this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
