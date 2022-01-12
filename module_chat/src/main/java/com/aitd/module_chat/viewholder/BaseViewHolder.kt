package com.aitd.module_chat.viewholder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.MessageAdapter
import com.aitd.module_chat.utils.TimeUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.imui_layout_msg_failed_by_refuse.view.*


abstract class BaseViewHolder(context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    var checkable = false
    var context = context
    private var INTERVAL_MESSAGE_TIME = 300000//显示消息间隔默认5分钟

    open fun fill(
        lastMsg: Message,
        currMsg: Message,
        listener: MessageAdapter.ItemListener,
        position: Int,
        checkable: Boolean) {
        //处理消息列表显示的时间
        if (currMsg.timestamp - lastMsg.timestamp >= INTERVAL_MESSAGE_TIME ||
            (currMsg.messageId == lastMsg.messageId)
        ) {
            itemView.tv_time_msg_time?.text = TimeUtil.getTimeString(context, currMsg.timestamp)
            itemView.tv_time_msg_time?.visibility = View.VISIBLE
        } else {
            itemView.tv_time_msg_time?.visibility = View.GONE
        }
    }

    protected fun setBroadCastView(
        to: String,
        textView: TextView,
        imageView: ImageView,
        context: Context) {
        //判断是否为广播，to为空即为广播
        if (to.isEmpty()) {
          textView.text = context.getString(R.string.qx_message_broadcast)
            Glide.with(context).load(R.mipmap.ic_launcher).apply(
                RequestOptions.bitmapTransform(
                    CircleCrop()
                )
            ).into(imageView)
        }
    }
}