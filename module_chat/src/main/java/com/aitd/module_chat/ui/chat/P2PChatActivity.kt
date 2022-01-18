package com.aitd.module_chat.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXUserInfoManager
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.ui.BaseChatActivity
import kotlinx.android.synthetic.main.imui_layout_chat_bottom.*


class P2PChatActivity : BaseChatActivity() {

    override fun getLayoutId(): Int {
        return R.layout.imui_activity_chat
    }
    override fun init(saveInstanceState: Bundle?) {
        isSmoothScrollToPosition = false
        //监听文本变化发送正在输入消息
        edt_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isNotEmpty()) {
//                    send(MessageCreator.instance.createInputStatusMessage(conversationType!!, QXIMClient.instance.getCurUserId()!!, targetId!!, "我是正在输入消息。。。",
//                            "扩展字段，随便填填"))
//                }
            }
        })
    }
    override fun setConversationType(): String {
        return ConversationType.TYPE_PRIVATE
    }

    override fun loadData() {
        QXUserInfoManager.getInstance().requestUserInfoUpdate(targetId)//请求更新用户信息
        super.loadData()
    }

    override fun loadUnReadMessage() {
        sendReadReceipt()
    }

    override fun onAdvancedBack() {
    }

    override fun onClearMessage() {
        //清空本地聊天记录成功
        mMessageList.clear()
        mMessageAdapter.notifyDataSetChanged()
    }
}

