package com.aitd.module_chat.lib.handler


import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.event.RTCEvent
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.C2SVideoRtcCandidate
import io.netty.channel.ChannelHandlerContext

class RTCCandidateHandler : BaseCmdHandler() {

    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage) {
        val resp =  C2SVideoRtcCandidate.VideoRtcCandidate.parseFrom(recMessage.contents)
        val event = RTCEvent(recMessage.cmd,resp)
        EventBusUtil.post(event)
    }
}