package com.aitd.module_chat.utils

import android.content.Context
import com.aitd.module_chat.*
import com.aitd.module_chat.lib.MessageConvertUtil
import com.aitd.module_chat.lib.NoticeUtil
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.ConversationEntity
import com.aitd.module_chat.pojo.UserInfoCache

object ConversationUtil {
    fun toConversationEntity(conversation: Conversation): ConversationEntity {
        var conversationEntity = ConversationEntity()
        conversationEntity.conversationId = conversation.conversationId
        conversationEntity.ownerId = conversation.ownerId
        conversationEntity.conversationType = conversation.conversationType
        conversationEntity.targetId = conversation.targetId
        conversationEntity.icon = conversation.icon
        conversationEntity.targetName = conversation.targetName
        conversationEntity.draft = conversation.draft
        conversationEntity.atTo = conversation.atTo
        conversationEntity.draft = conversation.draft
        conversationEntity.unReadCount = conversation.unReadCount
        conversationEntity.noDisturbing = conversation.noDisturbing
        conversationEntity.top = conversation.top
        conversationEntity.deleted = conversation.deleted
        conversationEntity.timestamp = conversation.timestamp
        conversationEntity.deleteTime = conversation.deleteTime
        conversationEntity.timeIndicator = conversation.timeIndicator
        conversationEntity.topTime = conversation.topTime
        conversationEntity.isNew = conversation.isNew
        conversationEntity.background = conversation.background!!
        conversationEntity.mentionedCount = conversation.mentionedCount!!
        return conversationEntity
    }

    fun toConversation(conversationEntity: ConversationEntity?): Conversation? {
        if (conversationEntity == null) {
            return null
        }
        var conversation = Conversation()
        conversation.conversationId = conversationEntity.conversationId
        conversation.ownerId = conversationEntity.ownerId
        conversation.conversationType = conversationEntity.conversationType
        conversation.targetId = conversationEntity.targetId
        conversation.icon = conversationEntity.icon
        conversation.targetName = conversationEntity.targetName
        var message = getLatestMessage(conversationEntity.conversationId)
        conversation.lastMessage = message
        conversation.draft = conversationEntity.draft
        conversation.atTo = conversationEntity.atTo
        conversation.draft = conversationEntity.draft
        conversation.unReadCount = conversationEntity.unReadCount
        conversation.noDisturbing = conversationEntity.noDisturbing
        conversation.top = conversationEntity.top
        conversation.deleted = conversationEntity.deleted
        conversation.timestamp = conversationEntity.timestamp
        conversation.deleteTime = conversationEntity.deleteTime
        conversation.timeIndicator = conversationEntity.timeIndicator
        conversation.topTime = conversationEntity.topTime
        conversation.isNew = conversationEntity.isNew
        conversation.background = conversationEntity.background
        conversation.mentionedCount = conversationEntity.mentionedCount
        return conversation
    }


    fun getLatestMessage(conversationId: String): Message? {
        var messageEntity = IMDatabaseRepository.instance.getLatestMessageByConversationId(conversationId, UserInfoCache.getUserId())
        if (messageEntity != null) {
            return MessageConvertUtil.instance.convertToMessage(messageEntity)
        }
        return null
    }


    fun getLastMessage(context: Context, message: Message?): String? {
        if (message == null) {
            return ""
        }
        val messageContent = message.messageContent ?: return ""
        if (messageContent is TextMessage) {
            val msg: TextMessage = messageContent as TextMessage
            return msg.content
        }
        if (messageContent is ImageMessage) {
            return context.getString(R.string.qx_target_name_image)
        }
        if (messageContent is AudioMessage) {
            return context.getString(R.string.qx_target_name_voice)
        }
        if (messageContent is VideoMessage) {
            return context.getString(R.string.qx_target_name_video)
        }
        if (messageContent is ImageTextMessage) {
            val msg: ImageTextMessage = messageContent as ImageTextMessage
            val format = context.getString(R.string.qx_target_name_image_text)
            return java.lang.String.format(format, msg.content)
        }
        if (messageContent is GeoMessage) {
            val msg: GeoMessage = messageContent as GeoMessage
            val format = context.getString(R.string.qx_target_name_geo)
            return java.lang.String.format(format, msg.getTitle())
        }
        if (messageContent is CallMessage) {
            val msg: CallMessage = messageContent as CallMessage
            return if (msg.callType === CallMessage.CallType.CALL_TYPE_VIDEO) {
                context.getString(R.string.qx_target_name_call_video)
            } else {
                context.getString(R.string.qx_target_name_call_audio)
            }
        }
        if (messageContent is FileMessage) {
            val msg: FileMessage = messageContent as FileMessage
            val format = context.getString(R.string.qx_target_name_file)
            return java.lang.String.format(format, msg.fileName)
        }
        if (messageContent is NoticeMessage) {
            val msg: NoticeMessage = messageContent as NoticeMessage
            val format = context.getString(R.string.qx_target_name_notice)
            return java.lang.String.format(
                format,
                NoticeUtil.getNoticeContent(message.targetId, msg, context)
            )
        }
        if (messageContent is ReplyMessage) {
            val msg: Message = (messageContent as ReplyMessage).answer
            return getLastMessage(context, msg)
        }
        return if (messageContent is RecordMessage) {
            context.getString(R.string.qx_target_name_record)
        } else ""
    }
}