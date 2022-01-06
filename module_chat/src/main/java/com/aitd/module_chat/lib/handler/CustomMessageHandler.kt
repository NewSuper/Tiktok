package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.EventMessage
import com.aitd.module_chat.lib.CustomEventManager
import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.MessageType
import io.netty.channel.ChannelHandlerContext

abstract class CustomMessageHandler : MessageContinuityCalcHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var messageBlock = getMessages(recMessage!!)
        if (messageBlock.isNullOrEmpty()) {
            return
        }
        var first = messageBlock.first()
        if (first is List<*>) {
            //多个会话，如:离线@功能
            for (block in messageBlock) {
                super.handle(ctx, recMessage, block as List<MessageEntity>)
            }
        } else if (first is MessageEntity) {
            //单个会话
            if (MessageType.isPreSetMessage(first.messageType)) {
                super.handle(ctx, recMessage, messageBlock as List<MessageEntity>)
            } else {
                //否则为Event消息或自定义消息
                for (msg in messageBlock) {
                    handleCustomMessage(ctx, recMessage, msg as MessageEntity)
                }
            }
        }
    }

    private fun handleCustomMessage(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?, msg: MessageEntity) {
        if (msg.messageType == MessageType.TYPE_EVENT) {
            //自定义事件，不做数据库存储，只做消息转发到UI层
            var message = MessageConvertUtil.instance.convertToMessage(msg)
            sendReceivedConfirm(arrayListOf(msg), ctx)//发送消息确认接收
            if (message?.messageContent != null && message?.messageContent is EventMessage) {
                var event = message?.messageContent as EventMessage
                var provider = CustomEventManager.getCustomEventProvider(event.event)
                provider?.onReceiveCustomEvent(message)
            }
        } else {
            //否则为自定义消息
            super.handle(ctx, recMessage, arrayListOf(msg))
        }
    }

    /**
     * 是否为自定义事件
     */
    private fun isCustomEvent(messageType: String): Boolean {
        return CustomEventManager.isExist(messageType)
    }

    /**
     * 是否为自定义消息
     */
    private fun isCustomMessage(messageType: String): Boolean {
        return CustomEventManager.isExist(messageType)
    }


}