package com.example.courage

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
class LogAdapter(private val context: Activity, private val nameArray: MutableList<String>, private val phoneArray: MutableList<String>, private val distanceArray: MutableList<String>)
    : ArrayAdapter<String>(context, R.layout.log_of_success, nameArray) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.log_of_success, null, true)

        val name = rowView.findViewById(R.id.name) as TextView
        val phone = rowView.findViewById(R.id.phone) as TextView
        val distance = rowView.findViewById(R.id.range) as TextView

        name.text = nameArray[position]
        phone.text=phoneArray[position]
        val number3digits:Double = String.format("%.3f", distanceArray[position].toDouble()).toDouble()
        distance.text = number3digits.toString()+" Km"

        return rowView
    }
}