package com.aitd.module_chat.lib

import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.ConversationEntity
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.lib.jobqueue.JobManagerUtil
import com.aitd.module_chat.QXError
import com.aitd.module_chat.listener.ResultCallback
import com.aitd.module_chat.netty.S2CSndMessage
import com.aitd.module_chat.netty.SystemCmd
import com.aitd.module_chat.pojo.UserInfoCache
import com.aitd.module_chat.utils.TargetIdUtil
import com.qx.it.protos.S2CSpecialOperation
import java.util.concurrent.ConcurrentHashMap

class ConversationModel {

    /**
     * 创建会话实体，不要每次从数据库中获取，应该增加缓存会话的机制
     */
    fun generateConversation(messageEntity: MessageEntity?): ConversationEntity {
        //先根据消息信息获取会话是否存在，如果存在直接返回

        var targetId = TargetIdUtil.getTargetId(messageEntity)
        var conversationEntity = IMDatabaseRepository.instance.getConversation(
            messageEntity!!.sendType, targetId
        )

        if (conversationEntity != null) {
            conversationEntity.deleted = 0//重置为非删除状态
            conversationEntity.timestamp = messageEntity.timestamp
            return conversationEntity
        }

        //否则创建一个新的会话
        conversationEntity = ConversationEntity()
        conversationEntity.deleted = 0//重置为非删除状态
        conversationEntity.isNew = true
        conversationEntity.timestamp = messageEntity.timestamp
        conversationEntity.conversationType = messageEntity!!.sendType
        conversationEntity.ownerId = UserInfoCache.getUserId()
        //此处用于发消息时适用
        conversationEntity.targetId = targetId

        return conversationEntity
    }

    /**
     * 获取会话属性：置顶和免打扰
     */
    fun getConversationProperty(conversationEntity: ConversationEntity) {
        var top = createGetTopPropertyMsg(conversationEntity)
        var nodisturb = createGetNoDisturbPropertyMsg(conversationEntity)
        JobManagerUtil.instance.postMessage(top, object : ResultCallback {

            override fun onSuccess() {
            }

            override fun onFailed(error: QXError) {
            }

        })
        JobManagerUtil.instance.postMessage(nodisturb, object : ResultCallback {

            override fun onSuccess() {
            }

            override fun onFailed(error: QXError) {
            }

        })
    }

    private fun createGetTopPropertyMsg(conversationEntity: ConversationEntity): S2CSndMessage {

        var targetId = conversationEntity.targetId
        var body = S2CSpecialOperation.SpecialOperation.newBuilder().setSendType(conversationEntity.conversationType)
            .setTargetId(targetId).setUserId(UserInfoCache.getUserId()).build()

        var msg = S2CSndMessage()
        msg.cmd = SystemCmd.C2S_GET_TOP_PROPERTY
        msg.body = body
        return msg
    }

    private fun createGetNoDisturbPropertyMsg(conversationEntity: ConversationEntity): S2CSndMessage {

        var targetId = conversationEntity.targetId
        var body = S2CSpecialOperation.SpecialOperation.newBuilder().setSendType(conversationEntity.conversationType)
            .setTargetId(targetId).setUserId(UserInfoCache.getUserId()).build()

        var msg = S2CSndMessage()
        msg.cmd = SystemCmd.C2S_GET_NO_DISTURB_PROPERTY
        msg.body = body
        return msg
    }


    internal object Holder {
        val holder = ConversationModel()
    }

    companion object {
        val instance = Holder.holder
        val mMap = ConcurrentHashMap<String, ConversationEntity>()
    }
}