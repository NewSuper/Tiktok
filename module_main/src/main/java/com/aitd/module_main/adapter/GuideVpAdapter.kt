package com.aitd.module_main.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class GuideVpAdapter(val views:List<View>):PagerAdapter() {
    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
       return view == `object` as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(views[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        (container as  ViewPager).addView(views[position],0)
        return views[position]
    }
}