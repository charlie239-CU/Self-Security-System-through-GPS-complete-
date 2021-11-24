package com.example.courage
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBindings
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.android.SphericalUtil
import org.w3c.dom.Text
import java.util.*

import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.location.Location
import android.os.*
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import kotlin.system.exitProcess


class MenuFragment : Fragment() {
    lateinit var addContact:Button
    lateinit var totalUserText:TextView
    lateinit var inRangeUserText:TextView
    lateinit var nearestUserText:TextView
    lateinit var descriptionText:TextView
    lateinit var messageButton:Button
    lateinit var headerForLogLayout:LinearLayout
    private var email:String=""
    lateinit var map:GoogleMap
    lateinit var mapButton:Button
    lateinit var sosButton:Button
    private val SMSpermissionRequest = 101
    private var username:String=""
    private var countUserNearBy=0
    private lateinit var activity:Activity
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var location: Location
    private var nearestUser:Double=-1.0
    private var userDataWithin:MutableList<User> = mutableListOf()
    private lateinit var listView:ListView
    private var nameList:MutableList<String> = mutableListOf()
    private var phoneList:MutableList<String> = mutableListOf()
    private var distanceList:MutableList<String> = mutableListOf()
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addContact=view.findViewById<Button>(R.id.nav_contact_button)
        mapButton=view.findViewById(R.id.mapButton)
        messageButton=view.findViewById(R.id.nav_message_button)
        sosButton=view.findViewById(R.id.sosButton)

        inRangeUserText=view.findViewById(R.id.inRangeUsers)
        totalUserText=view.findViewById(R.id.totalusers)
        nearestUserText=view.findViewById(R.id.nearest_user)
        listView=view.findViewById(R.id.listview)
        descriptionText=view.findViewById(R.id.description)
        headerForLogLayout=view.findViewById(R.id.headerForLogs)

        descriptionText.visibility=View.GONE
        headerForLogLayout.visibility=View.GONE

        auth = Firebase.auth
        database = Firebase.database.reference
        activity=requireActivity()

        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()
        val sharedPreference =  activity.getSharedPreferences("Contact_sqlite", Context.MODE_PRIVATE)
        val databaseHandler: DatabaseHandler= DatabaseHandler(activity)

        if(nameList.size!=0 && phoneList.size!=0 && distanceList.size!=0) {
            val myListAdapter = LogAdapter(this.activity, nameList, phoneList, distanceList)
            listView.adapter = myListAdapter
        }


        val user=auth.currentUser
        if (user == null) {
            val intent=Intent(activity,Login::class.java)
            startActivity(intent)
        }
        user?.let{
            username=user.displayName.toString()
            email=user.email.toString()
        }


        getTotalUsersWithinRange()
        val number3digits:Double = String.format("%.3f", nearestUser).toDouble()
        nearestUserText.setText("Nearest User/Contact to you:$number3digits Km")



        addContact.setOnClickListener {
            var intent=Intent(view.context,Contact::class.java)
            startActivity(intent)
        }
        mapButton.setOnClickListener {
            var intent=Intent(view.context,MapsActivity::class.java)
            startActivity(intent)
        }

        messageButton.setOnClickListener {
            var intent=Intent(view.context,InboxFragment::class.java)
            startActivity(intent)
        }

        val dialog: AlertDialog = AlertDialog.Builder(activity)
            .setTitle("SOS Alert")
            .setMessage("Do you really want to call for help?")
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    nameList= mutableListOf()
                    phoneList= mutableListOf()
                    distanceList= mutableListOf()
                    //sendMessage()
                    messagetoUsersInRange()
                    Snackbar.make(
                        view,
                        "SOS Sent to your contacts and Users nearby",
                        Snackbar.LENGTH_LONG
                    ).show()

                })
            .setNegativeButton(android.R.string.no, null)
            .create()
        dialog.setOnShowListener(object : OnShowListener {
            private val AUTO_DISMISS_MILLIS = 6000
            override fun onShow(dialog: DialogInterface) {
                val defaultButton: Button =
                    (dialog as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE)
                val negativeButtonText = defaultButton.text

            }
        })

        sosButton.setOnClickListener {

           dialog.show()

//
//            Log.d("location",uri)
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            intent.setPackage("com.google.android.apps.maps")
//            startActivity(intent)
        }



        totalUserText.setText(Helper.userList.size.toString()+" users")
    }





    fun getTotalUsersWithinRange(){
        for(list in Helper.userList){
           Log.d("nearby", list.toString())
            if(list.getEmail()!=email){
                userWithin3km(list.mapLocation?.get("lat")!!.toDouble(),list.mapLocation?.get("long")!!.toDouble(),list)
            }
        }
        inRangeUserText.setText(countUserNearBy.toString()+" users")
        Log.d("nearby", countUserNearBy.toString())

    }



    fun userWithin3km(latitude:Double,longitude:Double,list:User){
        val zoomlevel=15f
        // Add a marker in Sydney and move the camera
        val cLat=Helper.userData.mapLocation?.get("lat").toString().toDouble()
        val cLong=Helper.userData.mapLocation?.get("long").toString().toDouble()


        val dist=findDistance(latitude, longitude)
        if(nearestUser==-1.0){
            nearestUser=dist
        }
        else{
            nearestUser=Math.min(nearestUser,dist)
        }

        if(dist<3){
            userDataWithin.add(list)
           // Log.d("nearby",String.format("%.2f", distance / 1000) + "km")
            //Toast.makeText(this.requireContext(),"Distance between Sydney and Brisbane is \n " + String.format("%.2f", distance / 1000) + "km", Toast.LENGTH_SHORT).show();
            countUserNearBy++
        }
    }


    fun findDistance(latitude: Double,longitude: Double):Double{
        val cLat=Helper.userData.mapLocation?.get("lat").toString().toDouble()
        val cLong=Helper.userData.mapLocation?.get("long").toString().toDouble()


        val currentUser = LatLng(cLat, cLong)
        val anotherUser=LatLng(latitude,longitude)
        val distance= SphericalUtil.computeDistanceBetween(currentUser,anotherUser)
        //Log.d("nearby",String.format("%.2f", distance / 1000) + "km")
        return distance / 1000;
    }

    fun sendMessage() {
        val permissionCheck = ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            myMessage()
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.SEND_SMS),
                SMSpermissionRequest)
        }
    }


    private fun myMessage() {
        val cLat=Helper.userData.mapLocation?.get("lat").toString().toDouble()
        val cLong=Helper.userData.mapLocation?.get("long").toString().toDouble()
            //val ="This is a dummy message to check my application; There could be some more spam messages! sorry"
            val databaseHandler: DatabaseHandler= DatabaseHandler(activity)
            val contacts: List<ContactStructure> = databaseHandler.viewContacts()
            for(contact in contacts){
                val mymsg: String = java.lang.String.format(
                    Locale.ENGLISH,
                    "http://maps.google.com/maps?&daddr=%f,%f (%s)",
                    cLat,
                    cLong,
                    Helper.userData.name+" Need Help!!"
                )
            if (TextUtils.isDigitsOnly(contact.phone.toString()) && contact.phone!!.length == 10) {
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(contact.phone.toString(), null, mymsg, null, null)

               Log.d( "Message Sent","yes");
            } else {
                Log.d( "Message Sent","No ");
                Toast.makeText(this.requireContext(), "Please enter the correct number", Toast.LENGTH_SHORT).show()
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun messagetoUsersInRange(){
        for(list in userDataWithin){
            val mymsg: String = java.lang.String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?&daddr=%f,%f (%s)",
                list.mapLocation!!.get("lat")!!.toDouble(),
                list.mapLocation.get("long")!!.toDouble(),
                Helper.userData.name+":I Need Help!!"
            )
//            if (TextUtils.isDigitsOnly(list.phone.toString()) && list.phone!!.length == 10) {
//                val smsManager: SmsManager = SmsManager.getDefault()
//                smsManager.sendTextMessage(list.phone.toString(), null, mymsg, null, null)
//                nameList.add(list.name.toString())
//                phoneList.add(list.phone.toString())
//                distanceList.add(findDistance(list.mapLocation.get("lat")!!.toDouble(),list.mapLocation.get("long")!!.toDouble()).toString())
//                Log.d( "Message Sent","yes");
//            } else {
//                Log.d( "Message Sent","No ");
//                Toast.makeText(this.requireContext(), "Please enter the correct number", Toast.LENGTH_SHORT).show()
//            }
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
            )

            val date=Date()
            val time:Long=date.time

            val sosData:NotificationStructure= NotificationStructure(list.name.toString(),mymsg,"false",time.toString(),username)
            database.child("notification").child(list.username.toString()).child(username).setValue(sosData)
                nameList.add(list.name.toString())
                phoneList.add(list.phone.toString())
                distanceList.add(findDistance(list.mapLocation.get("lat")!!.toDouble(),list.mapLocation.get("long")!!.toDouble()).toString())
        }
        if(nameList.size!=0 && phoneList.size!=0 && distanceList.size!=0) {
            val myListAdapter = LogAdapter(this.activity, nameList, phoneList, distanceList)
            listView.adapter = myListAdapter
            descriptionText.visibility=View.VISIBLE
            headerForLogLayout.visibility=View.VISIBLE
        }
        else{
            Helper.makeToast(activity,"There is no user within 2km range").show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:
    IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMSpermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myMessage();
            } else {
                Toast.makeText(this.requireContext(), "You don't have required permission to send a message",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}