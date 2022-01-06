package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CMessageRecord
import io.netty.channel.ChannelHandlerContext


class OfflineMessageHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        val record = S2CMessageRecord.MessageRecord.parseFrom(recMessage!!.contents)
        QLog.i("OfflineMessageHandler", "收到离线消息 数量=" + record.msgsCount
        )
        when (record.sendType) {
            ConversationType.TYPE_PRIVATE -> {
                P2POfflineMessageHandler().handle(ctx, recMessage)
            }
            ConversationType.TYPE_GROUP -> {
                GroupOfflineMessageHandler().handle(ctx, recMessage)
            }
            ConversationType.TYPE_SYSTEM -> {
                SystemOfflineMessageHandler().handle(ctx, recMessage)
            }
        }
    }
}