package bmtreuherz.artour.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import bmtreuherz.artour.ARTourApplication
import bmtreuherz.artour.DTOs.Feature
import bmtreuherz.artour.R
import bmtreuherz.artour.Utilities.BeaconEventBroadcastReceiver
import bmtreuherz.artour.Utilities.Features.FeaturesAdapter
import bmtreuherz.artour.Utilities.HttpClient

class FeaturesInRangeActivity : NavigableActivity() {

    // UI Elements
    private lateinit var featuresLV: ListView
    private lateinit var featuresAdapter: FeaturesAdapter

    // Event Receiver
    private lateinit var beaconEventBroadcastReceiver: BeaconEventBroadcastReceiver

    // All features
    private lateinit var features: List<Feature>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        featuresLV = this.findViewById(R.id.featuresLV)
        featuresAdapter = FeaturesAdapter(this, ArrayList())



        featuresLV.adapter = featuresAdapter

        featuresLV.onItemClickListener = OnItemClickListener {
            adapterView, view, i, l ->
            var feature = featuresAdapter.getItem(i)


            //i is the beacon id
            //logs name
            Log.d("feature " + (i+1) + " : ", this.features[i].name.toString())

            //logs description
            //Log.d("feature 0:", this.features[0].description.toString())

            // Do stuff with the feature you'll need to create a new intent,
            // add the beaconID as a string extra, and start the activity with the intent
            //httpClient.getFeatures to get intent


            //var intent = Intent(this,DescriptionActivity::class.java)
            var intent = Intent(this,HelloSceneformActivity::class.java)
            intent.putExtra(DescriptionActivity.BEACON_ID, feature.beaconID);
            startActivity(intent)
        }




        // Create the broadcast receiver. This is how we will get notified when we enter or exit the region of a beacon
        beaconEventBroadcastReceiver = BeaconEventBroadcastReceiver(object: BeaconEventBroadcastReceiver.BeaconEventDelegate{
            override fun onEnteredRange(beaconID: Int) {
                Log.d("FEATURES", "Found one in Range")
                features.filter { it.beaconID == beaconID }
                        .forEach { featuresAdapter.add(it) }
                featuresAdapter.notifyDataSetChanged()
            }

            override fun onExitedRange(beaconID: Int) {
                Log.d("FEATURES", "Found one exited  Range")
                features.filter { it.beaconID == beaconID }
                        .forEach { featuresAdapter.remove(it) }
                featuresAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Initially clear the list of features
        featuresAdapter.clear()

        // Find all the features that are currently in range
        var featuresInRange = ARTourApplication.beaconsInRange
        features = HttpClient.getFeatures()
        for (featureInRange in featuresInRange){
            features
                    .filter { it.beaconID == featureInRange }
                    .forEach { featuresAdapter.add(it) }
        }

        featuresAdapter.notifyDataSetChanged()

        // Subscribe to notifications when new beacons come in
        var filter = beaconEventBroadcastReceiver.createFilter()
        LocalBroadcastManager.getInstance(this).registerReceiver(beaconEventBroadcastReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(beaconEventBroadcastReceiver)
    }

    override fun getCurrentMenuItemID(): Int {
        return R.id.nav_features_in_range
    }

    override fun setLayout() {
        setContentView(R.layout.activity_features_in_range)
    }
}
