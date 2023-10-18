package com.example.myapplication

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class PersonFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private lateinit var imageView: ImageView
    private lateinit var addImageButton: Button
    private val imagePickerRequestCode = 123
    private var displayedImageUrl: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        imageView = view.findViewById(R.id.imageView2)
        addImageButton = view.findViewById(R.id.addImageButton)
        addImageButton.setOnClickListener { openImagePicker() }
        displayUserInfo(view)
        return view
    }

    private fun displayUserInfo(view: View) {
        val user = auth.currentUser
        if (user != null) {
            val displayNameTextView: TextView = view.findViewById(R.id.displayNameTextView)
            val emailTextView: TextView = view.findViewById(R.id.emailTextView)
            // Set the user's display name and email
            displayNameTextView.text = user.displayName
            emailTextView.text = user.email

            // Load and display the currently displayed image (if available)
            if (displayedImageUrl != null) {
                Picasso.get().load(displayedImageUrl).into(imageView)
            }

            // Check if the user has a profile image URL in Firestore
            val userImageRef = storageRef.child("images/${user.uid}.jpg")
            userImageRef.downloadUrl.addOnSuccessListener { uri ->
                displayedImageUrl = uri
                Picasso.get().load(uri).into(imageView)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickerRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickerRequestCode && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                // Update the displayed image
                displayedImageUrl = selectedImageUri
                Picasso.get().load(selectedImageUri).into(imageView)
                // Upload the selected image to Firestore
                uploadImageToFirestore(selectedImageUri)
            }
        }
    }

    private fun uploadImageToFirestore(imageUri: Uri) {
        val user = auth.currentUser
        if (user != null) {
            val imagesRef = storageRef.child("images/${user.uid}.jpg")
            imagesRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    imagesRef.downloadUrl.addOnSuccessListener { uri ->
                        // Save the image URL in Firestore under the user's collection
                        // Implement this part based on your Firestore structure
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failures
                }
        }
    }
}



