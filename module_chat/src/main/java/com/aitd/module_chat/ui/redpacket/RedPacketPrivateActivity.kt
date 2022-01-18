package com.aitd.module_chat.ui.redpacket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.module_chat.R
import com.aitd.module_chat.api.ChatViewModel
import com.aitd.module_chat.databinding.ActivityPrivateRedpacketBinding

class RedPacketPrivateActivity:BaseMvvmActivity<ChatViewModel,ActivityPrivateRedpacketBinding>() {
    override fun init(saveInstanceState: Bundle?) {

        initData()

    }
    private fun initData(){
        mViewModel.getCoinLimit()
        mViewModel.getHangQing()

    }

    override fun getLayoutId(): Int = R.layout.activity_private_redpacket

    companion object{
        private const val TARGET_ID: String    = "targetId"
        @JvmStatic
        fun goToActivity(activity: Activity, targetId: String){
            activity.startActivity(Intent(activity, RedPacketPrivateActivity::class.java).putExtra(TARGET_ID, targetId))
        }
    }
}