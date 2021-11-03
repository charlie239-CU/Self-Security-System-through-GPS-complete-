package com.example.courage

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var activity: Activity
    private lateinit var auth: FirebaseAuth
    private var username:String=""
    private var email:String=""
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)
        auth = Firebase.auth
        database = Firebase.database.reference
        activity=this
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
        val user=auth.currentUser
        if(user!=null) {
            user?.let {
                username = user.displayName.toString()
                email = user.email.toString()
            }
            val userDataHandler = object : Helper.Companion.UserDataInterface {
                override fun onResult(result: User) {

                    //Helper.makeToast(activity, Helper.userData.mapLocation?.get("long").toString()).show()
                }

                override fun onError(error: String) {
                    Log.d("errorOccured",error)
                }
            }
            val userListHandler = object : Helper.Companion.UserListDataInterface {
                override fun onResult(result: MutableList<User>) {
                    //Helper.userList=result
                    //Log.d("userlist", Helper.userList.toString())
                    val sharedPreference =  getSharedPreferences("Contact_sqlite", Context.MODE_PRIVATE)

                    if(Helper.userData.contacts=="" ){

                    }
                    else {
                        if(sharedPreference.getString("operation","")!="true") {
                            databaseHandler.deleteAllData()
                            Log.d("contacts",Helper.userData.contacts.toString())
                            if(Helper.userData.contacts.toString()!="null"){

                            val data = Helper.userData.contacts as Map<String, Map<String, String>>
                            for ((k, v) in data) {
                                Log.d("size", data.size.toString())

                                var list = ContactStructure(
                                    name = v["name"].toString(),
                                    email = v["email"].toString(),
                                    address = v["address"].toString(),
                                    phone = v["phone"].toString()

                                )
                                Log.d("checking", list.email.toString())
                                val status = databaseHandler.addContact(list)
                                if (status < 0) {
                                    Toast.makeText(
                                        applicationContext, "Error in sqlLite",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            }

                            var editor = sharedPreference.edit()
                            editor.putString("operation", "true")
                            editor.commit()
                        }
                    }
                    Handler().postDelayed({
                        val intent = Intent(activity, AfterSplash::class.java)
                        startActivity(intent)
                        finish()
                    }, 5000)
                }

                override fun onError(error: String) {
                }
            }

            val usernameHandler = object : Helper.Companion.UsernameDataInterface {
                override fun onResult(result: List<String>) {
                    Helper.usernameList = result
                    Log.d("userlist", "1")
                    Helper.getUserDataInList(database, userListHandler)
                }

                override fun onError(error: String) {
                }
            }
            Log.d("userlist", "0")
            Helper.getUserData(username, userDataHandler, database)
            Helper.getUserName("", usernameHandler, database)
        }
        else{
            Handler().postDelayed({
                val intent = Intent(activity, Login::class.java)
                startActivity(intent)
                finish()
            }, 5000)
        }




    }
}