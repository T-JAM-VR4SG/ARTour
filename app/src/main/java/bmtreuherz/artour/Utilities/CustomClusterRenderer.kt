package bmtreuherz.artour.Utilities

import android.content.Context
import android.graphics.Color
import bmtreuherz.artour.Utilities.BeaconEventBroadcaster.context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * Created by champebarton on 4/30/18.
 */
class CustomClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<MyItem>?) : DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

    override fun getColor(clusterSize: Int): Int {
        return Color.rgb(17, 123, 152) // Return any color you want here. You can base it on clusterSize.
    }


    override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
    }

}

//    @Override
//    protected void onBeforeClusterRendered(Cluster<T> cluster, MarkerOptions markerOptions) {
//        // Use this method to set your own icon for the clusters
//    }
