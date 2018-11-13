package bmtreuherz.artour.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.content.DialogInterface
import bmtreuherz.artour.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import bmtreuherz.artour.ARTourApplication
import bmtreuherz.artour.Utilities.*
import java.util.jar.Manifest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager

import java.time.LocalTime

class MapActivity : NavigableActivity() {
    override fun getCurrentMenuItemID(): Int {
        return R.id.nav_map
    }

    override fun setLayout() {
        setContentView(R.layout.activity_map)
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var beaconEventBroadcastReceiver: BeaconEventBroadcastReceiver

    private var locations = HttpClient.getFeatures()
    private var locationLimit = locations.size - 1

    private var mClusterManager: ClusterManager<MyItem>? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var onMapReadyCallback = object: OnMapReadyCallback{
        override fun onMapReady(googleMap: GoogleMap) {

            map = googleMap

            map.uiSettings.isZoomControlsEnabled = true


            setUpClusterer()

            setUpMap()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(onMapReadyCallback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Sets instructions for what to do when you get close to a beacon on map screen and gives alertdialog
        beaconEventBroadcastReceiver = BeaconEventBroadcastReceiver(object: BeaconEventBroadcastReceiver.BeaconEventDelegate{
            override fun onEnteredRange(beaconID: Int) {
                //adds the nearby feature to the features list
                Log.d("FEATURES", "Found one in Range")
                var features = HttpClient.getFeatures()
                var feature = features.find { it.beaconID == beaconID }

                val builder = AlertDialog.Builder(this@MapActivity)
                //We will probably want to add vibration to alert user when dialog happens
                builder.setTitle("Nearby Beacon Detected")
                builder.setMessage("Would you like to get more information about: " + feature?.name + " ?")
                builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int ->
                    //Take the user to desription page for specific beacon here
                    var intent = Intent(this@MapActivity, DescriptionActivity::class.java)
                    intent.putExtra(DescriptionActivity.BEACON_ID, feature?.beaconID)
                    startActivity(intent)

                })
                builder.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int ->
                    //If the user clicks no, exit the dialog and do nothing
                })
                builder.show()
            }

            override fun onExitedRange(beaconID: Int) {
                Log.d("FEATURES", "Found one exited  Range")
            }
        })


    }

    override fun onResume() {
        super.onResume()

        var filter = beaconEventBroadcastReceiver.createFilter()
        LocalBroadcastManager.getInstance(this).registerReceiver(beaconEventBroadcastReceiver, filter)

        // Check and request bluetooth permissions
        if (!PermissionHelper.hasScanningPermissions(this)){
            return
        }

        // Search for beacons. If we are already searching this will do nothing.
        (application as ARTourApplication).startSearchingForBeacons()
    }


    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(beaconEventBroadcastReceiver)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // 1
        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_NORMAL

        if(Build.VERSION.SDK_INT > 26 && (LocalTime.now().hour >= 20 || LocalTime.now().hour <= 6))
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.dark))
        else if (Build.VERSION.SDK_INT > 26 && (LocalTime.now().hour > 20 || LocalTime.now().hour > 6))
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.light))
        else map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.light))

        // 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.5f))
            }
        }
    }

    private fun setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = ClusterManager(this, map)

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(mClusterManager)
        map.setOnMarkerClickListener(mClusterManager)

        placeMarkers()

        mClusterManager!!.setRenderer(CustomClusterRenderer(this, map, mClusterManager))

        mClusterManager!!.cluster()

    }

    private fun placeMarkers() {
        for (i in 0..locationLimit) {
            var item = MyItem(LatLng(locations[i].lat, locations[i].long),
                    locations[i].name,
                    null)
            mClusterManager?.addItem(item)
        }


    }

}