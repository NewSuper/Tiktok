package com.aitd.module_chat.lib.handler;

import com.aitd.module_chat.lib.jobqueue.JobManagerUtil;
import com.aitd.module_chat.listener.ConnectionStatusListener;
import com.aitd.module_chat.netty.NettyConnectionState;
import com.aitd.module_chat.netty.NettyConnectionStateManager;
import com.aitd.module_chat.netty.S2CRecMessage;
import com.aitd.module_chat.pojo.UserInfoCache;
import com.aitd.module_chat.utils.EventBusUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.ChannelHandlerContext;

public class KickHandler extends BaseCmdHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException {
        JobManagerUtil.getInstance().logout();
        UserInfoCache.INSTANCE.setUserId("");
        UserInfoCache.INSTANCE.setToken("");
        //被踢下线，设置状态为STATE_KICKED
        NettyConnectionStateManager.getInstance().setState(NettyConnectionState.STATE_KICKED);
        //回调给model层通知sdk已被踢下线
        EventBusUtil.post(ConnectionStatusListener.Status.KICKED);
    }
}
