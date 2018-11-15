package bmtreuherz.artour.Utilities

import bmtreuherz.artour.Activities.DescriptionActivity
import bmtreuherz.artour.Activities.Preferences
import bmtreuherz.artour.DTOs.Feature
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Created by Bradley on 3/19/18.
 */
object HttpClient {
    var filename = ""
    var searchURL = "https://ufind.clas.ufl.edu/wp-json/wp/v2/media?search="
    var size = 0
    var features = ArrayList<Feature>()
    var jsonObject = JsonObject()

    fun parse(toParse: JsonObject)  {
        var set = toParse.entrySet()

        set.forEach {
            var toAdd = Feature()
            toAdd.beaconID = it.key.toInt()

            var contents = it.value.toString()

            var parsedContents = JsonParser().parse(contents).asJsonObject

            toAdd.name = parsedContents.get("Name").toString()
            toAdd.name = toAdd.name.substring(1, toAdd.name.length-1)
            toAdd.long = parsedContents.get("Long").asDouble
            toAdd.lat = parsedContents.get("Lat").asDouble
            features.add(toAdd)
        }
    }

    fun getJson() {
        // Get location of JSON
        val url = searchURL + "Languages.json"

        Fuel.get(url).response { request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var languagesURL = jsonString.asJsonObject.
                        getAsJsonObject("guid").get("rendered").toString()

                languagesURL = languagesURL.substring(1, languagesURL.length - 1)

                // Find location of description file
                Fuel.get(languagesURL).response {
                    request, response, result ->
                    result.success {
                        var json = String(response.data)
                        val jsonString = JsonParser().parse(json)

                        jsonObject = jsonString.asJsonObject.getAsJsonObject("Locations")
                        size = jsonObject.size()
                    }

                    result.failure {
                        filename = "fail"
                    }
                }
            }

            result.failure {
                filename = "fail"
            }
        }
    }


    fun getFeatures(): List<Feature> {
        if (size == 0) {
            getJson()
            while (jsonObject.size() == 0) {

            }
            parse(jsonObject)
        }

        return features
    }
}
