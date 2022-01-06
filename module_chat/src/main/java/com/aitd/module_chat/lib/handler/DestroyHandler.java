package com.aitd.module_chat.lib.handler;

import com.aitd.module_chat.lib.jobqueue.JobManagerUtil;
import com.aitd.module_chat.listener.ConnectionStatusListener;
import com.aitd.module_chat.netty.NettyConnectionState;
import com.aitd.module_chat.netty.NettyConnectionStateManager;
import com.aitd.module_chat.netty.S2CRecMessage;
import com.aitd.module_chat.pojo.UserInfoCache;
import com.aitd.module_chat.utils.EventBusUtil;
import com.aitd.module_chat.utils.qlog.QLog;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.ChannelHandlerContext;

public class DestroyHandler extends BaseCmdHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException {
        QLog.i("DestroyHandler", "注销成功");
        JobManagerUtil.getInstance().logout();
        UserInfoCache.INSTANCE.setUserId("");
        UserInfoCache.INSTANCE.setToken("");
        //注销成功，设置状态为STATE_DISCONNECTED
        NettyConnectionStateManager.getInstance().setState(NettyConnectionState.STATE_DISCONNECTED);
        //回调给model层通知sdk关闭连接成功
        EventBusUtil.post(ConnectionStatusListener.Status.LOGOUT);
        //关闭连接
        ctx.close();
        QLog.i("DestroyHandler", "关闭长连接");
    }
}
