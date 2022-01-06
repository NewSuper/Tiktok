package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.db.IMDatabaseRepository.Companion.instance
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.S2CMessageStatus
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 消息已送达回执
 */
class MessageReceivedReceiptHandler : BaseCmdHandler() {
    private val STATE_SUCCESS = 1 //1-发送成功；
    private val STATE_RECEIVED = 2 //2-消息送达；
    private val STATE_READ = 3 //3-消息已读；
    private val STATE_FAILED = 4 //4-发送失败；

    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        val status = S2CMessageStatus.MessageStatus.parseFrom(recMessage.contents)
        QLog.i("MessageReceivedReceiptHandler",
            "state=" + status.state + " from=" + status.from + " to=" + status.to + " " + "sendType=" + status.sendType
        )

        //使用Android协程实现异步
        GlobalScope.launch {
            //更新到数据库
            for( messageId in status.messageIdsList) {
                if (instance.updateMessageState(messageId, status.state, "") > 0) {
                    //获取消息数据，并使用EventBus发送
                    EventBusUtil.postMessageStateChanged(instance.getMessageById(messageId))
                }
            }
        }
    }
}