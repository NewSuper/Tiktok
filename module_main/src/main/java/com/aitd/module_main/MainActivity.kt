package com.aitd.module_main

import android.os.Bundle
import android.text.BoringLayout
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.extend.getCompatColor
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_main.widget.bottombar.BottomBarItem
import com.aitd.module_main.widget.bottombar.BottomBarLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import me.jessyan.autosize.internal.CancelAdapt

@Route(path = ARouterUrl.Main.ROUTE_MAIN_ACTIVITY)
class MainActivity : BaseActivity(), BottomBarLayout.OnTabCheckListener, CancelAdapt {
    private val mBottomBar by lazy {
        findViewById<BottomBarLayout>(R.id.bbl_mian)
    }
    private val mFragment = HashMap<Int, Fragment>()
    private val mFragmentRouter = arrayOf(
        ARouterUrl.Chat.ROUTE_CHAT_FRAGMENT,
        ARouterUrl.Wealth.ROUTE_WEALTH_FRAGMENT,
        ARouterUrl.Discover.ROUTE_DISCOVER_FRAGMENT,
        ARouterUrl.Mine.ROUTE_MINE_FRAGMENT
    )
    var bottomItems = arrayOf(
        intArrayOf(
            R.string.main_bottombar_chat,
            R.mipmap.mian_bottom_chat_normal,
            R.drawable.main_anim_chat
        ),
        intArrayOf(
            R.string.main_bottombar_wealth,
            R.mipmap.main_bottom_wealth_normal,
            R.drawable.main_anim_wealth
        ),
        intArrayOf(
            R.string.main_bottombar_discover,
            R.mipmap.main_bottom_discover_normal,
            R.drawable.main_anim_find
        ),
        intArrayOf(
            R.string.main_bottombar_mine,
            R.mipmap.mian_bottom_mine_normal,
            R.drawable.main_anim_me
        )
    )
    private var temptime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - temptime > 2000) {
                ToastUtils.showShort(R.string.exit_press_again)
                temptime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun init(savedInstanceState: Bundle?) {
        initBottomBar()
    }

    /*
    * 初始化底部导航栏
    * */
    private fun initBottomBar() {
        bottomItems.forEach {
            mBottomBar.addTab(
                BottomBarLayout.Tab().setText(getString(it[0]))
                    .setColor(
                        getCompatColor(
                            R.color.main_bootombar_txt_nomal
                        )
                    )
                    .setCheckedColor(
                        getCompatColor(
                            R.color.main_bootombar_txt_selected
                        )
                    )
                    .setNormalIcon(it[1])
                    .setPressedIcon(it[2])
            )
        }
        mBottomBar.setOnTabCheckListener(this)
        mBottomBar.currentItem = 0
    }

    override fun getLayoutId(): Int = R.layout.main_activity

    override fun onTabSelected(bottomBarItem: BottomBarItem?, position: Int) {
        val mfragmentTransaction = supportFragmentManager.beginTransaction()
        hideAllFragments(mfragmentTransaction)
        if (mFragment[position] == null) {
            val fragment = ARouter.getInstance().build(mFragmentRouter[position])
                .navigation() as BaseFragment
            mFragment[position] = fragment
            mfragmentTransaction.add(R.id.fl_content, fragment)
        } else {
            mfragmentTransaction.show(mFragment[position]!!)
        }
        mfragmentTransaction.commit()
    }

    private fun hideAllFragments(ft: FragmentTransaction) {
        mFragment.forEach {
            ft.hide(it.value)
        }
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
         val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
         mFragment.forEach { transaction.remove(it.value) }
         transaction.commitAllowingStateLoss()
         super.onSaveInstanceState(outState)
     }*/
}