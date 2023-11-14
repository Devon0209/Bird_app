package com.example.intakab2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)




        val circularImageView = findViewById<ImageView>(R.id.imageView2)
// You can further customize your ImageView or load an image into it here.
    }
}