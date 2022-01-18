package com.aitd.module_chat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.library_common.utils.network.NetStateUtils
import com.aitd.library_common.utils.network.NetworkMonitor
import com.aitd.module_chat.Conversation
import com.aitd.module_chat.QXError
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.ConversationAdapter
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.lib.QXIMManager
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.pojo.UserCache
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_im.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus

class IMFragment : Fragment(), QXIMClient.ConversationListener, CoroutineScope by MainScope() {
    private lateinit var mAdapter: ConversationAdapter
    private lateinit var viewManager: LinearLayoutManager
    private var netStatus = 0;//网络状态：0.网络连接   1.没有网络
    private var isConnected = true//当前是否连接成功
    private var connectionStatus: Int = QXIMClient.ConnectionStatusListener.STATUS_UNKNOWN;   //连接状态

    @Volatile
    private var mDataSet: ArrayList<Conversation> = ArrayList()

    // private var homePop: HomePop? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(activity)
        return inflater.inflate(R.layout.fragment_im, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        QXIMManager.instance.addConnectionListener(object : QXIMManager.ConnectionListener {
            override fun onChanged(staus: Int) {
                if (staus == QXIMClient.ConnectionStatusListener.STATUS_REMOTE_SERVICE_CONNECTED) {
                    rcv_conversation_list.post { loadData() }
                }
                connectionStatus = staus;
                updateConnStatus()
            }
        })

        NetworkMonitor.getInstance()
            .setNetworkMonitorListener(object : NetworkMonitor.NetworkMonitorListener {
                override fun onAvailable() {
                    netStatus = 0;
                    updateNetworkTips()
                }

                override fun onLost() {
                    netStatus = 1;
                    updateNetworkTips()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        checkNetConnectState()
        loadData()
    }


    private fun checkNetConnectState() {
        checkNetwork()
        //主动获取状态，二级页面返回需要主动获取连接状态
        if (connectionStatus != QXIMClient.ConnectionStatusListener.STATUS_UNKNOWN) {
            connectionStatus = QXIMClient.instance.mConnectStatus!!.value
        }
        updateConnStatus()
    }

    private fun loadData() {
        QXIMKit.getInstance().getAllConversation(
            object : QXIMClient.ResultCallback<List<Conversation>>() {
                override fun onSuccess(data: List<Conversation>) {

                    mDataSet.clear()
                    mDataSet.addAll(data)
                    mDataSet.sort()
                    mAdapter.notifyDataSetChanged()
                }


                override fun onFailed(error: QXError) {
                    if (error == QXError.CONNECTION_NOT_READY) {
                        rcv_conversation_list.postDelayed({ loadData() }, 2000)
                    }
                }
            })
        QXIMKit.getInstance().getAllMessageUnReadCount(listOf(
            Conversation.Type.TYPE_GROUP,
            Conversation.Type.TYPE_PRIVATE, Conversation.Type.TYPE_SYSTEM
        ),
            object : QXIMClient.ResultCallback<Int>() {
                override fun onSuccess(data: Int) {
                }

                override fun onFailed(error: QXError) {
                }
            })
    }

    fun initView() {
        QXIMClient.instance.setOnConversationListener(this)
        mAdapter = ConversationAdapter(requireActivity().baseContext, mDataSet)
        rcv_conversation_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener(object : ConversationAdapter.OnItemClickListener {
            override fun onClick(position: Int) {

                var data = mDataSet[position]
                var targetName = data.targetName
                when (data.conversationType) {
                    ConversationType.TYPE_GROUP -> {
                        targetName = "群聊[" + data.targetId + "]"
                    }
                    ConversationType.TYPE_SYSTEM -> {
                        targetName = "系统消息"
                    }
                }

                QXIMKit.getInstance()
                    .goToChatUI(context, data.conversationType, targetName, data.targetId, null)
            }

            override fun onLongClick(position: Int, v: View): Boolean {
                var flag = mDataSet[position].noDisturbing == 0
                setNoDisturbing(mDataSet[position], flag)
                setTop(mDataSet[position].conversationId, flag)
                return true
            }
        })

        var userInfo = UserCache(requireContext()).getUserCache()
        Glide.with(this).load(userInfo?.userAvatar)
            .apply(RequestOptions.bitmapTransform(CircleCrop())).into(iv_avatar)

        iv_addgroup.setOnClickListener {
//            homePop = HomePop(requireContext())
//            homePop?.showAsDropDown(iv_addgroup)
//            homePop?.createGroupListener = {
//                SelectFriendActivity.startActivity(requireContext(), SelectFriendActivity.GROUP_CREATE)
//                homePop?.dismiss()
//            }
//            homePop?.addFriendListener = {
//                RecentConversationListActivity.startActivity(requireContext())
//                homePop?.dismiss()
//            }
        }
        tv_exit.text = userInfo?.userName +">>>退出"
        tv_exit.setOnClickListener {
            QXIMManager.instance.clearUserCache()   //退出
            QXIMKit.getInstance().logout()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    //通过id查询会话item的位置
    private fun checkItemId(cId: String): Int {
        for (i in 0 until mDataSet!!.size) {
            val data = mDataSet[i]
            if (null != data) {
                if (cId == data.conversationId) {
                    return i
                }
            }
        }
        return -1
    }

    private fun testDeleteRemoteMessageByTimestamp(Conversation: Conversation) {
        QXIMClient.instance.deleteRemoteMessageByTimestamp(Conversation.conversationType,
            Conversation.targetId,
            System.currentTimeMillis(),
            object : QXIMClient.OperationCallback() {

                override fun onSuccess() {
                    Toast.makeText(activity, "删除远程消息成功", Toast.LENGTH_SHORT).show()
                }

                override fun onFailed(error: QXError) {
                    Toast.makeText(activity, "删除远程消息失败", Toast.LENGTH_SHORT).show()
                }

            })
    }

    //置顶
    private fun setTop(conversationId: String, isTop: Boolean) {
        launch(Dispatchers.Main) {
            val deferred = async(Dispatchers.IO) {
                QXIMClient.instance.setConversationTop(conversationId,
                    isTop,
                    object : QXIMClient.OperationCallback() {

                        override fun onSuccess() {
                            loadData()
                        }

                        override fun onFailed(error: QXError) {

                        }
                    })
            }
        }
    }

    //免打扰
    private fun setNoDisturbing(conversation: Conversation, isMute: Boolean) {
        launch(Dispatchers.Main) {
            val deferred = async(Dispatchers.IO) {
                QXIMClient.instance.setConversationNoDisturbing(conversation.conversationId,
                    isMute,
                    object : QXIMClient.OperationCallback() {

                        override fun onSuccess() {
                            loadData()
                        }

                        override fun onFailed(error: QXError) {
                            Toast.makeText(
                                activity,
                                "设置免打扰失败，错误码：${error.code} 错误信息：${error.msg}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            }
        }
    }

    //测试删除指定会话
    fun testDeleteConversation(entity: Conversation) {
        QXIMClient.instance.deleteConversation(
            entity.conversationId,
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                    loadData()
                }

                override fun onFailed(error: QXError) {
                    Toast.makeText(
                        activity,
                        "设置删除失败，错误码：${error.code} 错误信息：${error.msg}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    //测试删除所有会话
    fun testDeleteAllConversation() {
        QXIMClient.instance.deleteAllConversation(object : QXIMClient.OperationCallback() {
            override fun onSuccess() {
                loadData()
            }

            override fun onFailed(error: QXError) {
                Toast.makeText(
                    activity,
                    "设置所有会话失败，错误码：${error.code} 错误信息：${error.msg}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        checkNetwork()
        if (!hidden) {
            loadData()
            checkNetConnectState()
        }
    }

    private fun checkNetwork() {
        //获取网络状态
        netStatus = NetStateUtils.getAPNType(context)
        netStatus = if (!NetStateUtils.isNetworkAvailable()) {
            1
        } else {
            0
        }
        updateNetworkTips()
    }

    override fun onChanged(list: List<Conversation>) {
        var temp = arrayListOf<Conversation>()
        for (remote in list) {
            if (mDataSet.contains(remote)) {
                var index = mDataSet.indexOf(remote)
                mDataSet[index] = remote//如果存在，则更新数据
            } else {
                temp.add(remote)//如果不存在，则插入到adapter里面
            }
        }
        if (temp.size > 0) {
            mDataSet.addAll(temp)
        }
        mDataSet.sort()//排序
        mAdapter.notifyDataSetChanged()//刷新
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    /**
     * 更改网络提示状态
     */
    private fun updateNetworkTips() {
        activity?.runOnUiThread {
            if (netStatus == 1) {
                //没有网络
                Log.i("ConversationFragment", "网络已关闭！")
                isConnected = false
                layout_no_network_tips.visibility = View.VISIBLE
                tv_group_notice.setText("当前网络不可用，请检查网络设置!")
            } else {
                Log.i("ConversationFragment", "网络已打开！")
                if (!isConnected) {
                    tv_group_notice.setText("网络已恢复正常,请稍候...")
                } else {
                    updateConnStatus()
                }
            }
        }
    }

    /**
     * 更新登录提示状态
     */
    private fun updateConnStatus() {
        activity?.runOnUiThread {
            if (netStatus == 0) {
                when (connectionStatus) {
                    QXIMClient.ConnectionStatusListener.STATUS_CONNECTED, QXIMClient.ConnectionStatusListener.STATUS_REMOTE_SERVICE_CONNECTED -> {
                        isConnected = true  //已连接
                        layout_no_network_tips.visibility = View.GONE
                    }
                    QXIMClient.ConnectionStatusListener.STATUS_DISCONNECTED, QXIMClient.ConnectionStatusListener.STATUS_UNCONNECTED -> {
                        isConnected = false   //断开连接
                        layout_no_network_tips.visibility = View.VISIBLE
                        tv_group_notice.setText("服务器已断开连接!")
                    }
                    QXIMClient.ConnectionStatusListener.STATUS_CONNECTING -> {
                        if (!isConnected) {
                            layout_no_network_tips.visibility = View.VISIBLE //连接中
                            tv_group_notice.setText("服务器建立连接中...")
                        }
                    }
                    QXIMClient.ConnectionStatusListener.STATUS_TIME_OUT -> {
                        isConnected = false
                        layout_no_network_tips.visibility = View.VISIBLE
                        tv_group_notice.setText("服务器连接超时!")
                    }
                    QXIMClient.ConnectionStatusListener.STATUS_SERVER_INVALID -> {
                        isConnected = false
                        layout_no_network_tips.visibility = View.VISIBLE
                        tv_group_notice.setText("连接失败,服务器错误!")
                    }
                }
            }
        }
    }
}