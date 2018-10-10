package bmtreuherz.artour.Activities

import android.content.Context
import android.content.SharedPreferences
import bmtreuherz.artour.Utilities.BeaconEventBroadcaster.context

/**
 * Used to set all user preferences with setters and getters
 */

class Preferences (context: Context) {
    companion object {

        val PREFS_FILENAME = "artour.prefs"
        val LANGUAGE = "language"
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

        fun getLang(): String {
            //return prefs.getString(LANGUAGE, "english")
            return prefs.getString(LANGUAGE, "English")
        }

        fun setLang(lang: String) {
            val editor = prefs.edit()
            editor.putString(LANGUAGE, lang)
            editor.apply()
        }
    }
}