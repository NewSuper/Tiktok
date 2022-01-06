package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.event.CallEvent
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CVideoCall
import io.netty.channel.ChannelHandlerContext

/**
 * 收到音视频呼叫
 */
class CallIncomeHandler: BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        val data = S2CVideoCall.VideoCall.parseFrom(recMessage!!.contents)
        // 收到音视频消息这时候没有入本地数据库的，
        // 服务端会通话挂单或者取消后发送一条p2p消息入本地数据库作为本次通话的消息
        // 因为多进程需要将本消息转发到qximclient中处理
        EventBusUtil.post(
            CallEvent(recMessage.cmd,data.roomId,data.sendType,
                data.targetId,data.type,data.userId,data.membersList)
        )
        QLog.i("CallIncomeHandler","收到音视频消息呼叫：${data.roomId},：${data.type}")
    }
}