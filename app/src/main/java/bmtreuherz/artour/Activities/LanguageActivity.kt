package bmtreuherz.artour.Activities

import android.os.Bundle
import android.view.View
import bmtreuherz.artour.R
import android.widget.RadioGroup
import android.widget.RadioButton
import bmtreuherz.artour.Utilities.BeaconEventBroadcaster.context
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Created by Ben on 3/21/2018.
 */
class LanguageActivity : NavigableActivity() {

    private lateinit var radioGroup : RadioGroup

    var searchURL = "https://ufind.clas.ufl.edu/wp-json/wp/v2/media?search="
    var jsonObject = JsonObject()
    var availableLanguages = ArrayList<String>()
    var numberOfLanguages = 0
    var radioButtons = HashMap<Int, String>()

    fun parse(toParse: JsonObject)  {
        var set = toParse.entrySet()

        set.forEach {
            var toAdd = it.key.toString()
            availableLanguages.add(toAdd)
        }
    }

    fun getLanguages() {
        // Get location of JSON
        val url = searchURL + "Languages.json"

        Fuel.get(url).response { request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var jsonObject2 = jsonString.asJsonObject
                var languagesURL = jsonObject2.getAsJsonObject("guid").get("rendered").toString()
                languagesURL = languagesURL.substring(1, languagesURL.length - 1)

                // Find location of description file
                Fuel.get(languagesURL).response {
                    request, response, result ->
                    result.success {
                        var json = String(response.data)
                        val jsonString = JsonParser().parse(json)
                        jsonObject = jsonString.asJsonObject.getAsJsonObject("Languages")
                        numberOfLanguages = jsonObject.size()
                    }

                    result.failure {
                    }
                }
            }

            result.failure {
            }
        }
    }

    override fun getCurrentMenuItemID(): Int {
        return R.id.nav_language
    }

    override fun setLayout() {
        setContentView(R.layout.activity_language)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioGroup = findViewById(R.id.radioGroup)

        if (numberOfLanguages == 0) {
            getLanguages()

            while (jsonObject.size() == 0) {

            }

            parse(jsonObject)
        }

        availableLanguages.forEach {
            var toAdd = RadioButton(context)
            toAdd.id = View.generateViewId()
            toAdd.setText(it)
            toAdd.setOnClickListener {
                onCheckboxClicked(it)
            }
            radioButtons.put(toAdd.id, it)
            radioGroup.addView(toAdd)
        }
    }

    override fun onResume() {
        super.onResume()

        var currLang = Preferences.getLang()

        radioButtons.forEach {
            var view : RadioButton = radioGroup.findViewById(it.key)

            view.isChecked = (it.value == currLang)
        }
    }

    //  Sets other checkboxes empty when one checkbox is clicked
    //Ensures only one option can be selected
    fun onCheckboxClicked(view: View) {
        var currID = view.getId()

        radioButtons.forEach {
            var view : RadioButton = radioGroup.findViewById(it.key)

            if (view.id == currID) {
                Preferences.setLang(it.value)
            }
            else {
                view.isChecked = false
            }
        }

    }
}