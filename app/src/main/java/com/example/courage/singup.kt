package com.example.courage

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import java.lang.Exception
import java.util.*


class singup : AppCompatActivity() {

    public interface ResultListener {
        fun onResult(isAdded: Boolean)
        fun onError(error: String)
    }


    protected val REQUEST_CHECK_SETTINGS = 0x1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var requestingLocationUpdates:Boolean=false

    private lateinit var locationCallback: LocationCallback
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var nameField:EditText
    lateinit var usernameField:EditText
    lateinit var emailField:EditText
    lateinit var passField:EditText
    lateinit var phoneField:EditText
    lateinit var login:TextView
    lateinit var signup:Button
    companion object{
        public const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private val MY_PERMISSION_FINE_LOCATION = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //getSupportActionBar()!!.hide(); // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_singup)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        nameField=findViewById(R.id.name)
        usernameField=findViewById(R.id.username)
        emailField=findViewById(R.id.email)
        passField=findViewById(R.id.password)
        phoneField=findViewById(R.id.phone)
        login=findViewById(R.id.login)
        signup=findViewById(R.id.register)
        database = Firebase.database.reference
        auth= Firebase.auth
        Helper.createLocationRequest(this)
        getLocationPermission()
        database=FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
        val activity=this
        signup.setOnClickListener {
            val email=emailField.text.toString()
            val password=passField.text.toString()
            val username=usernameField.text.toString()
            val name=nameField.text.toString()
            val phone=phoneField.text.toString()
            if(TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(username) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)){
                //Helper.makeToast(activity = Activity(),"Empty field")
                return@setOnClickListener
            }
            val handler=object:ResultListener{
                override fun onResult(isAdded: Boolean) {
                    if(!isAdded){
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(activity,"successfull",Toast.LENGTH_SHORT).show()
                                    val user = auth.currentUser
                                    writeNewUser(username, name, phone, email)
                                    val profileUpdates: UserProfileChangeRequest =
                                        UserProfileChangeRequest.Builder().setDisplayName(username).build()
                                    user!!.updateProfile(profileUpdates);
                                    val intent= Intent(activity,UpdateProfile::class.java)
                                    intent.putExtra("username",username)
                                    intent.putExtra("email",email)
                                    startActivity(intent)

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                                    // updateUI(null)
                                }
                            }

                    }
                    else{
                        Helper.makeToast(activity ,"Username Exist").show()
                    }
                }
                override fun onError(error: String) {

                }
            }
            val t=userExist(username,handler)

        }


        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent=Intent(this,Login::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {
            val intent=Intent(this,Login::class.java)
            startActivity(intent)

        }

    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            requestingLocationUpdates = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                singup.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun userExist(user:String,resultListener: ResultListener){
        var exist:Boolean=false
        database.child("users").get().addOnSuccessListener {
            if(it==null){
                Log.e(TAG,"User data is null")
                resultListener.onResult(false)
                resultListener.onError("Error")
                return@addOnSuccessListener
            }
            try {
                val td: Map<String, Any> = it.getValue() as Map<String, Any>

                //val values: List<Any> = td.values as List<Any>

                for ((k, v) in td) {

                    if (k.toString() == user) {
                        exist = true
                        resultListener.onResult(true)
                    }
                }
                if (!exist) {
                    resultListener.onResult(false)
                }
            }
            catch(e:Exception){
                resultListener.onResult(false)
                resultListener.onError("Error")
            }
        }

    }

    fun setEmptyFields(){
        nameField.setText("")
        phoneField.setText("")
        emailField.setText("")
        passField.setText("")
        usernameField.setText("")
    }

    fun writeNewUser(username:String, name: String, phone: String, email: String) {
        var lat:String=""
        var long:String=""
        val activity=this
        if (Helper.checkLocationPermission(this)) {
            Helper.setLocationPermission(this)
            setEmptyFields()
            Helper.makeToast(this,"accept the permissions and reenter the data").show()
        }
        else {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                }
            }
            if (requestingLocationUpdates) Helper.startLocationUpdates(this,fusedLocationClient,locationCallback)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {

                        lat= location!!.latitude.toString()
                        long= location!!.longitude.toString()
                        val mapLocation=mapOf<String,String>("lat" to lat,"long" to long)
                        val list= listOf<String>()
                        val user = User(name, email,phone,mapLocation,list,"false",username)
                        database.child("users").child(username).setValue(user)


                    }
                }
        }

    }



}


