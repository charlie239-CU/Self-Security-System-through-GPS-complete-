package com.example.courage

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import com.example.eleven.RequestListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class NotifyService : Service() {
    private lateinit var database: DatabaseReference
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    lateinit var builder: Notification.Builder
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        database = Firebase.database.reference.child("notification").child(Helper.usernameCurrent)
        Log.d("notification",Helper.usernameCurrent)
        // creating a media player which
        // will play the audio of Default
        // ringtone in android device
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.getValue<Any>()==null){
                    return
                }
                val post = dataSnapshot.getValue<Any>() as Map<String,Map<String,String>>
                    for((k,v) in post){
                        val flag: String? =post.get(k)!!.get("flag")
                        if(flag=="false"){
                            val p=post.get(k)
                            Log.d("servicerun", post.get(k)!!.get("username").toString())
                            val notifyData=NotificationStructure(username = p!!.get("username"),msg =p!!.get("msg"),
                                                                flag=p!!.get("flag"),time=p!!.get("time")!!.toString(),userId=p!!.get("userId")!!.toString())
                            Helper.notificationListData.add(notifyData)



                            sendNotification(intent,p!!.get("username")!!,post.get(k)!!.get("msg").toString())
                            //database.child(k).child("flag").setValue("false")
                        }
                        else if(flag=="rejected" || flag=="accepted"){
                            val p=post.get(k)
                            val notifyData=NotificationStructure(username = p!!.get("username"),msg =p!!.get("msg"),
                                flag=p!!.get("flag"),time=p!!.get("time")!!.toString(),userId=p!!.get("userId")!!.toString())
                            Log.d("servicerun", post.get(k)!!.get("username").toString())
                            Helper.notificationHistoryListData.add(notifyData)

                        }
                    }


                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)
        // returns the status
        // of the program
        return START_STICKY
    }

    // execution of the service will
    // stop on calling this method
    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onBind(intent: Intent): IBinder {

        TODO("Return the communication channel to the service.")
    }
    fun sendNotification(intent:Intent,user:String,msg:String){
        val intent=Intent(this,InboxFragment::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(packageName, R.layout.notification_remote)
        contentView.setTextViewText(R.id.sosUser,"User: $user")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

}