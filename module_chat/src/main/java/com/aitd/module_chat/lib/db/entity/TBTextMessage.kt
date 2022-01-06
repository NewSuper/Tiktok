package com.aitd.module_chat.lib.db.entity

import androidx.room.*
import com.aitd.module_chat.pojo.UserInfoCache
import com.aitd.module_chat.utils.UUIDUtil

@Entity(tableName = "text_message")
class TBTextMessage {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUIDUtil.getUUID().toString()

    @ColumnInfo(name = "message_id")
    var messageId: String = ""

    @ColumnInfo(name = "conversation_id")
    var conversationId: String = ""

    @ColumnInfo(name = "owner_id")
    var ownerId: String = ""

    @ColumnInfo(name = "content")
    var content: String? = ""

    @ColumnInfo(name = "extra")
    var extra: String? = ""

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0


    companion object {
        fun obtain(conversationId : String, messageId: String, content: String, extra: String): TBTextMessage {
            var textMessage = TBTextMessage()
            textMessage.conversationId = conversationId
            textMessage.messageId = messageId
            textMessage.content = content
            textMessage.ownerId = UserInfoCache.getUserId()
            textMessage.extra = extra
            return textMessage
        }
    }
}