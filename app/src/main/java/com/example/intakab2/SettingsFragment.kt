package com.example.intakab2
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : Fragment() {

    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView
    private lateinit var toggleButton: ToggleButton
    private lateinit var sendDataButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        seekBar = rootView.findViewById(R.id.seekBar)
        textView = rootView.findViewById(R.id.textView)
        toggleButton = rootView.findViewById(R.id.toggleButton)
        sendDataButton = rootView.findViewById(R.id.sendDataButton)

        // Retrieve user settings from Firestore and update UI
        retrieveUserSettingsFromFirestore()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sendDataButton.setOnClickListener {
            val value = seekBar.progress + 1
            val isMiles = toggleButton.isChecked

            // Save user settings to Firestore
            sendSettingsToFirestore(value, isMiles)
        }

        return rootView
    }

    private fun sendSettingsToFirestore(value: Int, isMiles: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            // Replace "users" with the actual name of your Firestore collection
            val userSettingsRef = db.collection("users").document(userId)
                .collection("user_settings").document("settings")

            val userSettings = hashMapOf(
                "value" to value,
                "isMiles" to isMiles
            )

            userSettingsRef.set(userSettings)
                .addOnSuccessListener {
                    // Data sent successfully
                    // You can add additional logic or UI updates here
                }
                .addOnFailureListener { e ->
                    // Handle errors
                }
        }
    }

    private fun retrieveUserSettingsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()

            // Replace "users" with the actual name of your Firestore collection
            val userSettingsRef = db.collection("users").document(userId)
                .collection("user_settings").document("settings")

            userSettingsRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val value = document.getLong("value")?.toInt() ?: 1
                        val isMiles = document.getBoolean("isMiles") ?: true

                        // Update UI with retrieved user settings
                        updateUI(value, isMiles)
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors
                }
        }
    }

    private fun updateUI(value: Int, isMiles: Boolean) {
        seekBar.progress = value - 1
        textView.text = value.toString()
        toggleButton.isChecked = isMiles
        toggleButton.textOn = if (isMiles) "Kilometers" else "Miles"
        toggleButton.textOff = if (isMiles) "Miles" else "Kilometers"
    }
}

