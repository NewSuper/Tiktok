package com.aitd.module_chat.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aitd.module_chat.Message
import com.aitd.module_chat.MessageContent
import com.aitd.module_chat.R
import com.aitd.module_chat.TextMessage
import java.lang.Exception

class ChatTextMessageHandler : ChatBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_msg_content_text)
        super.setContentView(itemView, contentLayout, message)
        var textMessage: MessageContent? = getMessageContent(message) ?: return
        textMessage = textMessage as TextMessage

        var textView = contentView!!.findViewById<TextView>(R.id.tv_text_msg_content)

        var lp = textView.layoutParams as ViewGroup.LayoutParams
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        textView.layoutParams = lp

        try {
            textView.text = textMessage!!.content
            /*val builder = SpannableStringBuilder(textMessage.content)
            //处理@
            if (message.conversationType == Conversation.Type.TYPE_GROUP && textMessage.atToMessageList != null && textMessage.atToMessageList.size > 0) {
                //处理@昵称 颜色
                for (at in textMessage.atToMessageList!!) {
                    var content = textMessage!!.content!!

                    if (at.atTo == "-1") {
                        var name = itemView.resources.getString(R.string.qx_at_to_all)
                        var index = content.indexOf(name)
                        if(index > 0) {
                            val span = ForegroundColorSpan(itemView.context.resources.getColor(R.color.message_content_text))
                            builder.setSpan(span, index, index + name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    } else {
                        var memberInfo = UserInfoUtil.getMemberInfo(message.targetId, at.atTo)
                        var name = "@" + memberInfo?.nickname
                        if (content.contains(name)) {
                            //如果文本内容中包含at清单中的用户名称，则做变色处理
                            var index = content.indexOf(name)
                            if(index > 0) {
                                val span = ForegroundColorSpan(itemView.context.resources.getColor(R.color.message_content_text))
                                builder.setSpan(span, index, index + name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                    }

                }
                textView.text = builder
            } else {
                textView.text = textMessage!!.content
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}