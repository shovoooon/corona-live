package com.shovon.coronalive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lat:Double = 0.0
    private var lon:Double = 0.0
    private var contactName = "Contact Name"
    private var lastSeen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        try {
            lat = intent.getStringExtra("lat").toDouble()
            lon = intent.getStringExtra("lon").toDouble()
            contactName = intent.getStringExtra("name")
            lastSeen = intent.getStringExtra("lastSeen")
            Log.v("step", "$lat  $lon")

        }catch (e:Exception){
            Log.v("step", e.message.toString())
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lat, lon)
        Log.v("step", "onMapReady")
        mMap.addMarker(MarkerOptions().position(sydney).title("$contactName - Last Seen: $lastSeen"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))

        if (lat == 0.0 && lon == 0.0){
            toast(this, "Did not found location")
        }
    }
}
