package com.aitd.module_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.utils.PreferenceUtils
import com.aitd.module_main.adapter.GuideVpAdapter
import com.aitd.module_main.databinding.MainActivityGuideBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
@Route(path = ARouterUrl.Main.ROUTE_GUIDE_ACTIVITY)
class GuideActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    lateinit var mBind: MainActivityGuideBinding

    private val viewIds = intArrayOf(
        R.layout.main_guide_layout_one,
        R.layout.main_guide_layout_two,
        R.layout.main_guide_layout_three,
        R.layout.main_guide_layout_four
    )
    private var views: MutableList<View> = mutableListOf()

    override fun init(savedInstanceState: Bundle?) {
        for (viewId in viewIds) {
            views.add(
                layoutInflater.inflate(viewId, null)
            )
        }
        mBind.vpGuidePager.adapter = GuideVpAdapter(views)
        mBind.vpGuidePager.addOnPageChangeListener(this)
        mBind.pivGuide.initIndicator(viewIds.size)

    }

    override fun getLayoutId(): Int = R.layout.main_activity_guide

    override fun getRealPageView(inflater: LayoutInflater): View {
        val realPageView = super.getRealPageView(inflater)
        mBind = DataBindingUtil.bind(realPageView)!!
        return realPageView
    }

    fun viewOnclick(view: View) {
        when (view.id) {
            R.id.tv_guide_login -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY).navigation()
                PreferenceUtils.setBoolean(this, "firstGuide", true)
                finish()
            }
            R.id.tv_guide_register -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_REGISTER_ACTIVITY).navigation()
                PreferenceUtils.setBoolean(this, "firstGuide", true)
            }
            R.id.tv_guide_language_choice -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTER_LANGUANG_ACTIVITY)
                    .withString("url", ARouterUrl.Main.ROUTE_GUIDE_ACTIVITY).navigation()
            }
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        mBind.pivGuide.setSelectedPage(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}