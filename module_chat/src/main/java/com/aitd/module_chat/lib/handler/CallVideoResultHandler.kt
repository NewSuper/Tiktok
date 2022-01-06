package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.event.CallEvent
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CVideoCall
import com.qx.it.protos.S2CVideoCallResult
import io.netty.channel.ChannelHandlerContext

class CallVideoResultHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage) {
        val data = S2CVideoCallResult.VideoCallResult.parseFrom(recMessage.contents)
        QLog.d("CallVideoResultHandler","$data")
    }
}