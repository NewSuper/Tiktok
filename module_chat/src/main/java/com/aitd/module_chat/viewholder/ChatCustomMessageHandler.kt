package com.aitd.module_chat.viewholder

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.Message
import com.aitd.module_chat.lib.provider.CustomMessageManager
import com.aitd.module_chat.lib.provider.MessageProvider

class ChatCustomMessageHandler : ChatBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var provider: MessageProvider? = CustomMessageManager.getMessageProvider(message.messageType)
            ?: return

        val layout = provider?.getViewId() ?: return
        createContentView(itemView, contentLayout, layout)
        super.setContentView(itemView, contentLayout, message)

        provider?.bindView(itemView, message)
    }

}