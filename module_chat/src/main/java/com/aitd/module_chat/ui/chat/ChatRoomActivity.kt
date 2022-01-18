package com.aitd.module_chat.ui.chat

import android.os.Bundle
import android.widget.Toast
import com.aitd.module_chat.Message
import com.aitd.module_chat.QXError
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.MuteCache
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.ui.BaseChatActivity
import com.aitd.module_chat.utils.qlog.QLog
import kotlinx.android.synthetic.main.imui_activity_chat.*


class ChatRoomActivity : BaseChatActivity() {

    private val TAG = "ChatRoomActivity"

    private var mChatNoticeReceivedListener: QXIMClient.OnChatNoticeReceivedListener? = null
    private var mChatRoomMessageReceiveListener: QXIMClient.OnChatRoomMessageReceiveListener? = null

    override fun getLayoutId(): Int {
        return R.layout.imui_activity_chat
    }

    override fun setConversationType(): String {
        return ConversationType.TYPE_CHAT_ROOM
    }

    override fun init(saveInstanceState: Bundle?) {
        swipe_refresh_layout.isEnabled = false
        mChatRoomMessageReceiveListener = object : QXIMClient.OnChatRoomMessageReceiveListener {

            override fun onReceiveNewChatRoomMessage(message: Message) {
                //刷新列表
                refreshListForInsert(arrayOf(message), FLAG_NEW_MESSAGE)
            }

            override fun onReceiveGetAttribute(data: HashMap<String, String>) {
            }

        }

        mChatNoticeReceivedListener = object : QXIMClient.OnChatNoticeReceivedListener {

            override fun onGroupGlobalMute(isEnabled: Boolean) {
            }

            override fun onGroupAllMute(groupId: String, isEnabled: Boolean) {
            }

            override fun onGroupMute(groupId: String, isEnabled: Boolean) {
            }

            override fun onChatRoomGlobalMute(isEnabled: Boolean) {
                QLog.i(TAG, "全局聊天室禁言，isEnabled=$isEnabled")
                checkChatRoomMute()
            }

            override fun onChatRoomBan(chatRoomId: String, isEnabled: Boolean) {
                QLog.i(TAG, "聊天室成员封禁，chatRoomId=$chatRoomId+ isEnabled=$isEnabled")
                if (isEnabled) {
                    Toast.makeText(
                        this@ChatRoomActivity,
                        getString(R.string.qx_chatroom_forbid_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ChatRoomActivity,
                        getString(R.string.qx_chatroom_forbid_join),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }

            override fun onChatRoomMute(chatRoomId: String, isEnabled: Boolean) {
                QLog.i(TAG, "聊天室成员禁言，chatRoomId=$chatRoomId+ isEnabled=$isEnabled")
                if (chatRoomId == targetId) {
                    checkChatRoomMute()
                }
            }

            override fun onChatRoomDestroy() {
                QLog.i(TAG, "聊天室销毁")
                Toast.makeText(
                    this@ChatRoomActivity,
                    getString(R.string.qx_chatroom_destory),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        QXIMClient.instance!!.addOnChatNoticeReceivedListener(mChatNoticeReceivedListener!!)
        QXIMClient.instance!!.setChatRoomMessageReceiveListener(mChatRoomMessageReceiveListener!!)
        checkChatRoomMute()
    }


    override fun updateDraft() {
    }

    override fun onAdvancedBack() {
    }

    override fun onClearMessage() {
    }

    /**
     * 检测禁言缓存
     */
    private fun checkChatRoomMute() {
        if (MuteCache.isChatRoomGlobalMute()) {
            updateInputViewByMute(getString(R.string.qx_chatroom_forbid_all_joined), true)
        } else {
            updateInputViewByMute(
                getString(R.string.qx_chatroom_forbid_everyone),
                MuteCache.isChatRoomMute(targetId!!)
            )
        }
    }

    override fun onDestroy() {
        QXIMClient.instance.removeChatRoomMessageReceiveListener()
        QXIMClient.instance.removeOnChatNoticeReceivedListener(mChatNoticeReceivedListener!!)

        QXIMClient.instance.exitChatRoom(targetId!!, object : QXIMClient.OperationCallback() {

            override fun onSuccess() {
            }


            override fun onFailed(error: QXError) {
                Toast.makeText(
                    this@ChatRoomActivity,
                    "退出聊天室失败，错误码：${error.code} 错误信息：${error.msg}",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
        super.onDestroy()
    }

}