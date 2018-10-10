package bmtreuherz.artour.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import bmtreuherz.artour.DTOs.Feature
import bmtreuherz.artour.R
import bmtreuherz.artour.Utilities.HttpClient
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.android.synthetic.main.nav_header.*
import bmtreuherz.artour.R.id.imageView
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.*


class DescriptionActivity : AppCompatActivity() {

    private lateinit var featureTitleTV: TextView
    private lateinit var descriptionTitleTV: TextView
    private lateinit var featureImage: ImageView

    //audio variables
    private lateinit var audioSlider: SeekBar
    private lateinit var playButton: Button
    private lateinit var audioPlayer: MediaPlayer
    var seekHandler = Handler()
    var wasPlaying = false


    companion object {
        val TAG = DescriptionActivity::class.java.simpleName
        val BEACON_ID = "BeaconID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        featureTitleTV = findViewById(R.id.feature_title)
        descriptionTitleTV = findViewById(R.id.description_title)
        featureImage = findViewById(R.id.feature_image)
        audioSlider = findViewById(R.id.audio_seekbar)
        playButton = findViewById(R.id.audio_button)

    }

    override fun onResume() {
        super.onResume()

        var beaconID = intent.getIntExtra(BEACON_ID, -1)
        var feature: Feature? = null
        var features = HttpClient.getFeatures()
        features.forEach {
            if (it.beaconID == beaconID){
                feature = it
            }
        }


        descriptionTitleTV.text = feature?.name
        featureTitleTV.text = feature?.description


        val uri = "@drawable/"+feature?.imageLink
        val imageResource = resources.getIdentifier(uri, null, packageName)
        val res = resources.getDrawable(imageResource)
        featureImage.setImageDrawable(res)


        val audio_uri:String = "@raw/"+feature?.audioLink
        val audio_resource = resources.getIdentifier(audio_uri, null, packageName)
        audioPlayer = MediaPlayer.create(this, audio_resource)
        audio_setup()


        //featureImage.setImageDrawable(feature?.imageLink);
        //featureImage.setImageDrawable()
        //change image here


        Log.d(TAG, "Found feature: " + feature?.name)
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
        audioPlayer.stop()
        audioPlayer.release()
    }
}


