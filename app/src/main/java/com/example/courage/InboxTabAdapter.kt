package com.example.eleven

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.courage.HistoryFragment
import com.example.courage.MenuFragment
import com.example.courage.RequestFragment

class InboxTabAdapter(private val myContext: Context?, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return RequestFragment()
            }
            1 -> {
                return HistoryFragment()
            }

            else -> return RequestFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}