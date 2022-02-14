package com.tiktok

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tiktok.base.BaseActivity
import com.tiktok.base.CommPagerAdapter
import com.tiktok.fragment.MainFragment
import com.tiktok.fragment.PersonalFragment
import com.tiktok.pojo.MainPageChangeEvent
import com.tiktok.pojo.PauseVideoEvent
import com.tiktok.utils.RxBus
import kotlinx.android.synthetic.main.activity_main.*
import rx.functions.Action1

class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main

    private var pagerAdapter: CommPagerAdapter? = null
    private val fragments = ArrayList<Fragment>()
    private val mainFragment = MainFragment()
    private val personalHomeFragment = PersonalFragment()

    /** 上次点击返回键时间  */
    private var lastTime: Long = 0

    /** 连续按返回键退出时间  */
    private val EXIT_TIME = 2000

    override fun init(savedInstanceState: Bundle?) {

        try {
            Thread.sleep(2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fragments.add(mainFragment)
        fragments.add(personalHomeFragment)
        pagerAdapter = CommPagerAdapter(supportFragmentManager, fragments, arrayOf("", ""))
        viewPager!!.adapter = pagerAdapter

        //点击头像切换页面
        RxBus.getInstance().toObservable(MainPageChangeEvent::class.java)
            .subscribe(Action1 { event: MainPageChangeEvent ->
                if (viewPager != null) {
                    viewPager!!.currentItem = event.page
                }
            } as Action1<MainPageChangeEvent>)
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                curMainPage = position
                if (position == 0) {
                    RxBus.getInstance().post(PauseVideoEvent(true))
                } else if (position == 1) {
                    RxBus.getInstance().post(PauseVideoEvent(false))
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onBackPressed() {
        //双击返回退出App
        if (System.currentTimeMillis() - lastTime > EXIT_TIME) {
            if (viewPager!!.currentItem == 1) {
                viewPager!!.currentItem = 0
            } else {
                Toast.makeText(applicationContext, "再按一次退出", Toast.LENGTH_SHORT).show()
                lastTime = System.currentTimeMillis()
            }
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        var curMainPage = 0
    }
}