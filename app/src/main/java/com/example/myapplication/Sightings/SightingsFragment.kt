package com.example.myapplication.Sightings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

class SightingsFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sightings, container, false)

        firestore = FirebaseFirestore.getInstance()
        listView = view.findViewById(R.id.listView)
        auth = FirebaseAuth.getInstance()

        // Check if a user is logged in
        val user = auth.currentUser
        if (user != null) {
            // Fetch and display sightings for the logged-in user
            fetchSightings(user.uid)
        }

        return view
    }

    private fun fetchSightings(userId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("sightings")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val sightingsList = ArrayList<SightingModel>()

                for (document in querySnapshot.documents) {
                    val bird = document.getString("bird") ?: ""
                    val date = document.getString("date") ?: ""
                    val location = document.getString("location") ?: ""
                    val description = document.getString("description") ?: ""

                    sightingsList.add(SightingModel(bird, date, location, description))
                }

                // Create a custom adapter to display the data
                val adapter = SightingAdapter(requireContext(), sightingsList)
                listView.adapter = adapter
            }
            .addOnFailureListener { e ->
                // Handle the failure to fetch data
            }
    }
}


