package com.example.courage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Contact : AppCompatActivity() {
    private val cList = ArrayList<ContactsDetail>()
    private lateinit var contactViewAdapter: ContactViewAdapter
    private lateinit var addContactButton: Button
    private lateinit var usernameText: TextView

    private lateinit var database: DatabaseReference
    lateinit var logo: ImageView
    private lateinit var auth: FirebaseAuth

    private var username:String=""
    private var email:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //supportActionBar!!.hide()
        setContentView(R.layout.activity_contact)
        addContactButton=findViewById(R.id.add_new_contact)
        usernameText=findViewById(R.id.username)
         var recylerView=findViewById<RecyclerView>(R.id.contacts_view)
        contactViewAdapter = ContactViewAdapter(cList)
        val layoutManager=LinearLayoutManager(applicationContext)
        recylerView.layoutManager=layoutManager
        recylerView.itemAnimator=DefaultItemAnimator()
        recylerView.adapter=contactViewAdapter
        addCList()
        this?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent:Intent=Intent(this@Contact,AfterSplash::class.java)
                startActivity(intent)
                // in here you can do logic when backPress is clicked
            }
        })
        auth = Firebase.auth
        Helper.createLocationRequest(this)
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
        val user=auth.currentUser
        user?.let {
            username = user.displayName.toString()
            email = user.email.toString()
        }
        usernameText.setText(Helper.userData.name.toString())


        addContactButton.setOnClickListener {
            val intent=Intent(this,FillContact::class.java)
            startActivity(intent)
        }

    }
    private fun addCList() {
//        if(Helper.userData.contacts==""){
//            return
//        }

//        val data=Helper.userData.contacts as Map<String,Map<String,String>>
//        if(data.isEmpty()){
//            return
//        }
//        for((k,v) in data){
//            var list = ContactsDetail(v.get("name").toString(), v.get("phone").toString(), v.get("email").toString(),v.get("address").toString(),false)
//            cList.add(list)
//        }

//        list = ContactsDetail("Vivek", "8126279903", "rawatvivek239@gmail.com","kotdwra, Uttarakhand",false)
//        cList.add(list)

        val databaseHandler: DatabaseHandler= DatabaseHandler(this)
        val contacts: List<ContactStructure> = databaseHandler.viewContacts()
        for(data in contacts){
            var list = ContactsDetail(data.name.toString(),data.phone.toString(),data.email.toString(),data.address.toString(),false)
            cList.add(list)
        }
        contactViewAdapter.notifyDataSetChanged()
    }
}