package com.example.eleven

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.courage.R
import java.util.*
import java.util.concurrent.TimeUnit

class HistoryListAdapter (private val context: Activity, private val title1: List<String>,private val title2: List<Long>,private val title3:List<String>,private val title4:List<String>)
    : ArrayAdapter<String>(context, R.layout.history_list_view, title1) {

    private lateinit var nameField: TextView
    private lateinit var timeField: TextView
    private lateinit var statusField: TextView
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.history_list_view, null, true)


        nameField= rowView!!.findViewById(R.id.sosUser)
        timeField= rowView!!.findViewById(R.id.sosTime)
        statusField= rowView!!.findViewById(R.id.status)



        nameField.text = title4[position]
        statusField.text = title3[position]
        timeField.text = findTimeRemain(title2[position])



        return rowView
    }

    fun findTimeRemain(time2:Long):String{
        var time1: Long=Date().time
        var diff=(time1-time2)
        Log.d("timedata",(time2/(1000*60*60)).toString())
        val date= Date()
        val time:Long=date.time
        diff= TimeUnit.MILLISECONDS.toHours(diff)


        if(diff<1){
            return "Time: few minutes ago"
        }
        else if(diff<24){

            return "Time: $diff hours ago"
        }
        else{
            time1=Date().time
            diff=(time1-time2)
            diff= TimeUnit.MILLISECONDS.toDays(diff)
            return return "Time: $diff days ago"

        }


    }
}


