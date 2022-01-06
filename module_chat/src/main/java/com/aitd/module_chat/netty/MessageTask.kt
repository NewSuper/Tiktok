package com.aitd.module_chat.netty

import com.aitd.module_chat.lib.handler.BaseCmdHandler
import com.aitd.module_chat.utils.qlog.QLog
import io.netty.channel.ChannelHandlerContext

class MessageTask(var ctx: ChannelHandlerContext, var msg: S2CRecMessage) {

    fun handle(handler: BaseCmdHandler) {
        QLog.d("MessageTask","并发测试 消费任务")
        handler.handle(ctx, msg)
    }
}