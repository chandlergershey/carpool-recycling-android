package com.example.carpool_recycling_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.carpool_recycling_app.ui.login.LoginActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.*


var mLocationPermissionGranted = false
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mMap: GoogleMap
    private lateinit var auth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var cache: HashMap<Pair<Double, Double>, ArrayList<Array<String>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        auth = FirebaseAuth.getInstance() //connects to DB
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        //val selectPhotoButton = findViewById<Button>(R.id.selectphoto_button_register)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        loadCache()
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        getPermissions()
        mapFragment.getMapAsync(this)
    }

    fun loadCache() {
        val file_path = this.applicationContext.filesDir
        val file = File(file_path, "cache")
        val ois = ObjectInputStream(FileInputStream(file))
        val map = ois.readObject() as HashMap<*, *>
        cache = map as HashMap<Pair<Double, Double>, ArrayList<Array<String>>>
    }

    fun saveCache() {
        val file_path = this.applicationContext.filesDir
        val file = File(file_path, "cache")
        val outputStream = ObjectOutputStream(FileOutputStream(file))
        outputStream.writeObject(cache)
        outputStream.flush()
        outputStream.close()
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
        val TAG = "recyclingHTTPRequest"
        mMap = googleMap
        var cacheValue = ArrayList<Array<String>>()
        mMap.isMyLocationEnabled = true
        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (mLocationPermissionGranted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location. In some rare situations this can be null.
                        var latitude = location?.latitude
                        var longitude = location?.longitude
                        if(longitude != null && latitude != null){
                            latitude = "%.2f".format(latitude).toDouble()
                            longitude = "%.2f".format(longitude).toDouble()
                            val currentLocation = LatLng(latitude, longitude)
                            val zoomLevel = 12.0f //This goes up to 21
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel))

                            val key = Pair(latitude, longitude)
                            Log.d(TAG, latitude.toString())
                            Log.d(TAG, longitude.toString())
                            if(cache.contains(key)){
                                    Log.d(TAG, "Cache hit")
                                    val markers = cache.get(key)
                                    for(center: Array<String> in markers!!){
                                        if(center.size == 4){
                                            parseResponse(center[0].toDouble(), center[1].toDouble(), center[2], center[3], mMap)
                                        }
                                    }
                            }
                            else {
                                recyclingLocationRequest(latitude, longitude, mMap, key)
                            }
                        }
                    }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    private fun recyclingLocationRequest(latitude : Double, longitude : Double, mMap : GoogleMap, key : Pair<Double, Double>) {
                val TAG = "recyclingHTTPRequest"
                val queue = Volley.newRequestQueue(this)
                val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?input="
                val input = "Recycling%20Center"
                val inputType = "&inputtype=textquery"
                val fields = "&fields=formatted_address,name,opening_hours,geometry"
                val radius = "&circle:50000@$latitude,$longitude"
//                val radius = "&point:$latitude,$longitude"
                val placesKey = this.resources.getString(R.string.google_places_api_key)
                val apiKey = "&key=$placesKey"
                val fullQuery = url + input + inputType + fields + radius + apiKey
                Log.d("APICall", fullQuery)
                Log.d("APICall", "$latitude $longitude")
                var cacheLocation = ArrayList<Array<String>>()

        // Request a string response from the provided URL.
                val stringRequest = StringRequest(
                    Request.Method.GET, fullQuery,
                    Response.Listener<String> { response ->
                        val a = response
                        var obj = JSONObject(response)
                        var arr = obj.getJSONArray("results")
                        var size = arr.length()
                        var i = 0
                        while(i < arr.length()){
                            val center  = arr.getJSONObject(i)
                            val lat = center.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                            val lng = center.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                            val name = center.getString("name")
                            val formattedAddress = center.getString("formatted_address")
                            parseResponse(lat, lng, name, formattedAddress, mMap)
                            var cacheData = arrayOf(lat.toString(), lng.toString(), name, formattedAddress)
                            cacheLocation.add(cacheData)
                            i += 1
                        }
                        cache[key] = cacheLocation
                    },
                    Response.ErrorListener { Log.d(TAG, "HTTP request failed") })
        // Add the request to the RequestQueue.
                queue.add(stringRequest)
    }

    private fun parseResponse(lat : Double, lng : Double, name : String, formattedAddress : String,  mMap : GoogleMap){
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(name)
                .snippet("Address : $formattedAddress")
        )
    }

    private fun getPermissions() {
        val appPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET)
        val fineLocationPermissions = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        val internetPermissions = ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        //Permission request triggered if either of these is not there.
        if(!fineLocationPermissions || !internetPermissions){
            ActivityCompat.requestPermissions(this, appPermissions,
                1
            )
        }
        else{
            mLocationPermissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.d("permission", "Permissions were called")
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    mLocationPermissionGranted = true
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    val toast = Toast.makeText(applicationContext, "Requires user location information", Toast.LENGTH_SHORT)
                    toast.show()
                    finish() //Quit activity
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveCache()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_message -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_create_group -> {
                Toast.makeText(this, "Groups clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CreateGroupActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_profile -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_map -> {
                Toast.makeText(this, "Map clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_sign_out -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
                auth.signOut()
                val toast = Toast.makeText(applicationContext, "Signed out", Toast.LENGTH_SHORT)
                toast.show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
