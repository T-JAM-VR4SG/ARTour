package bmtreuherz.artour.Activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import bmtreuherz.artour.R
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.app_bar_map.*

/**
 * Created by Bradley on 3/26/18.
 */
abstract class NavigableActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout()
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if (item.itemId != getCurrentMenuItemID()){
            when (item.itemId) {
                R.id.nav_map -> {
                    var intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_features_in_range -> {
                    var intent = Intent(this, FeaturesInRangeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_language -> {
                    var intent = Intent(this, LanguageActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    protected abstract fun getCurrentMenuItemID(): Int
    protected abstract fun setLayout()
}