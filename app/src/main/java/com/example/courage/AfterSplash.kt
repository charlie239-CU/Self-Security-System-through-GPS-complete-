package com.example.courage

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.courage.databinding.ActivityAfterSplashBinding

class AfterSplash : AppCompatActivity() {

    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_splash)
        viewPager=findViewById(R.id.view_pager)
        val adapter=MyAdapter(this,supportFragmentManager,3)
        viewPager.adapter=adapter
        viewPager.setCurrentItem(1,true)


    }
}