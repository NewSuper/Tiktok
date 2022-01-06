package com.aitd.module_chat.lib.handler

import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.pojo.event.BaseEvent
import com.aitd.module_chat.utils.EventBusUtil
import com.qx.it.protos.C2SChatroomProperty
import io.netty.channel.ChannelHandlerContext

/**
 * 聊天室属性获取
 */
class ChatRoomAttributeHandler : BaseCmdHandler() {
    override fun handle(ctx: ChannelHandlerContext?, recMessage: S2CRecMessage?) {
        var getPropertyResult = C2SChatroomProperty.GetPropertyResult.parseFrom(recMessage!!.contents)

        var data=  HashMap<String,String>();
        data.put("propName",getPropertyResult.propName)
        data.put("propValue",getPropertyResult.propValue)
        EventBusUtil.postUi(data, BaseEvent.EventName.EVENT_NAME_CHATROOM_ATTRIBUTE);
    }
}