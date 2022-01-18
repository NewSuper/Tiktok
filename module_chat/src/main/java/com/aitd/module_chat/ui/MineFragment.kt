package com.aitd.module_chat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.FriendAdapter
import com.aitd.module_chat.http.HttpCallback
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.pojo.Account
import com.aitd.module_chat.pojo.AccountConfig
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.OkhttpUtils
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MineFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var viewManager: LinearLayoutManager
    private var mDataSet : ArrayList<Account> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewManager = LinearLayoutManager(activity)
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }


    fun initView() {
        viewManager = LinearLayoutManager(activity)
        friendAdapter = FriendAdapter(AccountConfig.accList)
        rcv_friend_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        friendAdapter.setOnItemClickListener(object : FriendAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                if (mDataSet[position].userId == QXIMClient.instance.getCurUserId()) {
                    Toast.makeText(activity, "不能跟自己聊天！！！！", Toast.LENGTH_LONG).show()
                    return
                }
                var data = mDataSet[position]
                QXIMKit.getInstance().goToChatUI(context, ConversationType.TYPE_PRIVATE, data.name, data.userId, null)
            }
        })
    }

    fun loadData() {
        if (AccountConfig.accList.isEmpty()) {
            OkhttpUtils.getUsers(object : HttpCallback<String> {
                override fun onSuccess(t: String) {
                    mDataSet.clear()
                    mDataSet.addAll(AccountConfig.getAccounts())
                    activity?.runOnUiThread {
                        rcv_friend_list.adapter = friendAdapter
                    }
                }

                override fun onError(errorCode: Int, message: String) {
                }

            })
        } else {
            mDataSet.clear()
            mDataSet.addAll(AccountConfig.getAccounts())
            activity?.runOnUiThread {
                rcv_friend_list.adapter = friendAdapter
            }
        }

    }
    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDetach() {
        super.onDetach()
        cancel()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(hidden) {
            loadData()
        }
    }
}