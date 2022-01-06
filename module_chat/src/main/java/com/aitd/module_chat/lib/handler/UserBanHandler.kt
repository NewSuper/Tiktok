package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.listener.ConnectionStatusListener
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import io.netty.channel.ChannelHandlerContext
import org.greenrobot.eventbus.EventBus


class UserBanHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {

        QLog.i("UserBanHandler", "cmd=" + recMessage!!.cmd + "用户被封禁")

        EventBus.getDefault().post(ConnectionStatusListener.Status.CONN_USER_BLOCKED)
    }
}