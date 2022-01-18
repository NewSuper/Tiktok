package com.aitd.module_chat.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.Conversation
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.UserInfoUtil
import com.aitd.module_chat.pojo.AccountConfig
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.ConversationUtil
import com.aitd.module_chat.utils.TimeUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.imui_item_conversation.view.*

class ConversationAdapter(context: Context, private val myDataSet: ArrayList<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.MyViewHolder>() {

    lateinit var mOnItemClickListener: OnItemClickListener
    var context: Context = context

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_conversation, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // LogUtil.debug(this.javaClass, "position="+position+" size="+myDataSet.size)
        var data = myDataSet[position]
        //最后一条消息的状态
        if (data.lastMessage != null) {
            if (data.lastMessage?.state == Message.State.STATE_FAILED) {
                holder.itemView.tv_failed_status.visibility = View.VISIBLE
            } else {
                holder.itemView.tv_failed_status.visibility = View.GONE
            }
        }
        //如果@信息不为空
        if (!TextUtils.isEmpty(data.atTo)) {
            holder.itemView.tv_notice.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(data.draft)) {
                //如果草稿不为空，则显示草稿内容
                holder.itemView.tv_notice.text = "[草稿]"
                holder.itemView.tv_message.text = data.draft!!
            } else {
                //否则显示@信息
                var atto = context.getString(R.string.qx_conversation_at_to_me)
                if (data.atTo == "-1") {
                    //@所有人
                    atto = context.getString(R.string.qx_conversation_at_to_all)
                }

                holder.itemView.tv_notice.text = atto
                holder.itemView.tv_message.text =
                    ConversationUtil.getLastMessage(context, data.lastMessage)?.let {
                        getLatestMessage(holder.itemView.context, data.lastMessage,
                            it
                        )
                    }
            }
        } else {
            if (!TextUtils.isEmpty(data.draft)) {
                //如果草稿不为空，则显示草稿内容
                holder.itemView.tv_notice.visibility = View.VISIBLE
                holder.itemView.tv_notice.text = "[草稿]"
                holder.itemView.tv_message.text = data.draft!!
            } else {
                //否则隐藏通知tv
                holder.itemView.tv_notice.visibility = View.GONE
                holder.itemView.tv_message.text =
                    ConversationUtil.getLastMessage(context, data.lastMessage)?.let {
                        getLatestMessage(holder.itemView.context, data.lastMessage,
                            it
                        )
                    }
            }
        }

        holder.itemView.tv_text_time.text = TimeUtil.getTimeString(context,data.timestamp)
        if (data.unReadCount == 0) {
            holder.itemView.tv_unread_count.visibility = View.GONE
        } else {
            holder.itemView.tv_unread_count.visibility = View.VISIBLE
            if (data.unReadCount > 99) {
                holder.itemView.tv_unread_count.text = "99+"
            } else {
                holder.itemView.tv_unread_count.text = data.unReadCount.toString()
            }
        }

        if (data.top == Conversation.TopState.TOP_STATE_ENABLE) {
            //置顶

            holder.itemView.setBackgroundColor(
                context.resources.getColor(
                    R.color.item_conversation_is_top_enable
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                context.resources.getColor(
                    R.color.item_conversation_is_top_disable
                )
            )
        }

        when (data.conversationType) {
            ConversationType.TYPE_PRIVATE -> {
                Glide.with(holder.itemView.context).load(AccountConfig.getAccountIcon(data.targetId))
                    .apply(
                        RequestOptions.bitmapTransform(
                        CircleCrop()
                    ).placeholder(R.mipmap.chat_avatar_default)).into(holder.itemView.iv_text_avatar)
                holder.itemView.tv_conversation_name.text = AccountConfig.getAccountName(data.targetId)
            }
            ConversationType.TYPE_GROUP -> {
                Glide.with(holder.itemView.context).load(
                    R.mipmap.ic_group_img
                ).apply(
                    RequestOptions.bitmapTransform(
                    CircleCrop()
                ).placeholder(R.mipmap.chat_avatar_default)).into(holder.itemView.iv_text_avatar)
                holder.itemView.tv_conversation_name.text = "群聊[" + data.targetId + "]"

            }
            ConversationType.TYPE_SYSTEM -> {
                Glide.with(holder.itemView.context).load(R.mipmap.chat_avatar_system).apply(
                    RequestOptions.bitmapTransform(
                    CircleCrop()
                )).into(holder.itemView.iv_text_avatar)
                holder.itemView.tv_conversation_name.text = "系统消息"
            }
        }

        holder.itemView.iv_no_disturbing.visibility = if (data.noDisturbing == Conversation.NoDisturbingState.NO_DIST_STATE_ENABLE) {
            //如果会话为免打扰模式
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onClick(position)
        }

        holder.itemView.setOnLongClickListener {
            mOnItemClickListener?.onLongClick(position, it)
        }
    }

    override fun getItemCount() = myDataSet.size

    fun getLatestMessage(context: Context, message: Message?, messageStr: String): String {
        if (message == null) {
            return ""
        }
        var format = context.resources.getString(R.string.conversation_content)
        return String.format(format, UserInfoUtil.getSenderName(context, message, message.targetId), messageStr)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onLongClick(position: Int, v: View): Boolean
    }

}