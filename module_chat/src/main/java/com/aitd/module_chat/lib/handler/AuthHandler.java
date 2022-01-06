package com.aitd.module_chat.lib.handler;


import com.aitd.module_chat.lib.db.IMDatabaseRepository;
import com.aitd.module_chat.lib.db.entity.ConversationEntity;
import com.aitd.module_chat.lib.db.entity.MessageEntity;
import com.aitd.module_chat.lib.db.entity.TBUnTrustTime;
import com.aitd.module_chat.listener.ConnectionStatusListener;
import com.aitd.module_chat.netty.HeartBeatHolder;
import com.aitd.module_chat.netty.NettyConnectionState;
import com.aitd.module_chat.netty.NettyConnectionStateManager;
import com.aitd.module_chat.netty.S2CRecMessage;
import com.aitd.module_chat.pojo.UserInfoCache;
import com.aitd.module_chat.utils.EventBusUtil;
import com.aitd.module_chat.utils.qlog.QLog;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qx.it.protos.S2CAuth;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;

public class AuthHandler extends BaseCmdHandler {
    @Override
    public void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException {
        S2CAuth.AuthSuccess authSuccess = S2CAuth.AuthSuccess.parseFrom(recMessage.getContents());

        QLog.i("AuthHandler", "auth成功，auth：" + authSuccess.toString());
        //userId缓存到本地
        UserInfoCache.INSTANCE.setUserId(authSuccess.getUserId());
        // IMDatabaseRepository.Companion.getInstance().reCreateDataBase();
        NettyConnectionStateManager.getInstance().setState(NettyConnectionState.STATE_CONNECTED);
        // 并启动心跳保活服务
        HeartBeatHolder.getInstance().startHeartBeatService();
        //回调给model层通知sdk初始化成功
        EventBusUtil.post(ConnectionStatusListener.Status.CONNECTED);
        //处理不信任时间区域
        handleUnTrustTime(authSuccess.getTimestamp());
    }

    /**
     * @param endTime 用户当前登录时间
     */
    private void handleUnTrustTime(long endTime) {
        try {
            List<ConversationEntity> conversations = IMDatabaseRepository.Companion.getInstance().getAllConversation();
            if (conversations != null && conversations.size() > 0) {
                for (ConversationEntity conversationEntity : conversations) {
                    MessageEntity messageEntity = IMDatabaseRepository.Companion.getInstance().getLatestMessageByConversationId(conversationEntity.getConversationId(),
                            conversationEntity.getOwnerId());
                    if (messageEntity != null) {
                        TBUnTrustTime tbUnTrustTime = TBUnTrustTime.obtain(conversationEntity.getOwnerId(), conversationEntity.getConversationType(),
                                conversationEntity.getTargetId(), messageEntity.getTimestamp(), endTime);
                        IMDatabaseRepository.Companion.getInstance().insertUnTrustTime(tbUnTrustTime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
