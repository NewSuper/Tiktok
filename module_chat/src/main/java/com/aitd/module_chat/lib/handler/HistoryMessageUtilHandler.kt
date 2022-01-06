package com.aitd.module_chat.lib.handler

import android.util.Log
import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.InsertMessageResult
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.pojo.UserInfoCache
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.S2CMessageRecord


open class HistoryMessageUtilHandler: CustomMessageHandler() {
    private val TAG = "HistoryMessageUtilHandl"

    override fun getLatestMessage(sendType: String, from: String, to: String): MessageEntity? {
        return IMDatabaseRepository.instance.getLatestMessage(sendType, from, to, UserInfoCache.getUserId())
    }

    override fun setProperties(recMessage: S2CRecMessage) {
        var record = S2CMessageRecord.MessageRecord.parseFrom(recMessage.contents)
        super.setProperties(record.sendType, record.targetId)
    }

    override fun notifyUiUpdate(result: InsertMessageResult) {
        Log.e(TAG, "notifyUiUpdate: 发送EventBus更新UI:" + result.messages.size)
        EventBusUtil.postHistoryMessage(result.messages)
    }

    override fun getMessages(recMessage: S2CRecMessage): List<Any> {
        var list = arrayListOf<MessageEntity>()
        var record = S2CMessageRecord.MessageRecord.parseFrom(recMessage.contents)
        for (msg in record.msgsList) {
            var entity = MessageConvertUtil.instance.convertToMessageEntity(msg)
            if(msg.messageType == MessageType.TYPE_RECALL) {
                entity.messageId = msg.recall.targetMessageId
            }
            entity.state = MessageEntity.State.STATE_RECEIVED
            list.add(entity)
        }
        return list
    }

    override fun isNeedCheckDeleteTime(): Boolean {
        return true
    }
}