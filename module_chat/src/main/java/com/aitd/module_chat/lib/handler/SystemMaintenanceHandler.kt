package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import io.netty.channel.ChannelHandlerContext


class SystemMaintenanceHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        QLog.i("SystemMaintenanceHandler",  "cmd=" + recMessage!!.cmd + "系统维护")

    }
}