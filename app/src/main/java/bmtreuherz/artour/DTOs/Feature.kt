package bmtreuherz.artour.DTOs

/**
 * Created by Bradley on 3/19/18.
 */
class Feature {
    var name: String
    var beaconID: Int
    var long: Double
    var lat: Double
    var description: String

    constructor() {
        this.name = ""
        beaconID = 0
        long = 0.0
        lat = 0.0
        description = ""
    }

    constructor(name: String, beaconID: Int, long: Double, lat: Double, description: String){
        this.name = name
        this.beaconID = beaconID
        this.long = long
        this.lat = lat
        this.description = description
    }

}