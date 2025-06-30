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
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.nukarva_florist.R
import com.example.nukarva_florist.utils.Resource
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
import kotlinx.coroutines.launch
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
    private lateinit var cvPinCurrent: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_pinpoint)

        tvSelectedAddress = findViewById(R.id.tvSelectedAddress)
        tvSubAddress = findViewById(R.id.tvSubAddress)
        btnConfirm = findViewById(R.id.btnConfirmPin)
        cvPinCurrent = findViewById(R.id.cv_pin_current)

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
                putExtra("kota_kecamatan", tvSelectedAddress.text.toString())
                putExtra("address", tvSubAddress.text.toString())
            }
            setResult(RESULT_OK, data)
            finish()
        }

        cvPinCurrent.setOnClickListener {
            moveToCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMapToolbarEnabled = false
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_no_poi))

        val defaultLatLng = LatLng(-6.200000, 106.816666)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15f))

        centerMarker = map.addMarker(
            MarkerOptions()
                .position(defaultLatLng)
                .icon(getBitmapFromVector(this, R.drawable.ic_location_pin, 1.3f))
                .anchor(0.5f, 0.5f)
        )!!

        map.setOnCameraIdleListener {
            val center = map.cameraPosition.target
            centerMarker.position = center
            updateAddress(center)
        }

        // ==== Ambil dari intent ====
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val inputAddress = intent.getStringExtra("input_address")

        if (latitude != 0.0 && longitude != 0.0) {
            // Jika user sebelumnya sudah pilih lokasi, tampilkan lokasi itu
            val userChosenLatLng = LatLng(latitude, longitude)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(userChosenLatLng, 16f))
            centerMarker.position = userChosenLatLng
            updateAddress(userChosenLatLng)
        } else if (!inputAddress.isNullOrBlank()) {
            // Jika user belum punya lat-long, cari berdasarkan alamat
            fetchLatLngFromAddress(inputAddress)
        } else {
            // Jika tidak ada data, pakai lokasi saat ini
            getCurrentLocation()
        }
    }


    private fun fetchLatLngFromAddress(address: String) {
        lifecycleScope.launch {
            try {
                val geoResults = Geocoder(this@ChoosePinpointActivity, Locale.getDefault())
                    .getFromLocationName(address, 1)

                if (!geoResults.isNullOrEmpty()) {
                    val location = geoResults.first()
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    centerMarker.position = latLng
                    updateAddress(latLng)
                } else {
                    Toast.makeText(this@ChoosePinpointActivity, "Alamat tidak ditemukan", Toast.LENGTH_SHORT).show()
                    getCurrentLocation()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChoosePinpointActivity, "Gagal mengambil lokasi", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
        }
    }

    private fun updateAddress(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = addresses?.firstOrNull()

            val streetName = address?.thoroughfare ?: address?.featureName ?: "Unnamed Street"
            val subDistrict = address?.subLocality ?: ""
            val city = address?.locality ?: ""
            val finalSubAddress = listOf(subDistrict, city).filter { it.isNotBlank() }.joinToString(", ")

            tvSelectedAddress.text = streetName
            tvSubAddress.text = if (finalSubAddress.isNotBlank()) finalSubAddress else "Unknown area"

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

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    centerMarker.position = latLng
                    updateAddress(latLng)
                } else {
                    Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
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
