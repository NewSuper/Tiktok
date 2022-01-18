package com.aitd.module_chat.ui.chat

import android.view.View
import com.aitd.module_chat.pojo.ConversationType
import kotlinx.android.synthetic.main.imui_activity_chat.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*

class SystemChatActivity : GroupChatActivity() {

    override fun setConversationType(): String {
        return ConversationType.TYPE_SYSTEM
    }

    override fun initView() {
        super.initView()
        layout_input_panel.visibility = View.GONE
        iv_menu.visibility = View.GONE
    }

    override fun updateDraft() {

    }
}