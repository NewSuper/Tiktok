package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.lib.db.IMDatabaseRepository.Companion.instance
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.S2CMessageRead
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 消息已读回执
 */
class MessageReadReceiptHandler : BaseCmdHandler() {
    private val STATE_SUCCESS = 1 //1-发送成功；
    private val STATE_RECEIVED = 2 //2-消息送达；
    private val STATE_READ = 3 //3-消息已读；
    private val STATE_FAILED = 4 //4-发送失败；

    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        var readState = S2CMessageRead.MessageRead.parseFrom(recMessage.contents)
        //更新数据库数据
        GlobalScope.launch {
            instance.updateAllMessageReadState(readState.sendType, readState.targetId)
            //通知UI
            EventBusUtil.postMessageRead()
        }
    }

}