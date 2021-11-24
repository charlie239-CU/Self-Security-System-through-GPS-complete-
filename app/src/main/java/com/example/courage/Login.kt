package com.example.courage

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    lateinit var emailField:EditText
    lateinit var passField:EditText
    lateinit var loginButton:Button
    lateinit var signUpButton: TextView
    private lateinit var database: DatabaseReference
    private lateinit var dbForNotification: DatabaseReference
    lateinit var logo:ImageView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)


        emailField=findViewById(R.id.email)
        passField=findViewById(R.id.password)
        loginButton=findViewById(R.id.login)
        signUpButton=findViewById(R.id.register)
        database = Firebase.database.reference
        logo=findViewById(R.id.logo)
        auth = Firebase.auth

        val handler = object:Helper.Companion.LocationCheckInterface{
            override fun onResult(result: Boolean) {
                //super.onResult(result)
                Helper.makeToast(this@Login,result.toString()).show()
            }

            override fun onError(error: String) {

            }
        }
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()


        Helper.locationRequestWithResult(this,handler)

        val currentUser = auth.currentUser
        if(currentUser != null){
            //Toast.makeText(baseContext, "already login.", Toast.LENGTH_SHORT).show()
            val intent=Intent(this,AfterSplash::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {


            val email:String=emailField.text.toString()
            val password:String=passField.text.toString()
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                return@setOnClickListener
                //Helper.makeToast(activity = Activity(),"Empty Email and password field")

            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Successfull login.",
                            Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser

                        //Firebase.auth.signOut()
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                       // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("login", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
        }
        signUpButton.setOnClickListener {
            var intent=Intent(this,singup::class.java)
            startActivity(intent)
        }
    }

    fun onLocationTask(task: Task<LocationSettingsResponse>){
        task.addOnSuccessListener { locationSettingsResponse ->
            Helper.makeToast(this,"wonderful").show()
        }
        task.addOnCanceledListener {
            Helper.makeToast(this,"Kindly accept request").show()
        }
        if(task.isSuccessful){
            Helper.makeToast(this,"wonderful").show()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {

                    exception.startResolutionForResult(
                        this,
                        Helper.REQUEST_CHECK_SETTINGS
                    )

                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


}