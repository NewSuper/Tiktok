package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.S2CCustomMessage
import io.netty.channel.ChannelHandlerContext

class ReceiveChatRoomMessageHandler : BaseCmdHandler() {
    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {

        var msg = S2CCustomMessage.Msg.parseFrom(recMessage.contents)
        QLog.i("ReceiveChatRoomMessageHandler", "sendType=" + msg.sendType + " messageType=" + msg.messageType)
        var messageEntity = MessageConvertUtil.instance.convertToMessageEntity(msg)
        EventBusUtil.postNewChatRoomMessageEntity(messageEntity)
    }

}