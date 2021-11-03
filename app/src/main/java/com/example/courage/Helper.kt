package com.example.courage

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.ktx.getValue
import java.lang.Exception

public class Helper {


    companion object {

        interface ResultListener {
            fun onResult(result: Boolean)
            fun onError(error: String)
        }
        interface UserDataInterface {
            fun onResult(result: User)
            fun onError(error: String)
        }
        interface  UsernameDataInterface{
            fun onResult(result:List<String>)
            fun onError(error: String)
        }
        interface  UserListDataInterface{
            fun onResult(result:MutableList<User>)
            fun onError(error: String)
        }
        public var userData:User=User()
        public var usernameList:List<String> = listOf()
        public var userList:MutableList<User> = mutableListOf()
        protected val REQUEST_CHECK_SETTINGS = 0x1
        public  var locationRequest:LocationRequest=LocationRequest()
        public fun createLocationRequest(activity: Activity) {

            locationRequest = LocationRequest.create()?.apply {
                interval = 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }!!
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(activity)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener { locationSettingsResponse ->
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }

        public fun startLocationUpdates(activity:Activity, fusedLocationClient:FusedLocationProviderClient,locationCallback:LocationCallback) {

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

            }
            fusedLocationClient.requestLocationUpdates(Helper.locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }

        public fun checkLocationPermission(activity:Activity):Boolean{
            return (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
        }
        public fun setLocationPermission(activity: Activity){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                singup.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }

        public fun makeToast(activity:Activity,str:String):Toast{
            return Toast.makeText(activity,str,Toast.LENGTH_SHORT)
        }

        fun userExist(user:String,resultListener: ResultListener,database:DatabaseReference){
            var exist:Boolean=false
            database.child("users").get().addOnSuccessListener {
                if(it==null){
                    Log.e(ContentValues.TAG,"User data is null")
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
                catch(e: Exception){
                    resultListener.onResult(false)
                    resultListener.onError("Error")
                }
            }

        }
        fun getUserData(username:String,resultListener: UserDataInterface,database:DatabaseReference){
            var exist:Boolean=false
            database.child("users").child(username).get().addOnSuccessListener {

                try {
                    val user:User=it.getValue<User>() as User
                    userData=user
                    resultListener.onResult(user)
                }
                catch(e: Exception){
                    resultListener.onError("Error")
                }
            }

        }

        fun getUserName(username:String,resultListener: UsernameDataInterface,database:DatabaseReference){

            database.child("users").get().addOnSuccessListener {

                try {
                    val td: Map<String, User> = it.value as Map<String, User>
                    usernameList = ArrayList<String>(td.keys)
                    resultListener.onResult(usernameList)
                }
                catch(e:Exception){
                    resultListener.onError("exception")
                }

            }


        }
        fun getUserDataInList(database:DatabaseReference,resultListener: UserListDataInterface){
            Log.d("userlist", "4")
            for(user in usernameList){

                database.child("users").get().addOnSuccessListener {

                       val post=it.child(user).getValue<User>()
                    userList.add(post!!)
                    resultListener.onResult(userList)
                }
            }

        }
    }
}