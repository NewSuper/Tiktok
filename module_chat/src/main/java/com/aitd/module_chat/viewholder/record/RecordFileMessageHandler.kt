package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.FileMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.utils.file.FileUtil
import com.aitd.module_chat.utils.file.SizeUtil
import kotlinx.android.synthetic.main.imui_layout_record_content_file.view.*

class RecordFileMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_file)
        super.setContentView(itemView, contentLayout, message)

        var fileMessage = message.messageContent as FileMessage

        contentView!!.iv_icon.setImageResource(FileUtil.getResource(fileMessage))
        contentView!!.tv_title.text = fileMessage.fileName
        contentView!!.tv_size.text = SizeUtil.getDisplaySize(fileMessage.size)
    }
}