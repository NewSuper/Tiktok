package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.jobqueue.JobManagerUtil.Companion.instance
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.S2CAckStatus
import io.netty.channel.ChannelHandlerContext

class ACKHandler : BaseCmdHandler() {
    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        val status = S2CAckStatus.AckStatus.parseFrom(recMessage.contents)
        QLog.i("ACKHandler",
            "收到ACK回复 sequence：" + recMessage.sequence + " code=" + status.code + " message=" + status.message
        )
        if (status.code == 1) {
            //如果为1，则表示成功
            instance.callbackSuccess(recMessage.sequence, status)
        } else {
            //回调失败返回给上层
            instance.callbackFailed(recMessage.sequence, status.code, status.message)
        }
        //取消正在计时的message timer
        instance.removeMessageTimer(recMessage.sequence)

    }

}