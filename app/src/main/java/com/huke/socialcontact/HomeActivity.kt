package com.huke.socialcontact

import android.os.Bundle
import android.view.View
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.launcher.ARouter


class HomeActivity: BaseActivity() {
    var mImFragment: BaseFragment? = null
    var mWealthFragment: BaseFragment? = null
    var mDiscoveryFragment: BaseFragment? = null
    var mMineFragment: BaseFragment? = null
    override fun init(saveInstanceState: Bundle?) {
        ARouter.getInstance().build(ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY).navigation();
    }

    override fun getLayoutId(): Int = R.layout.activity_home
    fun viewOnclick(view: View) {
        when (view.id) {
            R.id.button -> {
                ARouter.getInstance().build(ARouterUrl.Main.ROUTE_MAIN_ACTIVITY).navigation()
            }
            R.id.button2 -> {
                ARouter.getInstance().build(ARouterUrl.Chat.ROUTE_CHAT_ACTIVITY).navigation()
            }
            R.id.button3 -> {
                ARouter.getInstance().build(ARouterUrl.Wealth.ROUTE_WEALTH_ACTIVITY).navigation()
            }
            R.id.button4 -> {
                ARouter.getInstance().build(ARouterUrl.Discover.ROUTE_DISCOVER_ACTIVITY)
                    .navigation()
            }
            R.id.button5 -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY).navigation()
            }
            R.id.button6 -> {
                ARouter.getInstance().build(ARouterUrl.Mine.ROUTE_MINE_ACTIVITY).navigation()
            }
            R.id.btn_chat -> {
                  ARouter.getInstance().build(ARouterUrl.Chat.ROUTE_CHAT_ACTIVITY).navigation()
//                mImFragment =
//                    ARouter.getInstance().build(ARouterUrl.Chat.ROUTE_CHAT_FRAGMENT)
//                        .navigation() as BaseFragment
//                supportFragmentManager.beginTransaction().replace(R.id.fl_content, mImFragment!!)
//                    .commit()
            }
            R.id.btn_wealth -> {
                ARouter.getInstance().build(ARouterUrl.Wealth.ROUTE_WEALTH_ACTIVITY).navigation()
            }
            R.id.btn_discover -> {
                ARouter.getInstance().build(ARouterUrl.Discover.ROUTE_DISCOVER_ACTIVITY)
                    .navigation()
            }
            R.id.btn_mine -> {
                ARouter.getInstance().build(ARouterUrl.Mine.ROUTE_MINE_ACTIVITY).navigation()
            }
        }
    }
}