package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import kotlinx.android.synthetic.main.imui_layout_record_content_voice.view.*

class RecordVoiceMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_voice)
        super.setContentView(itemView, contentLayout, message)
        contentView!!.tv_audio_msg.text = "[不支持的消息类型]"
    }
}