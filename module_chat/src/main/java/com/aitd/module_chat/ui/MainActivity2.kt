package com.aitd.module_chat.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.R
import com.aitd.module_chat.http.HttpCallback
import com.aitd.module_chat.utils.OkhttpUtils
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2:BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main2

    val INDEX_CONVERSATION = 0
    val INDEX_FRIEND = 1
   // val INDEX_MINE = 2
    var friendFragment = MineFragment()
    var conversationFragment = IMFragment()
   // var mineFragment = MineFragment()
    var currFragment: Fragment? =null
    var fragmentList = arrayListOf<Fragment>()

    override fun init(saveInstanceState: Bundle?) {
        initView()

        OkhttpUtils.getUsers(object : HttpCallback<String> {
            override fun onSuccess(t: String) {
                //  QLog.d("MainActivity","onCreate cacheLanguage:$cacheLanguage")
                //  QLog.d("MainActivity","getDeviceBrand : ${SystemUtil.getDeviceBrand()}" +",getSystemModel:${SystemUtil.getSystemModel()}")
            }

            override fun onError(errorCode: Int, message: String) {
            }
        })
    }

    private fun initView() {
        switchFragment(INDEX_CONVERSATION)
    }

    private fun switchFragment(index : Int) {
        when(index) {
            INDEX_CONVERSATION -> {
                btn_conversation.isSelected = true
                btn_friend.isSelected = false
               // btn_mine.isSelected = false
                hide(conversationFragment)
            }
            INDEX_FRIEND -> {
                btn_conversation.isSelected = false
                btn_friend.isSelected = true
              //  btn_mine.isSelected = false
                hide(friendFragment)
            }
//            INDEX_MINE   -> {
//                btn_conversation.isSelected = false
//                btn_friend.isSelected = false
//                btn_mine.isSelected = true
//                hide(mineFragment)
//            }
        }
    }

    private fun hide(fragment: Fragment) {
        if(fragment == currFragment) {
            return
        }
        var transaction = supportFragmentManager.beginTransaction()
        if(!fragmentList.contains(fragment)) {
            transaction.add(R.id.fly_fragment_container, fragment)
            fragmentList.add(fragment)
        } else {
            transaction.show(fragment)
        }
        if(currFragment!=null) {
            transaction.hide(currFragment!!)
        }
        currFragment = fragment
        transaction.commit()

    }

    fun onFriendClick(view: View) {
        switchFragment(INDEX_FRIEND)
    }

    fun onConversation(view: View) {
        switchFragment(INDEX_CONVERSATION)
    }

//    fun onMineClick(view: View) {
//        switchFragment(INDEX_MINE)
//    }
}