package com.aitd.module_chat.lib.handler;

import com.aitd.module_chat.netty.S2CRecMessage;
import com.aitd.module_chat.utils.qlog.QLog;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.ChannelHandlerContext;

public class DefaultHandler extends BaseCmdHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException {
        QLog.i("DefaultHandler", "未知指令：cmd=" + recMessage.getCmd());
    }
}
