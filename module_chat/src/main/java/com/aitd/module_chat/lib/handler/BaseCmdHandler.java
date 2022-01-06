package com.aitd.module_chat.lib.handler;

import com.aitd.module_chat.netty.S2CRecMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.ChannelHandlerContext;

public abstract class BaseCmdHandler {
    public abstract void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException;
}
