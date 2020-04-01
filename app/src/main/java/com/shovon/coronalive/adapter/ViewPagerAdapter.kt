package com.shovon.coronalive.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager){

    private val fragmentList : MutableList<Fragment> = ArrayList()
    private val titletList : MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]

    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String){
        fragmentList.add(fragment)
        titletList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titletList[position]
    }

}