package com.aitd.module_chat.lib.provider

import android.content.Context
import android.view.View
import com.aitd.module_chat.Message

abstract class MessageProvider {

    abstract val providerTag: String
    abstract val bubbleStyle: BubbleStyle

    /**
     * 获取通知消息文本
     */
    open fun getNoticeText(context : Context): String {
        return ""
    }

    /**
     * 是否为通知消息，如果为true，则需要同时重载getNoticeText方法
     */
    open fun isNotice(): Boolean {
        return false
    }

    /**
     * 初始化view
     */
    abstract fun getViewId(): Int

    /**
     * 操作view
     */
    abstract fun bindView(view: View?, data: Message)

    abstract fun onClick(view: View?, data: Message)

    class BubbleStyle(var left: Style = Style.WHITE, var right: Style = Style.WHITE) {

        enum class Style {
            WHITE,
            BLUE;
        }
    }
}