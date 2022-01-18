package com.aitd.module_chat.pojo

import com.aitd.module_chat.lib.db.entity.MessageEntity

class InsertMessageResult {
    var newMessageCount = 0
    var messages = ArrayList<MessageEntity>()
}