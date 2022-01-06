package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.ConversationModel
import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.ConversationEntity
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.pojo.UserInfoCache
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.qx.it.protos.S2CSessionUnReadCount
import io.netty.channel.ChannelHandlerContext


/**
 * 处理别人发送给自己的消息，自己还没阅读的数量，在登录验证成功后，服务器返回此消息
 */
class UnReadCountHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var unReadCount = S2CSessionUnReadCount.SessionUnReadCount.parseFrom(recMessage!!.contents)
        //QLog.i("UnReadCountHandler", "消息未读会话RECYCLER ITEMSHULINAG--unReadsList.size-->：" + unReadCount.unReadsList.size)
        var list = arrayListOf<ConversationEntity>()
        //1.更新数据库
        for (data in unReadCount.unReadsList) {
            var messageEntity = MessageConvertUtil.instance.convertToMessageEntity(data.lastMsg)
            if (messageEntity.messageType == MessageType.TYPE_RECALL) {
                messageEntity.messageId = data.lastMsg.recall.targetMessageId
            }
            messageEntity.state = MessageEntity.State.STATE_RECEIVED
            //确保会话的from为本人，to为对方
            if (messageEntity.to == UserInfoCache.getUserId() || messageEntity.to.isEmpty()) {
                messageEntity.to = data.from
            }
            var conversationEntity = ConversationModel.instance.generateConversation(messageEntity)
            messageEntity.conversationId = conversationEntity.conversationId

            val result = IMDatabaseRepository.instance.insertMessage(messageEntity)   //todo 此处决定了离线+在线消息的插入,删除则离线消息插入不成功
            QLog.d("UnReadCountHandler", "insertMessage result:${result.newMessageCount}, conversationid:${conversationEntity.conversationId},message $messageEntity")
            QLog.i("UnReadCountHandler", "消息未读会话--atCount-->：" + data.atCount)
            if (data.atCount > 0) {
                conversationEntity.mentionedCount = data.atCount
            }
            conversationEntity.unReadCount = data.count
            if (IMDatabaseRepository.instance.insertConversation(conversationEntity) > 0) {
                //这里要处理会话最新一条消息时间
                IMDatabaseRepository.instance.refreshConversationInfo(
                    MessageConvertUtil.instance.convertToMessageEntity (data.lastMsg) )
                if (conversationEntity.isNew) {
                    ConversationModel.instance.getConversationProperty(conversationEntity)
                }
            }
            var c = IMDatabaseRepository.instance.getConversationById(conversationEntity.conversationId)
            list.add(c!!)
        }
        //  QLog.i("UnReadCountHandler", "消息未读会话--list-->：" + Gson().toJson(list))
        if (list.isNotEmpty()) {
            //通知UI
            EventBusUtil.postConversationUpdate(list)
        }
    }
}