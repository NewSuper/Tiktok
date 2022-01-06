package com.aitd.module_chat.lib.handler

import android.util.Log
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.S2CSpecialOperation
import io.netty.channel.ChannelHandlerContext


class ConversationNoDisturbingHandler : BaseCmdHandler() {
    private val TAG = "ConversationNoDisturbin"
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var specialOperation = S2CSpecialOperation.SpecialOperation.parseFrom(recMessage!!.contents)

        Log.i(TAG, "收到免打扰消息，sendType：" + specialOperation.sendType + " userId=" + specialOperation
            .userId + " targetId=" + specialOperation.targetId + " type=" + specialOperation.type)
        var isNoDisturbing = if (specialOperation.type == "set") {
            1
        } else {
            0
        }

        var conversation = IMDatabaseRepository.instance.getConversation(specialOperation.sendType, specialOperation.targetId)
        if (conversation != null) {
            //更新免打扰状态
            IMDatabaseRepository.instance.updateConversationNoDisturbing(specialOperation.sendType, specialOperation.targetId, isNoDisturbing)
            //通知UI
            EventBusUtil.postConversationUpdate(arrayListOf(conversation))
        }
    }
}