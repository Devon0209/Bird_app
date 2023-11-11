package com.example.myapplication.Sightings

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.myapplication.R

class SightingAdapter(private val context: Context, private val sightingsList: List<SightingModel>) : BaseAdapter() {



    override fun getCount(): Int {
        return sightingsList.size
    }

    override fun getItem(position: Int): Any {
        return sightingsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val sighting = sightingsList[position]

        val view = View.inflate(context, R.layout.list_item_sighting, null)

        val textBird = view.findViewById<TextView>(R.id.textBird)
        val textDate = view.findViewById<TextView>(R.id.textDate)
        val textLocation = view.findViewById<TextView>(R.id.textLocation)
        val textDescription = view.findViewById<TextView>(R.id.textDescription)

        textBird.text = "Bird: ${sighting.bird}"
        textDate.text = "Date: ${sighting.date}"
        textLocation.text = "Location: ${sighting.location}"
        textDescription.text = "Description: ${sighting.description}"

        return view
    }
}
