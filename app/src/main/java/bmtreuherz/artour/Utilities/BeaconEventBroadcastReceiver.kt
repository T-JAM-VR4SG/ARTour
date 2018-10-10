package bmtreuherz.artour.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Created by Bradley on 3/21/18.
 */
class BeaconEventBroadcastReceiver : BroadcastReceiver {
    interface BeaconEventDelegate{
        fun onEnteredRange(beaconID: Int)
        fun onExitedRange(beaconID: Int)
    }

    private var delegate: BeaconEventDelegate

    constructor(delegate: BeaconEventDelegate){
        this.delegate = delegate
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action){
            BeaconEventBroadcaster.ENTERED_RANGE -> {
                delegate.onEnteredRange(intent.getIntExtra(BeaconEventBroadcaster.BEACON_ID, -1))
            }
            BeaconEventBroadcaster.EXITED_RANGE -> {
                delegate.onExitedRange(intent.getIntExtra(BeaconEventBroadcaster.BEACON_ID, -1))
            }
        }
    }

    fun createFilter(): IntentFilter {
        var filter = IntentFilter()
        filter.addAction(BeaconEventBroadcaster.ENTERED_RANGE)
        filter.addAction(BeaconEventBroadcaster.EXITED_RANGE)
        return filter
    }
}