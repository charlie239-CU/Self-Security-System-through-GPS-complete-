package com.example.courage

import android.app.Activity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import com.example.courage.databinding.ActivityAfterSplashBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AfterSplash : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        //getSupportActionBar()!!.hide(); // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_after_splash)
        viewPager=findViewById(R.id.view_pager)
        val activity:Activity=this
        database = Firebase.database.reference
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()


        val adapter=MyAdapter(this,supportFragmentManager,2)
        viewPager.adapter=adapter
        viewPager.setCurrentItem(0,true)


    }
}