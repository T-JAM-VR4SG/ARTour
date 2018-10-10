package bmtreuherz.artour

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.preference.PreferenceScreen
import android.util.Log
import bmtreuherz.artour.Activities.Preferences
import bmtreuherz.artour.Activities.Preferences.Companion.prefs
import bmtreuherz.artour.Utilities.BeaconEventBroadcaster
import bmtreuherz.artour.Utilities.HttpClient
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import java.util.UUID
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Bradley on 3/21/18.
 */
class ARTourApplication : Application() {

    companion object {
        val TAG = ARTourApplication::class.java.simpleName
        var beaconsInRange = ConcurrentSkipListSet<Int>()
    }

    private val appUUID = UUID.fromString("2dd68426-eb4a-4920-b94b-02a7d5ec265c")
    private var isSearchingForBeacons = AtomicBoolean(false)
    private var isInBackground = false

    // NOTE: If bluetooth is turned off while the app is running it will likely crash (or do just not work)
    fun startSearchingForBeacons(){
        if (isSearchingForBeacons.compareAndSet(false, true)){
            // Request all the locations

            //var features = listOf(feature())
            val s = Preferences.getLang()


            var features = HttpClient.getFeatures()

            // Create the beacon manager.
            var beaconManager = BeaconManager(applicationContext)

            // TODO: Weigh pros and cons of responsiveness v battery life and tweak this
            beaconManager.setForegroundScanPeriod(10,2)
            beaconManager.setBackgroundScanPeriod(10,2)

            // Initialize the beacon manager
            beaconManager.connect {
                // Create a region for each beacon
                features
                    .forEach {
                        beaconManager.startMonitoring(BeaconRegion(it.beaconID.toString(),
                                appUUID, it.beaconID, null))
                    }
            }


            // Attach a listener to the beacon manager
            beaconManager.setMonitoringListener( object:BeaconManager.BeaconMonitoringListener{
                override fun onEnteredRegion(beaconRegion: BeaconRegion?, beacons: MutableList<Beacon>?) {
                    onEnteredBeaconRegion(beaconRegion?.major!!)
                }
                override fun onExitedRegion(beaconRegion: BeaconRegion?) {
                    onExitedBeaconRegion(beaconRegion?.major!!)
                }
            })
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Keep track of if the app is in the foreground.
        registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(p0: Activity?) {
                Log.d(TAG, "Is in background")
                isInBackground = true
            }

            override fun onActivityResumed(p0: Activity?) {
                Log.d(TAG, "Is not in background")
                isInBackground = false
            }

            override fun onActivityStarted(p0: Activity?) {}

            override fun onActivityDestroyed(p0: Activity?) {}

            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {}

            override fun onActivityStopped(p0: Activity?) { }

            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {}
        })

        // Set the context for the Beacon Event Broadcaster
        BeaconEventBroadcaster.context = applicationContext
    }

    private fun onEnteredBeaconRegion(beaconID: Int){
        Log.d(TAG, "Entered region: " + beaconID)
        // Add the beacon to the set
        beaconsInRange.add(beaconID)

        // Broadcast that the beacon region was entered
        BeaconEventBroadcaster.beaconEnteredRange(beaconID)

        // If the app is in the background, create a notification
        if (isInBackground) createNotification(beaconID)
    }

    private fun onExitedBeaconRegion(beaconID: Int){
        Log.d(TAG, "Exited region: " + beaconID)

        // Remove the beacon from the set
        beaconsInRange.remove(beaconID)

        // Broadcast that the beacon exited region
        BeaconEventBroadcaster.beaconExitedRange(beaconID)
    }

    private fun createNotification(beaconID: Int){
        // TODO: Implement this
    }
}