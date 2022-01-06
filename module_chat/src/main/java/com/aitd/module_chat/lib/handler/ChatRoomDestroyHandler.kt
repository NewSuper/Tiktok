package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import io.netty.channel.ChannelHandlerContext

class ChatRoomDestroyHandler: BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        QLog.i("ChatRoomDestroyHandler", "cmd=" + recMessage!!.cmd + "聊天室已销毁")
        EventBusUtil.postChatRoomDestroy()
    }
}