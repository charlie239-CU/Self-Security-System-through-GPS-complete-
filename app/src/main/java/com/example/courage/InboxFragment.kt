package com.example.courage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.eleven.InboxTabAdapter
import com.google.android.material.tabs.TabLayout


class InboxFragment : AppCompatActivity() {
    // TODO: Rename and change types of parameters
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // supportActionBar!!.hide()
        setContentView(R.layout.fragment_inbox)
        tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        viewPager = findViewById<ViewPager>(R.id.viewPager)
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Requests"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("History"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        val context=this
        val adapter = InboxTabAdapter(context,this.supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }


}