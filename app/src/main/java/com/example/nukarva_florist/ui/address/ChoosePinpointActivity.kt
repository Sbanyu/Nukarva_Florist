package com.example.nukarva_florist.ui.address

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nukarva_florist.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ChoosePinpointActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var centerMarker: Marker

    private lateinit var tvSelectedAddress: TextView
    private lateinit var tvSubAddress: TextView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pinpoint)

        tvSelectedAddress = findViewById(R.id.tvSelectedAddress)
        tvSubAddress = findViewById(R.id.tvSubAddress)
        btnConfirm = findViewById(R.id.btnConfirmPin)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnConfirm.setOnClickListener {
            val center = map.cameraPosition.target
            val data = Intent().apply {
                putExtra("latitude", center.latitude)
                putExtra("longitude", center.longitude)
                putExtra("address", tvSubAddress.text.toString())
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isMapToolbarEnabled = false
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_no_poi)
        )

        val defaultLatLng = LatLng(-6.200000, 106.816666)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15f))

        // Marker di tengah kamera
        centerMarker = map.addMarker(
            MarkerOptions()
                .position(defaultLatLng)
                .icon(getBitmapFromVector(this, R.drawable.ic_location_pin, 1.0f))
                .anchor(0.5f, 0.5f)
        )!!

        map.setOnCameraIdleListener {
            val center = map.cameraPosition.target
            centerMarker.position = center
            updateAddress(center)
        }

        getCurrentLocation()
    }

    private fun updateAddress(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.firstOrNull()
            tvSelectedAddress.text = address?.featureName ?: "Unnamed place"
            tvSubAddress.text = address?.getAddressLine(0) ?: "Unknown address"
        } catch (e: Exception) {
            tvSelectedAddress.text = "Unknown"
            tvSubAddress.text = "Unable to load address"
        }
    }

    private fun getBitmapFromVector(context: Context, drawableId: Int, scale: Float): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, drawableId)!!
        val width = (vectorDrawable.intrinsicWidth * scale).toInt()
        val height = (vectorDrawable.intrinsicHeight * scale).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 10000
                fastestInterval = 5000
                numUpdates = 1
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    centerMarker.position = latLng
                    updateAddress(latLng)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }, Looper.getMainLooper())

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
