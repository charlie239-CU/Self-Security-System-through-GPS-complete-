package com.example.courage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.courage.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import android.graphics.Bitmap
import android.graphics.Canvas

import android.graphics.drawable.Drawable

import com.google.android.gms.maps.model.BitmapDescriptor
import android.R
import android.content.Intent
import androidx.lifecycle.Transformations.map

import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private var cameraPosition: CameraPosition? = null
    private lateinit var locationCallback: LocationCallback
    private var requestingLocationUpdates:Boolean=true
    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private lateinit var auth: FirebaseAuth
    private var username:String=""
    private var email:String=""
    companion object {
        private val TAG=MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
        private val MY_PERMISSION_FINE_LOCATION = 101
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //supportActionBar!!.hide()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager=getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.example.courage.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val user=auth.currentUser
        if (user == null) {
            val intent= Intent(this,Login::class.java)
            startActivity(intent)
        }
        user?.let{
            username=user.displayName.toString()
            email=user.email.toString()
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
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lat=Helper.userData.mapLocation!!.get("lat")
        val long=Helper.userData.mapLocation!!.get("long")

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(lat!!.toDouble(), long!!.toDouble())
        map.addMarker(MarkerOptions().position(sydney).title(Helper.userData.name))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        for(list in Helper.userList) {
            if(list.getEmail()!=email){
                addmarker(list.mapLocation?.get("lat")!!.toDouble(),list.mapLocation?.get("long")!!.toDouble(),list.name.toString())
            }
        }



        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
        else {//condition for Marshmello and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation()
    }

    private fun addmarker(lat:Double,long:Double,username:String){
        val latlng = LatLng(lat, long)
        map.addMarker(MarkerOptions().position(latlng).title(username))

    }




    private fun setMapMarkerForUser(knownLocation:Location){
        val current=LatLng(knownLocation.latitude,knownLocation.longitude)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
            LatLng(knownLocation!!.latitude,
                knownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
        map.addMarker(MarkerOptions().position(current))
        return
    }
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        if(locationResult!!.lastLocation!=null){
                            setMapMarkerForUser(locationResult!!.lastLocation)
                        }
                        //Log.d("locationdata",locationResult!!.lastLocation.longitude.toString())
                        //setMapMarkerForUser(locationResult!!.lastLocation)
                    }
                }
                if (requestingLocationUpdates) Helper.startLocationUpdates(this,fusedLocationProviderClient,locationCallback)
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                                //Log.d("locationdata",location.longitude.toString())

                        }
                    }

                }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
    private fun setPoiClick(map:GoogleMap){
        map.setOnPoiClickListener {
                poi->
            val poiMarker=map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            poiMarker.showInfoWindow()
        }
    }
    override fun onMarkerClick(p0: Marker?) = false

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//permission to access location grant
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                   map.isMyLocationEnabled = true
                }
            }
            //permission to access location denied
            else {
                Toast.makeText(applicationContext, "This app requires location permissions to be granted", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}