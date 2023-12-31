package com.example.myapplication


import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)

            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location->

        if (location != null) {
            lastLocation = location
            val currentLatLong = LatLng(location.latitude, location.longitude)
            placeMarkerOnMap(currentLatLong)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))

           }
        }
    }
       private fun placeMarkerOnMap(currentLatLong: LatLng) {
            val markerOptions = MarkerOptions().position(currentLatLong)
            markerOptions.title("$currentLatLong")
            mMap.addMarker(markerOptions)
        }

    //override fun onMarkerClick(p0: Marker?)= false
    override fun onMarkerClick(marker: Marker): Boolean {
        // Get the marker's position (latitude and longitude)
        val position = marker.position

        // Create a CameraUpdate to zoom in on the marker
        val zoomLevel = 15f // Adjust this value as needed
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoomLevel)

        // Move the camera to the marker's position with the specified zoom level
        mMap.moveCamera(cameraUpdate)

        // Display the latitude and longitude in a Toast message
        val message = "Latitude: ${position.latitude}, Longitude: ${position.longitude}"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Return true to indicate that the marker click event has been handled
        return true
    }

}
