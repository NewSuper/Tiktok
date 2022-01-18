package com.aitd.module_chat.lib

import com.aitd.module_chat.Conversation
import com.aitd.module_chat.Message
import com.aitd.module_chat.ReplyMessage
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.pojo.QXFavorite
import com.google.gson.Gson

object ConvertUtil {
    fun getMessageType(message: Message): Int {
        when (message.messageType) {
            MessageType.TYPE_TEXT -> {
                return 0
            }
            MessageType.TYPE_AUDIO -> {
                return 1
            }
            MessageType.TYPE_VIDEO -> {
                return 2
            }
            MessageType.TYPE_IMAGE -> {
                return 3
            }
            MessageType.TYPE_FILE -> {
                return 4
            }
            MessageType.TYPE_IMAGE_AND_TEXT -> {
                return 5
            }
            MessageType.TYPE_GEO -> {
                return 6
            }
        }
        return -1
    }

    fun getSessionType(type: String): Int {
        when (type) {
            Conversation.Type.TYPE_PRIVATE -> {
                return 0
            }
            Conversation.Type.TYPE_GROUP -> {
                return 1
            }
        }
        return -1
    }

    fun covert(message: Message): QXFavorite? {
        var messageType = getMessageType(message)
        var sessionType = getSessionType(message.conversationType)
        var content = Gson().toJson(message.messageContent)
        return QXFavorite(
            0,
            message.messageId,
            messageType,
            message.senderUserId,
            sessionType,
            content
        )
    }

    fun convertToFavorite(favorites: List<Message>): List<QXFavorite> {
        var list = arrayListOf<QXFavorite>()
        for (message in favorites) {
            var f = covert(message)
            if (f != null) {
                if (message.messageType == MessageType.TYPE_REPLY) {
                    var replyMessage = message.messageContent as ReplyMessage
                    f = covert(replyMessage.answer)
                }
                if (f != null) {
                    list.add(f!!)
                }
            }
        }
        return list
    }
}