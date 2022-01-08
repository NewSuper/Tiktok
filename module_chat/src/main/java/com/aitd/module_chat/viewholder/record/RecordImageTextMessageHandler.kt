package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.ImageTextMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.utils.file.GlideUtil
import kotlinx.android.synthetic.main.imui_layout_record_content_image_text.view.*

class RecordImageTextMessageHandler : RecordBaseMessageHandler() {

    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_image_text)
        super.setContentView(itemView, contentLayout, message)

        var imageTextMessage = message.messageContent as ImageTextMessage

        GlideUtil.loadImage(itemView.context, imageTextMessage.imageUrl, contentView!!.iv_icon)
        contentView!!.tv_title.text = imageTextMessage.title
        contentView!!.tv_description.text = imageTextMessage.content
    }
}