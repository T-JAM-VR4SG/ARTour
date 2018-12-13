package bmtreuherz.artour.Activities

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import bmtreuherz.artour.R
import android.support.v7.app.AlertDialog

class AlertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_description)
        val builder = AlertDialog.Builder(this@AlertActivity, R.style.DescriptionActivityTheme)
        builder.create()
        //We will probably want to add vibration to alert user when dialog happens
        builder.setTitle("Nearby Location Detected")
        builder.setMessage("Bring up camera view?")
        builder.setPositiveButton("Yes", { dialogInterface: DialogInterface, i: Int ->
            //Take the user to desription page for specific beacon here

            var intent = Intent (this@AlertActivity, HelloSceneformActivity::class.java)
            startActivity(intent)

        })
        builder.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int ->
            //If the user clicks no, exit the dialog and do nothing
            var intent = Intent ( this@AlertActivity, MapActivity::class.java)
            startActivity(intent)
        })
        builder.show()

    }
}



