package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.Sightings.AddFragment
import com.example.myapplication.Sightings.SightingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuActivity : AppCompatActivity() {
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_add -> {
                loadFragment(AddFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_feather -> {
                loadFragment(SightingsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_location -> {
                loadFragment(LocationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_person -> {
                loadFragment(PersonFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                loadFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        loadFragment(LocationFragment()) // Load the initial fragment.
    }
}