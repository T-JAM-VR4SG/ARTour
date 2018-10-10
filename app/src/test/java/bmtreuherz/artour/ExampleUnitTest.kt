package bmtreuherz.artour

import bmtreuherz.artour.Utilities.HttpClient
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun get_Locations_Works(){
        var locations = HttpClient().getLocations()
        for (loc in locations){
            System.out.println("Testing")
            System.out.println(loc.name)
            System.out.println(loc.description)
            System.out.println(loc.lat.toString() + ", " + loc.long)
            for (feat in loc.features){
                System.out.println("\t" + feat.name)
                System.out.println("\t" + feat.description)
                System.out.println("\t" + feat.beaconID)
            }
        }
    }
}
