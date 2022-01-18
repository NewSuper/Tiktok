package com.aitd.module_chat.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.*
import com.aitd.module_chat.lib.*
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.pojo.Member
import com.aitd.module_chat.pojo.QXGroupInfo
import com.aitd.module_chat.pojo.QXGroupNotice
import com.aitd.module_chat.ui.BaseChatActivity
import com.aitd.module_chat.utils.SharePreferencesUtil
import com.aitd.module_chat.utils.qlog.QLog
import kotlinx.android.synthetic.main.imui_activity_chat.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*
import kotlinx.android.synthetic.main.imui_layout_chat_bottom.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*

open class GroupChatActivity : BaseChatActivity() {

    override fun getLayoutId(): Int = R.layout.imui_activity_chat

    open class UnReadMessage
    class UnReadCountMessage(var count: Int = 0) : UnReadMessage()

    class UnReadAtMessage(var message: Message?) : UnReadMessage()

    private val TAG = "GroupChatActivity"

    private var lastVisiblePosition: Int = -1
    private var mFirstUnReadMessage: Message? = null

    //消息显示列表，用于记录UI回调比获取未读消息数据还要快的消息，当获取完未读消息之后，再去掉这些消息
    private val messageDisplayList = arrayListOf<Message>()
    private var mNotice: QXGroupNotice? = null
    private var mChatNoticeReceivedListener: QXIMClient.OnChatNoticeReceivedListener? = null

    //@人 返回code
    private val REQUEST_REFER_TO = 66

    private var mUnReadMessageStack = Stack<UnReadMessage>()
    override fun loadData() {
        QXUserInfoManager.getInstance().refreshGroupInfo(targetId)
        super.loadData()
    }

    override fun init(saveInstanceState: Bundle?) {
        initNotice()
        checkGroupMute()
        initEdtText()
        QXIMKit.setQXAtGroupMemberCallBack {
            handleAtTo(it)
        }

        initUnReadClickListener()
        initRecyclerViewScrollListener()
        isSmoothScrollToPosition = false
    }

    private fun initRecyclerViewScrollListener() {
        recycler_view_message.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    var linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                    var firstPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    var lastPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                    if (lastVisiblePosition == -1) {
                        //第一次加载
                        var unLoadList = mMessageList.subList(firstPosition, lastPosition + 1)
                        for (message in unLoadList) {
                            messageDisplay(message)
                        }
                    } else {
                        if (lastVisiblePosition != firstPosition) {
                            messageDisplay(mMessageList[firstPosition])
                        }
                    }
                    lastVisiblePosition = firstPosition

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun initUnReadClickListener() {
        ll_unread.setOnClickListener {
            try {

                var unReadMessage = mUnReadMessageStack.peek()
                if (unReadMessage != null) {
                    //如果是未读数消息
                    if (unReadMessage is UnReadCountMessage) {
                        if (!mUnReadMessageStack.isEmpty()) {
                            var unReadCountMessage = mUnReadMessageStack.pop() as UnReadCountMessage
                            updateUnReadUI()
                            var position = mMessageList.size - unReadCountMessage.count
                            recycler_view_message.smoothScrollToPosition(position)
                        }
                    } else {
                        //否则为@未读消息
                        var atMessage = unReadMessage as UnReadAtMessage
                        if (atMessage != null) {
                            getMessageByTimestamp(atMessage.message!!)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getFirstUnReadMessage() {
        //获取第一条未读消息
        QXIMKit.getInstance()
            .getFirstUnReadMessage(conversationId, object : QXIMClient.ResultCallback<Message>() {
                override fun onSuccess(data: Message) {
                    mFirstUnReadMessage = data
                    getAllUnReadMessageCount()
                }

                override fun onFailed(error: QXError) {
                    getAllUnReadMessageCount()
                }

            })
    }

    override fun loadUnReadMessage() {
        getFirstUnReadMessage()
    }

    private fun getAllUnReadMessageCount() {
        //获取会话未读消息数量
        QXIMKit.getInstance().getConversationUnReadCount(
            conversationId,
            true,
            object : QXIMClient.ResultCallback<Int>() {
                override fun onSuccess(data: Int) {
                    QLog.d(TAG, "获取会话未读消息数：" + data)
                    if (data > 0) {
                        var layoutManager =
                            recycler_view_message.layoutManager as LinearLayoutManager
                        var visibleCount =
                            layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstCompletelyVisibleItemPosition()
                        //如果未读消息的数量比当前屏幕一页的item数量还要多，则显示未读消息浮窗，否则不显示浮窗
                        if (visibleCount > 0 && visibleCount < data) {
                            //生产：压入栈中
                            mUnReadMessageStack.push(UnReadCountMessage(data))
                            updateUnReadUI()
                            QLog.d(TAG, "压入未读消息数量：${data}，未读消息栈剩余：${mUnReadMessageStack.size}")
                        }
                    }
                    loadUnReadAtToMessage()
                }

                override fun onFailed(error: QXError) {
                    QLog.d(
                        TAG,
                        "loadUnReadMessage failed > name:" + error.name + " msg:" + error.msg + " code:" + error.code
                    )
                }

            })
    }

    override fun loadUnReadAtToMessage() {
        QXIMKit.getInstance().getUnReadAtToMessage(
            conversationId,
            object : QXIMClient.ResultCallback<MutableList<Message>>() {
                override fun onSuccess(data: MutableList<Message>) {
                    QLog.d(TAG, "获取未读@消息" + data.size)
                    //生产：压入未读栈
                    for (message in data) {
                        mUnReadMessageStack.push(UnReadAtMessage(message))
                        QLog.d(
                            TAG,
                            "压入@消息：${message?.messageId}，未读消息栈剩余：${mUnReadMessageStack.size}"
                        )
                    }
                    //检查消息显示列表中是否存在消息
                    for (message in messageDisplayList) {
                        if (!mUnReadMessageStack.isEmpty()) {
                            mUnReadMessageStack.pop()
                            updateAtMessageReadState(message)
                        }
                    }
                    updateUnReadUI()
                    //发送消息回执
                    sendReadReceipt()
                    //清空该会话数据库中的@消息为已读
                    clearAtMessage()
                }

                override fun onFailed(error: QXError) {
                    QLog.d(
                        TAG,
                        "loadUnReadAtToMessages failed > name:" + error.name + " msg:" + error.msg + " code:" + error.code
                    )
                    //发送消息回执
                    sendReadReceipt()
                    //清空该会话数据库中的@消息为已读
                    clearAtMessage()
                }

            })
    }

    private fun clearAtMessage() {
        QXIMKit.getInstance()
            .clearAtMessage(conversationId, object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                }

                override fun onFailed(error: QXError) {
                }

            })
    }

    override fun messageDisplay(message: Message) {

        if (message?.messageContent == null) {
            return
        }
        //处理文本@消息
        handleAtMessage(message)
        //处理第一条未读消息
        handleFirstUnMessage()
    }

    private fun handleFirstUnMessage() {
        if (isFirstUnReadMessageCompletelyVisible()) {
            ll_unread.visibility = View.GONE
        }
    }

    private fun handleAtMessage(message: Message) {
        //如果是文本消息，处理@未读消息的消费业务逻辑
        if (message.messageContent is TextMessage) {
            //如果是收到的消息
            if (message.direction == Message.Direction.DIRECTION_RECEIVED) {
                var textMessage = message.messageContent as TextMessage
                if (textMessage.atToMessageList.size > 0) {
                    var atMe = AtToMessage(
                        QXIMKit.getInstance().curUserId,
                        AtToMessage.ReadState.STATE_UN_READ
                    )
                    var atAll = AtToMessage("-1", AtToMessage.ReadState.STATE_UN_READ)
                    if (textMessage.atToMessageList.contains(atMe) || textMessage.atToMessageList.contains(
                            atAll
                        )
                    ) {
                        //如果该@消息包含自己且未读
                        messageDisplayList.add(message)
                        if (mUnReadMessageStack.isEmpty()) {
                            return
                        }
                        if (message.messageContent is TextMessage) {
                            if (mUnReadMessageStack.peek() is UnReadAtMessage) {
                                var unReadAtMessage = mUnReadMessageStack.peek() as UnReadAtMessage

                                //如果当前消息数量小于一屏消息数量，则执行下面判断逻辑
                                if (unReadAtMessage.message?.messageId == message?.messageId) {
                                    //如果是@消息，则消费未读消息栈中的元素，并更新@消息的阅读状态为已读
                                    unReadAtMessage = mUnReadMessageStack.pop() as UnReadAtMessage
                                    updateAtMessageReadState(message)
                                    QLog.d(
                                        TAG, "消费@消息：" +
                                                "${unReadAtMessage.message?.messageId}，未读消息栈剩余：${mUnReadMessageStack.size}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isFirstUnReadMessageCompletelyVisible(): Boolean {
        if (mFirstUnReadMessage != null) {
            var layoutManager = recycler_view_message.layoutManager as LinearLayoutManager
            var position = layoutManager.findFirstCompletelyVisibleItemPosition()
            var index = mMessageList.indexOf(mFirstUnReadMessage)
            if (position <= index) {
                return true
            }
        }
        return false
    }

    private fun updateAtMessageReadState(message: Message) {
        updateUnReadUI()
    }

    private fun updateUnReadUI() {
        if (mUnReadMessageStack.size > 0) {
            //读取栈头元素
            var unReadMessage = mUnReadMessageStack.peek()
            if (unReadMessage != null) {

                //如果是未读数消息
                if (unReadMessage is UnReadCountMessage) {
                    if (!isFirstUnReadMessageCompletelyVisible()) {
                        var count = (unReadMessage as UnReadCountMessage).count
                        tv_unread.text = String.format(
                            resources.getString(R.string.qx_message_unread_msg),
                            count
                        )
                        ll_unread.visibility = View.VISIBLE
                    }
                } else {
                    //否则为@未读消息
                    tv_unread.text = resources.getString(R.string.qx_message_unread_atto)
                    ll_unread.visibility = View.VISIBLE
                }
            }
        } else {
            ll_unread.visibility = View.GONE
        }
    }

    override fun onAvatarLongClick(userInfo: QXUserInfo) {
        if (userInfo.id.isNullOrEmpty() || userInfo.displayName.isNullOrEmpty()) {
            return
        }
        mAtToList.add(Member(userInfo.id, userInfo.displayName))
        qxExtension.setInputText("@" + userInfo.displayName + " ")
    }


    /**
     * 加载群公告
     */
    private fun initNotice() {
        var provider = QXContext.getInstance().qxGetGroupNotice
        if (provider == null) {
            QLog.d(TAG, "群公告提供者未设置")
        } else {

            provider.getGroupNotice(
                targetId,
                object : QXIMKit.QXGetGroupNoticeProvider.QXGetGroupNoticeCallback {
                    override fun onSuccess(notice: QXGroupNotice) {
                        mNotice = notice
                        //如果未读，则显示群公告
                        if (!SharePreferencesUtil.getInstance(this@GroupChatActivity)
                                .isGroupNoticeRead(notice)
                        ) {
                            layout_group_notice_tips.visibility = View.VISIBLE
                            tv_group_notice.text = String.format(
                                resources.getString(R.string.qx_chat_group_notice),
                                notice.groupNotice
                            )
                        }
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        QLog.d(TAG, "获取群公告失败：code=$code msg=$msg")
                    }

                })
        }
        layout_group_notice_tips.setOnClickListener {
            if (mNotice != null) {
                QXContext.getInstance().uiEventProvider.onGroupNoticeClick(
                    this, targetId!!,
                    conversationId!!, mNotice!!.groupNotice
                )
            }
        }

        iv_group_notice.setOnClickListener {
            if (mNotice != null) {
                layout_group_notice_tips.visibility = View.GONE
                mNotice!!.isRead = true
                SharePreferencesUtil.getInstance(this).addGroupNoticeReadCache(mNotice!!)
            }
        }
    }

    override fun setTitleBarName() {
        if (targetId.isNullOrEmpty()) {
            return
        }
        var groupInfo: QXGroupInfo? = QXUserInfoManager.getInstance().getGroup(targetId!!)
            ?: return
        setTitle(groupInfo?.name!!, groupInfo?.memberCount)
    }

    private fun setTitle(groupName: String, memberCount: Int) {
        var title =
            String.format(resources.getString(R.string.qx_chat_group_title), groupName, memberCount)
        tv_title_bar_name.text = title
    }

    private fun initEdtText() {
        var beforeChanged = ""
        edt_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeChanged = s.toString()

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    var afterChanged = s.toString()

                    if (!beforeChanged.contains(afterChanged)) {
                        if (afterChanged.last().toString() == "@") {
                            QXContext.getInstance().selectGroupMemberProvider?.selectMember(
                                this@GroupChatActivity,
                                targetId
                            )
                        }
                    }
                }
            }
        })
    }

    override fun initIMListener() {
        super.initIMListener()

        mChatNoticeReceivedListener = object : QXIMClient.OnChatNoticeReceivedListener {

            override fun onGroupGlobalMute(isEnabled: Boolean) {
                QLog.i(TAG, "全局群组禁言 isEnabled=$isEnabled")
                checkGroupMute()
            }

            override fun onGroupAllMute(groupId: String, isEnabled: Boolean) {
                //此处要判断禁言的群跟当前聊天的群是否一致
                if (groupId == targetId) {
                    checkGroupMute()
                }
            }

            override fun onGroupMute(groupId: String, isEnabled: Boolean) {
                QLog.i(TAG, "群组成员禁言，groupId=$groupId+ isEnabled=$isEnabled")
                //此处要判断禁言的群跟当前聊天的群是否一致
                if (groupId == targetId) {
                    checkGroupMute()
                }
            }

            override fun onChatRoomGlobalMute(isEnabled: Boolean) {
            }

            override fun onChatRoomBan(chatRoomId: String, isEnabled: Boolean) {
            }

            override fun onChatRoomMute(chatRoomId: String, isEnabled: Boolean) {
            }

            override fun onChatRoomDestroy() {
            }

        }
        QXIMClient.instance!!.addOnChatNoticeReceivedListener(mChatNoticeReceivedListener!!)

    }

    private fun handleAtTo(mutableList: MutableList<Member>) {
        //清掉前面一个@
        var content = edt_content.text.toString()

        edt_content.setText(
            content.substring(0, content.length - 1)
        )
        //2.生成[@昵称 ]格式压入到输入框中
        for (at in mutableList) {
            mAtToList.add(at)
            var name = "@" + at.name + " "//空格一定要加
            edt_content.setText(
                edt_content.text.toString() + name
            )
        }

        edt_content.setSelection(edt_content.text.length)
    }


    /**
     * 检测禁言缓存
     */
    private fun checkGroupMute() {
        QLog.d(TAG, "checkGroupMute")
        when {
            MuteCache.isGroupGlobalMute() -> {
                runOnUiThread {
                    updateInputViewByMute(getString(R.string.qx_group_forbid_all_joined), true)
                }

            }
            MuteCache.isGroupAllMute(targetId!!) -> {
                runOnUiThread {
                    updateInputViewByMute(getString(R.string.qx_chat_bottom_bar_mute), true)
                }

            }
            else -> {
                runOnUiThread {
                    updateInputViewByMute(
                        getString(R.string.qx_group_forbid),
                        MuteCache.isGroupMute(targetId!!)
                    )
                }

            }
        }
    }

    override fun onAdvancedBack() {
        QXUserInfoManager.getInstance().refreshGroupInfo(targetId)
    }

    override fun onClearMessage() {
        //清空本地聊天记录成功
        mMessageList.clear()
        mMessageAdapter.notifyDataSetChanged()
    }

    override fun onExitGroup() {
        finish()
    }

    override fun setConversationType(): String {
        return ConversationType.TYPE_GROUP
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onGroupInfoUpdate(groupInfo: QXGroupInfo?) {
        QLog.d("groupchat", "targetId:${targetId},groupInfo id: ${groupInfo?.id}")
        groupInfo?.let {
            if (it.id == targetId) {
                setTitle(it.name!!, it.memberCount)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        QXIMClient.instance.removeOnChatNoticeReceivedListener(mChatNoticeReceivedListener!!)
    }
}