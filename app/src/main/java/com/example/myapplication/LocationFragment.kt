package com.example.myapplication


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class LocationFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {



    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("X-eBirdApiToken", "tsmqqtr4glk1")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.ebird.org/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EBirdApiService::class.java)

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var currentLocationButton: Button
    private lateinit var locationNameTextView: TextView
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView

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
        locationNameTextView = rootView.findViewById(R.id.locationNameTextView)
        latitudeTextView = rootView.findViewById(R.id.latitudeTextView)
        longitudeTextView = rootView.findViewById(R.id.longitudeTextView)

        // Load a custom marker image for hotspots
        hotspotMarker = BitmapDescriptorFactory.fromResource(R.drawable.hotspot_marker)

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
        GlobalScope.launch(Dispatchers.IO) {
            val hotspots = api.getNearbyHotspots(20, latitude, longitude, 30, "json")
            activity?.runOnUiThread {
                // Handle the list of nearby hotspots
                if (hotspots != null) {
                    for (hotspot in hotspots) {
                        // Add markers for nearby hotspots on the map with custom marker
                        val hotspotLatLng = LatLng(hotspot.latitude, hotspot.longitude)
                        val markerOptions = MarkerOptions()
                            .position(hotspotLatLng)
                            .title(hotspot.name)
                            .icon(hotspotMarker) // Use the custom marker
                        mMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }
}

