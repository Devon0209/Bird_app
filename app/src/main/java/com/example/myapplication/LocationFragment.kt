package com.example.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import kotlin.concurrent.thread

class LocationFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocationButton: Button

    // Custom marker for hotspots
    private lateinit var hotspotMarker: BitmapDescriptor

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_location, container, false)

        currentLocationButton = rootView.findViewById(R.id.currentLocationButton)

        // Load a custom marker image for hotspots
        hotspotMarker = createCustomMarker(Color.argb(100, 0, 0, 255))

        currentLocationButton.setOnClickListener {
            // Get the user's current location and add a marker to the map
            getCurrentLocation()
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLong)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))

                    // Now get nearby hotspots based on the current location
                    getNearbyHotspots(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("Current Location")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        // Handle marker click events here
        return false
    }

    private fun getNearbyHotspots(latitude: Double, longitude: Double) {
        val apiKey = "tsmqqtr4glk1"

        // Build the URL for the eBird API
        val url =
            "https://api.ebird.org/v2/ref/hotspot/geo?lat=$latitude&lng=$longitude&dist=50&fmt=json"

        thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .addHeader("X-eBirdApiToken", apiKey)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body()?.string()
                    responseData?.let {
                        parseHotspotData(it)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch nearby hotspots",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun parseHotspotData(responseData: String?) {
        responseData?.let {
            try {
                val hotspotsArray = JSONArray(it)
                for (i in 0 until hotspotsArray.length()) {
                    val hotspot = hotspotsArray.getJSONObject(i)
                    val name = hotspot.getString("locName")
                    val lat = hotspot.getDouble("lat")
                    val lng = hotspot.getDouble("lng")
                    val hotspotLocation = LatLng(lat, lng)

                    activity?.runOnUiThread {
                        mMap.addMarker(
                            MarkerOptions()
                                .position(hotspotLocation)
                                .title(name)
                                .icon(hotspotMarker)
                        )
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Error parsing hotspot data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createCustomMarker(color: Int): BitmapDescriptor {
        val width = 48
        val height = 48
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("LocationFragment", "Connection failed: $connectionResult")
    }
}



