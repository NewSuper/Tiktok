package com.aitd.module_mine

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_mine.lottie.*
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.mine_activity_home.*

@Route(path = ARouterUrl.Mine.ROUTE_MINE_ACTIVITY)
class MineHomeActivity : BaseActivity() {
    private var homeFragment: BaseFragment? = null
    private var msgFragment: BaseFragment? = null
    private var callFragment: BaseFragment? = null
    private var discoverFragment: BaseFragment? = null
    private var personFragment: BaseFragment? = null
    private var currendIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectChanged(0)
    }

    override fun init(savedInstanceState: Bundle?) {

        ll_first.setOnClickListener {
            selectChanged(0)
        }
        ll_msg.setOnClickListener {
            selectChanged(1)
        }
        ll_call.setOnClickListener {
            selectChanged(2)
        }
        ll_discovery.setOnClickListener {
            selectChanged(3)
        }
        ll_mine.setOnClickListener {
            selectChanged(4)
        }
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment!!)
        }
        if (msgFragment != null) {
            transaction.hide(msgFragment!!)
        }
        if (callFragment != null) {
            transaction.hide(callFragment!!)
        }
        if (discoverFragment != null) {
            transaction.hide(discoverFragment!!)
        }
        if (personFragment != null) {
            transaction.hide(personFragment!!)
        }
    }

    private fun showHomeFragment(transaction: FragmentTransaction) {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
            transaction.add(R.id.fl_content, homeFragment!!)
        } else {
            transaction.show(homeFragment!!)
        }
    }

    private fun showMsgFragment(transaction: FragmentTransaction) {
        if (msgFragment == null) {
            msgFragment = MsgFragment()
            transaction.add(R.id.fl_content, msgFragment!!)
        } else {
            transaction.show(msgFragment!!)
        }
    }

    private fun showCallFragment(transaction: FragmentTransaction) {
        if (callFragment == null) {
            callFragment = CallFragment()
            transaction.add(R.id.fl_content, callFragment!!)
        } else {
            transaction.show(callFragment!!)
        }
    }

    private fun showDiscoverFragment(transaction: FragmentTransaction) {
        if (discoverFragment == null) {
            discoverFragment = DisFragment()
            transaction.add(R.id.fl_content, discoverFragment!!)
        } else {
            transaction.show(discoverFragment!!)
        }
    }

    private fun showPersonFragment(transaction: FragmentTransaction) {
        if (personFragment == null) {
            personFragment = MineFragment()
            transaction.add(R.id.fl_content, personFragment!!)
        } else {
            transaction.show(personFragment!!)
        }
    }

    private fun selectChanged(i: Int) {
        currendIndex = i
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)
        when (i) {
            0 -> {
                showHomeFragment(transaction)
            }
            1 -> {
                showMsgFragment(transaction)
            }
            2 -> {
                showCallFragment(transaction)
            }
            3 -> {
                showDiscoverFragment(transaction)
            }
            4 -> {
                showPersonFragment(transaction)
            }
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currendIndex != 0){
            notifyAdapterData(0)
        }else{
//            if (System.currentTimeMillis() - firstPressedTime > 2000) {
//                ToastUtil.show(R.string.exit_app)
//                firstPressedTime = System.currentTimeMillis()
//            } else {
//                exitApp()
//            }
        }
    }

    private fun notifyAdapterData(index: Int) {
        selectChanged(index)
    }
    override fun getLayoutId(): Int = R.layout.mine_activity_home
}