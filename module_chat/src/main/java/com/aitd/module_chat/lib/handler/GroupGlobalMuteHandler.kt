package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.ChatNotice
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CGlobalOperation
import io.netty.channel.ChannelHandlerContext

class GroupGlobalMuteHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var opr = S2CGlobalOperation.GlobalOperation.parseFrom(recMessage!!.contents)

        QLog.i("GroupGlobalMuteHandler",  "cmd=" + recMessage!!.cmd + " 全局群组禁言 userId=" + opr.userId + " type=" + opr.type)

        var notice = ChatNotice()
        notice.isGlobal = true
        notice.sendType = ConversationType.TYPE_GROUP
        notice.userId = opr.userId
        notice.type = opr.type
        EventBusUtil.postChatNotice(notice)
    }
}