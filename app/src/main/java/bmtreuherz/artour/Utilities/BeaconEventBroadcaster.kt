package bmtreuherz.artour.Utilities

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

/**
 * Created by Bradley on 3/21/18.
 */
object BeaconEventBroadcaster {
    val BEACON_ID = "beaconID"
    val ENTERED_RANGE = "enteredRange"
    val EXITED_RANGE = "exitedRange"
    lateinit var context: Context

    fun beaconEnteredRange(beaconID: Int){
        var intent = Intent()
        intent.action = ENTERED_RANGE
        intent.putExtra(BEACON_ID, beaconID)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    fun beaconExitedRange(beaconID: Int){
        var intent = Intent()
        intent.action = EXITED_RANGE
        intent.putExtra(BEACON_ID, beaconID)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}