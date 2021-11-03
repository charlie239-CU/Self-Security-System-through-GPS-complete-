package com.example.courage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class FillContact : AppCompatActivity() {
    data class CollectiveContact(
        val name:List<ContactStructure>
    )
    companion object {
        public val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    private lateinit var nameField:EditText
    private lateinit var emailField:EditText
    private lateinit var phoneField:EditText
    private lateinit var addressField:EditText
    private lateinit var addContactButton: Button
    private lateinit var spinner: Spinner

    private lateinit var database: DatabaseReference
    lateinit var logo: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var activity:Activity

    private var map:MutableMap<String,String> = mutableMapOf()
    private var username:String=""
    private var email:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
       // supportActionBar!!.hide()
        setContentView(R.layout.activity_fill_contact)
        nameField=findViewById(R.id.name)
        emailField=findViewById(R.id.email)
        phoneField=findViewById(R.id.phone)
        addressField=findViewById(R.id.address)
        addContactButton=findViewById(R.id.register)
        spinner=findViewById(R.id.spinnerContact)
        activity=this
        loadContacts()
        val names=map.keys.toTypedArray()
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, names
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    nameField.setText(names[position].toString())
                    var p=map.get(names[position].toString())!!.replace("[+()\\s-]+".toRegex(), "")
                    if(p.length==12){
                        p=p.drop(2)
                    }
                    phoneField.setText(p)
//                    Toast.makeText(
//                        this@FillContact,
//
//                        names[position].toString(), Toast.LENGTH_SHORT
//                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)
        val sharedPreference =  getSharedPreferences("Contact_sqlite", Context.MODE_PRIVATE)

        auth = Firebase.auth
        Helper.createLocationRequest(this)
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
        val user=auth.currentUser
        user?.let {
            username = user.displayName.toString()
            email = user.email.toString()
        }



        //}

        addContactButton.setOnClickListener {
            val name=nameField.text.toString()
            val email=emailField.text.toString()
            val phone=phoneField.text.toString()
            val address=addressField.text.toString()
            val contactData:ContactStructure=ContactStructure(name,email,address,phone)
            val status = databaseHandler.addContact(contactData)
            var editor = sharedPreference.edit()
            editor.putString("operation", "false")
            editor.commit()
            database.child("users").child(username).child("contacts").child(name).setValue(contactData)
                    Helper.makeToast(activity,"Successfully inserted")
                    val intent=Intent(activity,Contact::class.java)
                    startActivity(intent)
                    finish()


        }
    }
    public fun loadContacts() {
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                FillContact.PERMISSIONS_REQUEST_READ_CONTACTS
            )

        } else {
            map = ContactHelper.getContacts(this.contentResolver)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FillContact.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }
}