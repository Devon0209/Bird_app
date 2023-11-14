package com.example.intakab1.Sightings
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.example.intakab1.R

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val birdEditText = view.findViewById<EditText>(R.id.editTextBird)
        val dateEditText = view.findViewById<EditText>(R.id.editTextDate)
        val locationEditText = view.findViewById<EditText>(R.id.editTextLocation)
        val descriptionEditText = view.findViewById<EditText>(R.id.editTextDescription)
        val addButton = view.findViewById<Button>(R.id.addButton)

        // Set up a Calendar instance to get the current date
        val calendar = Calendar.getInstance()

        dateEditText.setOnClickListener {
            // Create a date picker dialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    // Update the date EditText with the selected date
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateEditText.setText(sdf.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Show the date picker dialog
            datePickerDialog.show()
        }

        addButton.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val uid = user.uid
                val bird = birdEditText.text.toString()
                val date = dateEditText.text.toString()
                val location = locationEditText.text.toString()
                val description = descriptionEditText.text.toString()

                // Create a data object to be stored in Firestore
                val data = HashMap<String, Any>()
                data["bird"] = bird
                data["date"] = date
                data["location"] = location
                data["description"] = description

                // Store the data in the user's "sightings" subcollection
                firestore.collection("users")
                    .document(uid)
                    .collection("sightings")
                    .add(data)
                    .addOnSuccessListener {
                        // Data added successfully
                        birdEditText.text.clear()
                        dateEditText.text.clear()
                        locationEditText.text.clear()
                        descriptionEditText.text.clear()
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure to add data
                        // You can display an error message or handle it as needed
                    }
            }
        }

        return view
    }
}