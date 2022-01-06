package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CMessageRecord
import io.netty.channel.ChannelHandlerContext

/**
 * 历史消息处理类，本类只做一个消息类型分发，不做具体处理
 */
class HistoryMessageHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        val record = S2CMessageRecord.MessageRecord.parseFrom(recMessage.contents)
        QLog.i("HistoryMessageHandler", "收到历史消息 数量=" + record.msgsCount
        )
        HistoryMessageUtilHandler().handle(ctx, recMessage)
    }
}