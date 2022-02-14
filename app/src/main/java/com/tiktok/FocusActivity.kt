package com.tiktok

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tiktok.base.BaseActivity
import com.tiktok.base.CommPagerAdapter
import com.tiktok.fragment.FansFragment
import kotlinx.android.synthetic.main.activity_focus.*

class FocusActivity : BaseActivity() {
    private val fragments = ArrayList<Fragment>()
    private var pagerAdapter: CommPagerAdapter? = null
    private val titles = arrayOf("关注 128", "粉丝 128", "推荐关注")
    override fun init(savedInstanceState: Bundle?) {
        for (i in titles.indices) {
            fragments.add(FansFragment())
            tablayout!!.addTab(tablayout!!.newTab().setText(titles[i]))
        }
        pagerAdapter = CommPagerAdapter(supportFragmentManager, fragments, titles)
        viewpager!!.adapter = pagerAdapter
        tablayout!!.setupWithViewPager(viewpager)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_focus
    }
}