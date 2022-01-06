package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CAtMessages


class OfflineAtToMessageHandler : GroupOfflineMessageHandler() {

    override fun getMessages(recMessage: S2CRecMessage): List<Any> {
        var messageBlock = arrayListOf<List<MessageEntity>>()
        var messageList = arrayListOf<MessageEntity>()
        var atMessages = S2CAtMessages.AtMessages.parseFrom(recMessage.contents)
        var tagsList = atMessages.tagsList
        // QLog.d("OfflineAtToMessageHandler","-------messageBlock----tagsList------"+Gson().toJson(tagsList))
        if(tagsList != null && tagsList.isNotEmpty()) {
            //遍历群list：这里是多个群，也就是多个会话
            for(tag in tagsList) {
                //遍历群组中的@消息块
                for(at in tag.atsList) {
                    //遍历@消息块中的消息list
                    for(msg in at.atMsgsList) {
                        var entity = MessageConvertUtil.instance.convertToMessageEntity(msg)
                        messageList.add(entity)
                    }
                }
                messageBlock.add(messageList)
            }
        }
        QLog.d("OfflineAtToMessageHandler", "-------messageBlock------$messageBlock")
        return messageBlock
    }
}