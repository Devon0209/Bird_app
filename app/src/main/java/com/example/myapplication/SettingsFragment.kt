package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent

import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton

class SettingsFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var toggleButton: ToggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        textView = view.findViewById(R.id.textView)
        seekBar = view.findViewById(R.id.seekBar)
        toggleButton = view.findViewById(R.id.toggleButton)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleButton.textOn = if (isChecked) "Miles" else "Kilometers"
            toggleButton.textOff = if (isChecked) "Kilometers" else "Miles"
        }

        // Handle sending data using Intent
        view.findViewById<View>(R.id.sendDataButton).setOnClickListener {
            val value = seekBar.progress + 1
            val isMiles = toggleButton.isChecked

            val intent = Intent(activity, LocationFragment::class.java)
            intent.putExtra("value", value)
            intent.putExtra("isMiles", isMiles)
            startActivity(intent)
        }

        return view
    }
}
