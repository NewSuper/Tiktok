package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import kotlinx.android.synthetic.main.imui_layout_record_content_text.view.*

class RecordCallMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_text)
        super.setContentView(itemView, contentLayout, message)

        contentView!!.tv_text_msg_content.text = "[不支持的消息类型]"
    }
}