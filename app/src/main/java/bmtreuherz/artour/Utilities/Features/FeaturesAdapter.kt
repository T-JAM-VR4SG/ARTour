package bmtreuherz.artour.Utilities.Features

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import bmtreuherz.artour.DTOs.Feature
import bmtreuherz.artour.R

/**
 * Created by Bradley on 3/27/18.
 */
class FeaturesAdapter : ArrayAdapter<Feature> {

    constructor(context: Context, foundPlayers: ArrayList<Feature>): super(context, 0, foundPlayers)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get the feature at this position
        var feature = getItem(position)

        // If this isn't a recycled view, inflate the view
        var view = convertView
        if (view == null){
            var inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.feature_cell, parent, false)
        }

        // Get the UI elements
        var nameTV = view?.findViewById<TextView>(R.id.featureNameTV)

        // Populate the data
        nameTV?.text = feature.name

        return view!!
    }
}