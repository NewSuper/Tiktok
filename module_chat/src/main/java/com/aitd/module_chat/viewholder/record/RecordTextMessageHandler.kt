package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.TextMessage
import kotlinx.android.synthetic.main.imui_layout_record_content_text.view.*

class RecordTextMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_text)
        super.setContentView(itemView, contentLayout, message)

        var textMessage = message.messageContent as TextMessage
        contentView!!.tv_text_msg_content.text = textMessage.content
    }
}