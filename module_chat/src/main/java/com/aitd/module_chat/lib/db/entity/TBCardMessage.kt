package com.aitd.module_chat.lib.db.entity

import androidx.room.*
import com.aitd.module_chat.utils.UUIDUtil


@Entity(tableName = "card_manage")
class TBCardMessage() {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUIDUtil.getUUID().toString()

    @ColumnInfo(name = "message_id")
    var messageId: String = ""

    @ColumnInfo(name = "conversation_id")
    var conversationId: String = ""

    @ColumnInfo(name = "origin_url")
    var originUrl :String=""

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0
}