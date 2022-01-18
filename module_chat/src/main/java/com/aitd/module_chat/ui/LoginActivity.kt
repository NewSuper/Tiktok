package com.aitd.module_chat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.utils.OkhttpUtils
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.AccountAdapter
import com.aitd.module_chat.http.HttpCallback
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMClient.Companion.connect
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.pojo.AccountConfig
import com.aitd.module_chat.pojo.UIUserInfo
import com.aitd.module_chat.pojo.UserCache
import com.aitd.module_chat.pojo.UserInfo
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var loginName: String
    private lateinit var userCache: UserCache

    override fun init(saveInstanceState: Bundle?) {
        userCache = UserCache(this)
        viewManager = LinearLayoutManager(this)
        accountAdapter = AccountAdapter(AccountConfig.getAccounts())
        ryUserList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = accountAdapter
        }

        OkhttpUtils.getUsers(object : HttpCallback<String> {
            override fun onSuccess(t: String) {
                runOnUiThread {
                    accountAdapter.notifyDataSetChanged()
                }
            }

            override fun onError(errorCode: Int, message: String) {

            }
        })
        accountAdapter.setOnItemClickListener(object : AccountAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                loginName = AccountConfig.getAccounts()[position].name
                val account = AccountConfig.getAccounts()[position]
                QXIMKit.connect(account.token, object : QXIMClient.ConnectCallBack() {
                    override fun onSuccess(result: String?) {
                        Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        UIUserInfo.userId = AccountConfig.getAccounts()[position].userId
                        val cacheUserInfo =
                            UserInfo(account.token, account.userId, account.name, account.icon)
                        userCache.saveUserCache(cacheUserInfo)
                        val intent = Intent(this@LoginActivity, MainActivity2::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onError(errorCode: String?) {
                        Toast.makeText(this@LoginActivity, "登录失败$errorCode", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onDatabaseOpened(code: Int) {
                    }
                })
            }

        })
    }

    override fun getLayoutId(): Int = R.layout.activity_login
}