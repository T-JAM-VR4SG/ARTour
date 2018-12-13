package bmtreuherz.artour.Activities

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraDevice
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import bmtreuherz.artour.DTOs.Feature
import bmtreuherz.artour.R
import bmtreuherz.artour.Utilities.HttpClient
import android.media.MediaPlayer
import android.os.Handler
import android.widget.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.success
import com.github.kittinunf.result.failure
import com.google.gson.JsonParser
import java.io.File
import android.net.Uri
import android.view.SurfaceHolder
import android.view.ViewGroup

class DescriptionActivity : AppCompatActivity() {

    private lateinit var featureTitleTV: TextView
    private lateinit var descriptionTitleTV: TextView
    private lateinit var featureImage: ImageView


    //audio variables
    private lateinit var audioSlider: SeekBar
    private lateinit var playButton: Button
    lateinit var audioPlayer: MediaPlayer
    var seekHandler = Handler()
    var wasPlaying = false

    var filename = ""
    var searchURL = "https://ufind.clas.ufl.edu/wp-json/wp/v2/media?search="
    var audioURL = ""
    var imageURL = ""
    var imageName = ""

    companion object {
        val TAG = DescriptionActivity::class.java.simpleName
        val BEACON_ID = "BeaconID"
    }

    fun getJson() {
        // Get location of JSON
        val url = searchURL + "Languages.json"

        Fuel.get(url).response { request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var jsonObject = jsonString.asJsonObject
                var languagesURL = jsonObject.getAsJsonObject("guid").get("rendered").toString()
                languagesURL = languagesURL.substring(1, languagesURL.length - 1)

                var beaconID = intent.getIntExtra(BEACON_ID, -1)

                // Find location of description file
                Fuel.get(languagesURL).response {
                    request, response, result ->
                    result.success {
                        var json = String(response.data)
                        val jsonString = JsonParser().parse(json)
                        var jsonObject = jsonString.asJsonObject
                        var test = jsonObject.getAsJsonObject("Locations").getAsJsonObject(beaconID.toString())
                        descriptionTitleTV.text = test.get("Name").toString()
                        descriptionTitleTV.text = descriptionTitleTV.text.substring(1, descriptionTitleTV.text.length-1)

                        var name = test.get("Filename").toString()

                        var currLang = Preferences.getLang()
                        var prefix = jsonObject.getAsJsonObject("Languages").getAsJsonObject(currLang).get("Prefix").toString()

                        imageName = name.substring(1, name.length-1)
                        filename = prefix.substring(1, prefix.length-1) + "_" + imageName
                    }

                    result.failure {
                        filename = "fail"
                        descriptionTitleTV.text = "Unable to retrieve data"
                    }
                }
            }

            result.failure {
                filename = "fail"
            }
        }
    }

    fun getDescription() {
        var url = searchURL + filename + ".txt"

        Fuel.get(url).response {
            request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var jsonObject = jsonString.asJsonObject
                var descriptionURL = jsonObject.getAsJsonObject("guid").get("rendered").toString()
                descriptionURL = descriptionURL.substring(1, descriptionURL.length - 1)

                Fuel.get(descriptionURL).response {
                    request, response, result ->
                    result.success {
                        featureTitleTV.setText(String(response.data))
                    }
                }
            }
            result.failure {
                featureTitleTV.text = "Unable to retrieve data"
            }
        }
    }

    fun getAudio() {
        var url = searchURL + filename + ".mp3"
        Fuel.get(url).response {
            request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var jsonObject = jsonString.asJsonObject
                audioURL = jsonObject.getAsJsonObject("guid").get("rendered").toString()
                audioURL = audioURL.substring(1, audioURL.length - 1)

                var F = File(getFilesDir(), "test.mp3")
                Fuel.download(audioURL).destination { response, url ->
                    F
                }.response { req, res, result ->

                }

                audioPlayer = MediaPlayer()
                audioPlayer.setDataSource(F.absolutePath)
                audioPlayer.prepare()
                audioPlayer.start()
                audio_setup()
            }
        }
    }

    fun getImage() {
        var url = searchURL + imageName + ".jpeg"

        Fuel.get(url).response {
            request, response, result ->
            result.success {
                var json = String(response.data)
                json = json.substring(1, json.length - 1)
                val jsonString = JsonParser().parse(json)
                var jsonObject = jsonString.asJsonObject
                imageURL = jsonObject.getAsJsonObject("guid").get("rendered").toString()
                imageURL = imageURL.substring(1, imageURL.length - 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        featureTitleTV = findViewById(R.id.feature_title)
        descriptionTitleTV = findViewById(R.id.description_title)
        featureImage = findViewById(R.id.feature_image)
        audioSlider = findViewById(R.id.audio_seekbar)
        playButton = findViewById(R.id.audio_button)


        getJson()

        while (filename == "") {
        }

        getDescription()
        getAudio()

    }

    //audio functions
    fun audio_setup() {
        //creates the media player and slider objects
        var time = 0


        //looks for when the seekbar is used
        audioSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Gets the length of the current audio file
                seekBar.setMax(audioPlayer.getDuration() / 1000)

                if(fromUser) {
                    audioPlayer.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (audioPlayer.isPlaying() == true) {
                    audioPlayer.pause()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (wasPlaying == true) {
                    audioPlayer.start()
                }
            }
        })

        seekUpdate()

        playButton.setOnClickListener {
            audioPlayer.isLooping = false
            if (audioPlayer.isPlaying() == true) {
                playButton.setBackgroundResource(R.drawable.play_button_icon)
                time = audioPlayer.getCurrentPosition()
                audioPlayer.pause()
                wasPlaying = false
            } else if (audioPlayer.isPlaying() == false) {
                playButton.setBackgroundResource(R.drawable.ic_audio_pause)
                audioPlayer.seekTo(time)
                audioPlayer.start()
                wasPlaying = true
            }
        }
    }

    var run:Runnable = object:Runnable {
        public override fun run() {
            seekUpdate()
        }
    }

    fun seekUpdate() {
        audioSlider.setProgress(audioPlayer.getCurrentPosition() / 1000)
        seekHandler.postDelayed(run, 1000)
    }

    override fun onPause() {
        super.onPause()
        seekHandler.removeCallbacks(run)

        try {
            audioPlayer.stop()
            audioPlayer.release()
        }
        catch(e: Exception) {

        }
    }
}



