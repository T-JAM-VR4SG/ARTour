package bmtreuherz.artour.Utilities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import bmtreuherz.artour.ARTourApplication

/**
 * Created by Bradley on 3/21/18.
 */
object PermissionHelper {
    fun hasScanningPermissions(activity: Activity): Boolean{

        var bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        var bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null){
            Log.d("Permission Helper", "Does not support bluetooth. Pretending all beacons are in range.")
            // Bluetooth is not supported. To mock bluetooth discovery, pretend that all beacons are in range
            var features = HttpClient.getFeatures()
            features
                .forEach {
                    ARTourApplication.beaconsInRange.add(it.beaconID)
                }
            return false
        }

        // Check if bluetooth is enabled and request if not
        if (bluetoothAdapter?.isEnabled != true){
            activity.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return false
        }

        // Check if location permissions are enabled and request if not
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            var permissions = Array(1){ Manifest.permission.ACCESS_FINE_LOCATION}
            activity.requestPermissions(permissions, 1)
            return false
        }
        return true
    }
}