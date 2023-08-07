package com.example.googlemap

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.googlemap.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
//        val india = LatLng(21.20053564852279, 72.80392602733627)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(india)
//                .title("Iroid, Iam here")
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(india))

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true


        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

                val markerOptions = MarkerOptions().position(latLng).title("Current Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_push_pin))
                mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady(mMap)
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val DEFAULT_ZOOM = 10f
    }
}

