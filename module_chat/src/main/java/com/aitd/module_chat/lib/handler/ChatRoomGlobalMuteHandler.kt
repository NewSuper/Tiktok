package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.ChatNotice
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CGlobalOperation
import io.netty.channel.ChannelHandlerContext

class ChatRoomGlobalMuteHandler: BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var opr = S2CGlobalOperation.GlobalOperation.parseFrom(recMessage!!.contents)
        QLog.i("ChatRoomGlobalMuteHandler", "cmd=" + recMessage!!.cmd + "聊天室【全局禁言】："+" userId="+opr.userId +" type="+opr.type)
        var notice = ChatNotice()
        notice.isGlobal = true
        notice.sendType = ConversationType.TYPE_CHAT_ROOM
        notice.userId = opr.userId
        notice.type = opr.type
        EventBusUtil.postChatNotice(notice)
    }
}