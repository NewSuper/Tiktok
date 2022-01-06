package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import io.netty.channel.ChannelHandlerContext

class SystemBusyHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {

        QLog.i("SystemBusyHandler",  "cmd=" + recMessage!!.cmd + "系统繁忙")
    }
}