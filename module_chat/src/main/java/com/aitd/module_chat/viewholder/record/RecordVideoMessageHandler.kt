package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.VideoMessage
import com.aitd.module_chat.utils.TimeUtil
import com.aitd.module_chat.utils.file.GlideUtil
import kotlinx.android.synthetic.main.imui_layout_record_content_video.view.*

class RecordVideoMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_video)
        super.setContentView(itemView, contentLayout, message)

        var fileMessage = message.messageContent as VideoMessage

        GlideUtil.loadImage(itemView.context, fileMessage.originUrl, contentView!!.iv_icon)
        contentView!!.tv_duration.text = TimeUtil.getDuration(fileMessage.duration)
    }
}