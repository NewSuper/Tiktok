package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.RecordMessage
import com.aitd.module_chat.lib.UserInfoUtil
import kotlinx.android.synthetic.main.imui_layout_record_content_record.view.*

class RecordRecordMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_record)
        super.setContentView(itemView, contentLayout, message)
        var recordMessage = message.messageContent as RecordMessage

        contentLayout.tv_record_title.text = UserInfoUtil.getRecordTitle(itemView.context, message)
        contentLayout.tv_record_content.text = UserInfoUtil.getRecordText(itemView.context, recordMessage)
    }
}