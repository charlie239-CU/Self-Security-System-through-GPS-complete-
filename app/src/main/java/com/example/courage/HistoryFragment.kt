package com.example.courage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.eleven.HistoryListAdapter
import com.example.eleven.RequestListAdapter


class HistoryFragment : Fragment() {

    private lateinit var listView: ListView
    val name = mutableListOf<String>()
    val time = mutableListOf<Long>()
    val status= mutableListOf<String>()
    val userId= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView=view.findViewById(R.id.listViewOfRequest)
        if(Helper.notificationHistoryListData.size!=0) {

            for(d in Helper.notificationHistoryListData){
                name.add(d.username.toString())
                time.add(d!!.time!!.toLong())
                status.add(d.flag.toString())
                userId.add(d.userId.toString())
            }


            val requestListAdapter =HistoryListAdapter(this.requireActivity(), name, time,status,userId)
            listView.adapter = requestListAdapter
        }
    }
}