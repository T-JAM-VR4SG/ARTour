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
    var imageLink: String
    var audioLink: String

    constructor(name: String, beaconID: Int, long: Double, lat: Double, description: String, imageLink: String, audioLink: String){
        this.name = name
        this.beaconID = beaconID
        this.long = long
        this.lat = lat
        this.description = description
        this.imageLink = imageLink
        this.audioLink = audioLink
    }

}