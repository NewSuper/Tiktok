package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.ConversationModel
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.ConversationEntity
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.netty.S2CSndMessage
import com.aitd.module_chat.netty.SystemCmd
import com.aitd.module_chat.pojo.InsertMessageResult
import com.aitd.module_chat.pojo.UserInfoCache
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.C2SMessageContext
import io.netty.channel.ChannelHandlerContext


/**
 * 消息连续性计算类
 */
abstract class MessageContinuityCalcHandler : BaseCmdHandler() {

    var timeIndicator = -1L
    var topTime = -1L

    //消息列表块，包含n个操作的消息列表
    private var messageBlock: List<List<MessageEntity>> = arrayListOf()

    //当前操作的消息列表
    private lateinit var messages: List<MessageEntity>
    private lateinit var sendType: String
    private lateinit var from: String
    private lateinit var to: String

    fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?, block : List<MessageEntity>) {

        messages = block
        when (recMessage?.cmd) {
            //当收到离线消息、新消息（单聊、群聊、聊天室、系统消息）时，自动回复已收到cmd
            SystemCmd.C2S_SEND_P2P_MESSAGE, SystemCmd.C2S_SEND_GROUP_MESSAGE, SystemCmd.C2S_SEND_CHATROOM_MESSAGE,
            SystemCmd.C2S_SEND_SYSTEM_MESSAGE, SystemCmd.S2C_MESSAGE_LIST_OFFLINE -> {
                sendReceivedConfirm(messages, ctx)
            }
        }

        //设置sendType、from、to
        if (messageBlock.isNotEmpty()) {
            setProperties(messages.first().sendType, messages.first().to)
        } else {
            setProperties(recMessage!!)
        }
        //计算数据的连续性
        calcData()


    }

    private fun calcData() {
        //获取会话
        var conversation = ConversationModel.instance.generateConversation(messages.first())
        //持久化到数据库
        saveToDb(conversation)
    }

    private fun saveToDb(conversation: ConversationEntity) {
        var messageEntity: MessageEntity? = null
        messageEntity = if (messages.isNotEmpty()) {
            messages.first()
        } else {
            //如果为空，则说明返回来的数据库为空，尝试从本地获取
            IMDatabaseRepository.instance.getLatestMessage(sendType, from, to, UserInfoCache.getUserId())
        }
        if (messageEntity == null) {
            //说明本地和网络上都没有数据，则不用往下处理了
            return
        }

        //插入或更新会话
        if (IMDatabaseRepository.instance.insertConversation(conversation) > 0) {
            //处理不信任时间区域
            IMDatabaseRepository.instance.calcUnTrustTime(conversation.conversationType,
                conversation.targetId, messages.last().timestamp, messages.first().timestamp)
            //插入消息
            var result = insertMessage(conversation, messages)

            //更新会话信息：最后一条消息、时间
            IMDatabaseRepository.instance.refreshConversationInfo(
                messageEntity.sendType, messageEntity.from, messageEntity.to
            )
            if (conversation.isNew) {
                ConversationModel.instance.generateConversation(messageEntity)
            }
            notifyUiUpdate(result)
            var conversation = IMDatabaseRepository.instance.getConversationById(conversation.conversationId)
            if (conversation != null) {
                EventBusUtil.postConversationUpdate(arrayListOf(conversation))
            }
        }

    }

    private fun insertMessage(conversationEntity: ConversationEntity, list: List<MessageEntity>): InsertMessageResult {

        var result = InsertMessageResult()
        //TODO 此处待优化，否则循环查询数据库很耗时
        for (msg in list) {
            //加此条件过滤消息时间<=deleteTime的消息，满足此条件的消息不插入数据库
            if (isNeedCheckDeleteTime() && msg.timestamp <= conversationEntity.deleteTime) {
                continue
            }
            msg.conversationId = conversationEntity.conversationId
            var r = IMDatabaseRepository.instance.insertMessage(msg)
            result.newMessageCount += r.newMessageCount
            result.messages.addAll(r.messages)
        }
        return result
    }


    open fun sendReceivedConfirm(messages: List<MessageEntity>,
                                 ctx: ChannelHandlerContext?) {

        var ids = arrayListOf<String>()
        for (message in messages) {
            ids.add(message.messageId)
        }

        var body = C2SMessageContext.MessageContext.newBuilder().addAllMessageIds(ids).setFrom(messages[0].from)
            .setTo(messages[0].to).setSendType(messages[0].sendType).build()
        var msg = S2CSndMessage()
        msg.cmd = SystemCmd.C2S_RECV_CONFIRM
        msg.body = body
        ctx!!.channel().writeAndFlush(msg)
    }

    open fun setProperties(sendType: String, to: String) {
        this.sendType = sendType
        from = UserInfoCache.getUserId()
        this.to = to
    }

    open fun isPreSetMessage(messageType: String) {

    }

    abstract fun notifyUiUpdate(result: InsertMessageResult)
    abstract fun getMessages(recMessage: S2CRecMessage): List<Any>
    abstract fun getLatestMessage(sendType: String, from: String, to: String): MessageEntity?
    abstract fun isNeedCheckDeleteTime(): Boolean

    open fun setProperties(recMessage: S2CRecMessage) {
    }
}