package com.aitd.module_chat.ui

import android.content.Intent
import android.os.Bundle
import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_chat.lib.QXIMManager
import com.aitd.module_chat.R
import com.aitd.module_chat.pojo.AccountConfig
import com.aitd.module_chat.ui.emotion.DBHelper
import com.aitd.module_chat.utils.file.FileUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.alibaba.android.arouter.facade.annotation.Route


//module 为true  走这里
@Route(path = ARouterUrl.Chat.ROUTE_CHAT_ACTIVITY)
class ChatHomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        //todo 本来应该放在baseapplication执行的
        QXIMManager.instance.init(this)
        var list = AccountConfig.getAccounts()
        QLog.e("MyApplication", "size=" + list.size)
        DBHelper.initDB(this.application)
        FileUtil.getAudioDir(this)
    }

    override fun init(saveInstanceState: Bundle?) {
        if (QXIMManager.instance.isCacheUser()) {
            go2Main()
        } else {
            go2Login()
        }
    }

    private fun go2Main() {
        val intent = Intent(this, MainActivity2::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun go2Login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun getLayoutId(): Int = R.layout.activity_chat_home
}