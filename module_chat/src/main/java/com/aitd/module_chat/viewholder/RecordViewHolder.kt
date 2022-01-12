package com.aitd.module_chat.viewholder

import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.viewholder.record.ChatRecordAdapter
import com.aitd.module_chat.lib.UserInfoUtil
import com.aitd.module_chat.utils.TimeUtil
import com.aitd.module_chat.utils.file.GlideUtil
import com.aitd.module_chat.viewholder.record.RecordListDispatcher
import kotlinx.android.synthetic.main.imui_item_message_record.view.*

class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun fill(currMsg: Message, lastMsg: Message, position: Int, mOnItemClickListener: ChatRecordAdapter.OnItemClickListener?) {
        var layoutDate = itemView.findViewById<LinearLayout>(R.id.layout_date)
        var layoutMessageContent = itemView.findViewById<ConstraintLayout>(R.id.layout_message_content)
        //处理消息列表显示的消息时间
        if (!TimeUtil.isSameday(currMsg.timestamp, lastMsg.timestamp) || (currMsg.messageId == lastMsg.messageId)) {
            layoutDate.tv_date.text = TimeUtil.getDate(currMsg.timestamp)
            layoutDate.visibility = View.VISIBLE
        } else {
            layoutDate.visibility = View.GONE
        }
        //处理头像、昵称
        GlideUtil.loadAvatar(itemView.context,
            UserInfoUtil.getAvatar(currMsg, currMsg.senderUserId), itemView.iv_avatar)
        itemView.tv_nick_name.text = UserInfoUtil.getTargetName(itemView.context, currMsg, currMsg.targetId)
        itemView.tv_time.text = TimeUtil.getHourAndMin(currMsg.timestamp)
        layoutMessageContent.setOnClickListener {
            mOnItemClickListener?.onClick(position, layoutMessageContent)
        }
        RecordListDispatcher.dispatch(itemView, layoutMessageContent, currMsg)
    }
}