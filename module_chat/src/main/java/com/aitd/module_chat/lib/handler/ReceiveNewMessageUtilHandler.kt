package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.InsertMessageResult
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.S2CCustomMessage


abstract class ReceiveNewMessageUtilHandler : CustomMessageHandler() {

    override fun notifyUiUpdate(result: InsertMessageResult) {
        if (result.messages.size > 0) {
            //在此叠加消息未读数
            IMDatabaseRepository.instance.addConversationUnReadCount(
                result.messages[0].sendType, result.messages[0].from, result.messages[0].to, result.newMessageCount,0 )
            EventBusUtil.postNewMessageEntity(result.messages)
        }
    }

    override fun getMessages(recMessage: S2CRecMessage): List<MessageEntity> {
        val msg = S2CCustomMessage.Msg.parseFrom(recMessage.contents)
        var entity = MessageConvertUtil.instance.convertToMessageEntity(msg)
        if(msg.messageType == MessageType.TYPE_RECALL) {
            entity.messageId = msg.recall.targetMessageId
        }
        entity.state = MessageEntity.State.STATE_RECEIVED
        return arrayListOf(entity)
    }

    override fun getLatestMessage(sendType: String, from: String, to: String): MessageEntity {
        TODO("Not yet implemented")
    }

    override fun isNeedCheckDeleteTime(): Boolean {
        return false
    }
}