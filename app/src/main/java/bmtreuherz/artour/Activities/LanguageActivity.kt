package bmtreuherz.artour.Activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import bmtreuherz.artour.R
import kotlinx.android.synthetic.main.content_language.*

/**
 * Created by Ben on 3/21/2018.
 */
class LanguageActivity : NavigableActivity() {

    override fun getCurrentMenuItemID(): Int {
        return R.id.nav_language
    }

    override fun setLayout() {
        setContentView(R.layout.activity_language)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        when (Preferences.getLang()){
            "English" -> {
                checkEnglish.isChecked = true
                checkSpanish.isChecked = false
                checkChinese.isChecked = false

            }
            "Spanish" -> {
                checkEnglish.isChecked = false
                checkSpanish.isChecked = true
                checkChinese.isChecked = false

            }
            "Chinese" -> {
                checkEnglish.isChecked = false
                checkSpanish.isChecked = false
                checkChinese.isChecked = true
            }
        }
    }

    //  Sets other checkboxes empty when one checkbox is clicked
    //Ensures only one option can be selected
    fun onCheckboxClicked(view: View) {

        when (view.getId()) {

            R.id.checkEnglish -> {

                //setLanguage("English")
                checkChinese.setChecked(false)
                checkSpanish.setChecked(false)
                Preferences.setLang("English")
            }
            R.id.checkSpanish -> {
                //setLanguage("Spanish")

                checkChinese.setChecked(false)
                checkEnglish.setChecked(false)
                Preferences.setLang("Spanish")
            }
            R.id.checkChinese -> {
                //setLanguage("Chinese")

                checkEnglish.setChecked(false)
                checkSpanish.setChecked(false)
                Preferences.setLang("Chinese")
            }

        }
    }
}