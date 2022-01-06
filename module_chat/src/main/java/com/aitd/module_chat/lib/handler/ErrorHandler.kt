package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.listener.ConnectionStatusListener
import com.aitd.module_chat.netty.HeartBeatTimeCheck
import com.aitd.module_chat.netty.NettyConnectionState
import com.aitd.module_chat.netty.NettyConnectionStateManager
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.S2CCommon
import io.netty.channel.ChannelHandlerContext

class ErrorHandler : BaseCmdHandler() {
    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        val error = S2CCommon.ErrorResponse.parseFrom(recMessage.contents)
        QLog.i("ErrorHandler",
            "channelRead 错误：cmd=" + recMessage.cmd + " getCode" + "=" + error.code + " message:" + error.message
        )
        HeartBeatTimeCheck.getInstance().cancelTimer()
        NettyConnectionStateManager.getInstance().state = NettyConnectionState.STATE_SERVER_REFUSE
        val status = ConnectionStatusListener.Status.SERVER_INVALID
        status.message = error.message
        EventBusUtil.post(status)

        if(error.code == 301) {
            //非法请求，拒绝服务，服务端需强制断开TCP连接，不可重连，协议文档：S2C_ErrorResponse
        } else {
            //可重连
        }
    }
}