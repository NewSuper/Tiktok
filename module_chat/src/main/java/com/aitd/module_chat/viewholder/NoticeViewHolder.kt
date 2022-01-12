package com.aitd.module_chat.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.aitd.module_chat.CustomMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.NoticeMessage
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.MessageAdapter
import com.aitd.module_chat.lib.NoticeUtil
import com.aitd.module_chat.lib.provider.CustomMessageManager
import com.aitd.module_chat.utils.TimeUtil
import kotlinx.android.synthetic.main.imui_item_msg_notice.view.*

class NoticeViewHolder(contex: Context, itemView: View) : BaseViewHolder(contex,itemView) {
    var mContext = contex
    override fun fill(lastMsg: Message, currMsg: Message, listener: MessageAdapter.ItemListener, position: Int, checkable: Boolean) {
        super.fill(lastMsg, currMsg, listener, position, checkable)
        var noticeText = ""

        var commNoticelayout = itemView.findViewById<ConstraintLayout>(R.id.msg_comm_notice)
        if(commNoticelayout != null){
            commNoticelayout.setOnClickListener{
                listener?.onItemClick(position)
            }
        }

        when (currMsg?.messageContent) {
            //IM自带通知消息
            is NoticeMessage -> {
                noticeText = NoticeUtil.getNoticeContent(currMsg.targetId, (currMsg!!.messageContent as NoticeMessage), mContext)
                itemView.tv_notice_msg.text = noticeText
                itemView.tv_notice_time.text = TimeUtil.getTimeString(mContext,currMsg.timestamp)
            }
            //自定义通知消息
            is CustomMessage -> {
                var provider = CustomMessageManager.getNoticeProvider(currMsg.messageType)
                if (provider != null) {
                    //删除notice的子view，并重新加载自定义的notice view
                    var parent = itemView as ViewGroup
                    parent.removeAllViews()
                    var view = LayoutInflater.from(parent.context).inflate(provider.getViewId(), parent, true)

                    provider.bindView(view, currMsg)
                }
            }

        }
    }
}