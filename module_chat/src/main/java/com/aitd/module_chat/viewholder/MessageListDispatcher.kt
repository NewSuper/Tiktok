package com.aitd.module_chat.viewholder

import com.aitd.module_chat.Message
import com.aitd.module_chat.ReplyMessage
import com.aitd.module_chat.pojo.MessageType

object MessageListDispatcher : BaseContentViewDispatcher() {

    //覆盖父类方法
    override fun getMessageType(currMsg: Message): String {
        return when (currMsg.messageType) {
            //回复布局
            MessageType.TYPE_REPLY -> {
                var reply = currMsg.messageContent as ReplyMessage
                reply.answer?.messageType!!
            }
            else -> {
                currMsg.messageType
            }
        }
    }

    override fun createTextHandler(): ChatBaseMessageHandler {
        return ChatTextMessageHandler()
    }

    override fun createImageHandler(): ChatBaseMessageHandler {
        return ChatImageMessageHandler()
    }

    override fun createImageTextHandler(): ChatBaseMessageHandler {
        return ChatImageTextMessageHandler()
    }

    override fun createAudioHandler(): ChatBaseMessageHandler {
        return ChatVoiceMessageHandler()
    }

    override fun createVideoHandler(): ChatBaseMessageHandler {
        return ChatVideoMessageHandler()
    }

    override fun createCallHandler(): ChatBaseMessageHandler {
        return ChatCallMessageHandler()
    }

    override fun createFileHandler(): ChatBaseMessageHandler {
        return ChatFileMessageHandler()
    }

    override fun createGeoHandler(): ChatBaseMessageHandler {
        return ChatGeoMessageHandler()
    }

    override fun createRecordHandler(): ChatBaseMessageHandler {
        return ChatRecordMessageHandler()
    }

    override fun createCustomHandler(): ChatBaseMessageHandler {
        return ChatCustomMessageHandler()
    }


}