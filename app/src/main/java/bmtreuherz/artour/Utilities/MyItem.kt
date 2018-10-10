package bmtreuherz.artour.Utilities

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Created by champebarton on 4/29/18.
 */
class MyItem : ClusterItem {
    private var position: LatLng
    private var title: kotlin.String
    private var snippet: kotlin.String?

    constructor(position: LatLng, title: String, snippet: String?) {
        this.position = position
        this.snippet = snippet
        this.title = title
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }
}
