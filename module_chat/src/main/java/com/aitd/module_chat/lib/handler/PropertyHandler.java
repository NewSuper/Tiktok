package com.aitd.module_chat.lib.handler;


import com.aitd.library_common.utils.GlobalContextManager;
import com.aitd.module_chat.RTCServerConfig;
import com.aitd.module_chat.netty.S2CRecMessage;
import com.aitd.module_chat.pojo.event.RTCConfigEvent;
import com.aitd.module_chat.utils.SharePreferencesUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qx.it.protos.S2CProperty;

import org.greenrobot.eventbus.EventBus;

import io.netty.channel.ChannelHandlerContext;

public class PropertyHandler extends BaseCmdHandler{
    @Override
    public void handle(ChannelHandlerContext ctx, S2CRecMessage recMessage) throws InvalidProtocolBufferException {
        S2CProperty.Property propertyData = S2CProperty.Property.parseFrom(recMessage.getContents());
        //缓存特殊字符
        SharePreferencesUtil.Companion.getInstance(GlobalContextManager.getInstance().getContext()).saveSpecialCharacters(propertyData.getSpecialCharacters());
        // ice servce,设备硬编码白名单
        RTCServerConfig rtcConfig = new RTCServerConfig();
        rtcConfig.setIceServers(propertyData.getIceServicesList());
        rtcConfig.setWhiteDeveice(propertyData.getWebrtcWhitelist());
        EventBus.getDefault().post(new RTCConfigEvent(rtcConfig));
    }
}
