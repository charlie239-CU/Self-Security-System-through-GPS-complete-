package com.example.courage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Contact : AppCompatActivity() {
    private val cList = ArrayList<ContactsDetail>()
    private lateinit var contactViewAdapter: ContactViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_contact)
         var recylerView=findViewById<RecyclerView>(R.id.contacts_view)
        contactViewAdapter = ContactViewAdapter(cList)
        val layoutManager=LinearLayoutManager(applicationContext)
        recylerView.layoutManager=layoutManager
        recylerView.itemAnimator=DefaultItemAnimator()
        recylerView.adapter=contactViewAdapter
        addCList()

    }
    private fun addCList() {
        var list = ContactsDetail("Shailesh", "812373712", "shaileshrai2@gmail.com","Ranchi, jharkhand",false)
        cList.add(list)
        list = ContactsDetail("Vivek", "8126279903", "rawatvivek239@gmail.com","kotdwra, Uttarakhand",false)
        cList.add(list)
        contactViewAdapter.notifyDataSetChanged()
    }
}