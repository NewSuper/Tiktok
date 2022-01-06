package com.aitd.module_chat.lib.handler

import com.aitd.library_common.utils.SystemUtil
import com.aitd.module_chat.netty.C2SRouter
import com.aitd.module_chat.netty.S2CRecMessage
import com.aitd.module_chat.netty.S2CSndMessage
import com.aitd.module_chat.netty.SystemCmd
import com.aitd.module_chat.pojo.UserInfoCache
import com.google.protobuf.InvalidProtocolBufferException
import com.qx.it.protos.C2SAuth
import com.qx.it.protos.C2SKey
import io.netty.channel.ChannelHandlerContext

class AesKeyHandler : BaseCmdHandler() {
    @Throws(InvalidProtocolBufferException::class)
    override fun handle(ctx: ChannelHandlerContext, recMessage: S2CRecMessage) {
        val key = C2SKey.KeyRes.parseFrom(recMessage.contents)
        C2SRouter.aesKey = key.key
        if(key.key.isNotEmpty()) {
            //发起auth认证
            var msg = createAuthMessage()
            ctx.writeAndFlush(msg)
        } else {
            //TODO 做提示处理
        }
    }

    private fun createAuthMessage()  : S2CSndMessage {
        val deviceNo: String = UserInfoCache.getToken() + System.currentTimeMillis()
        var systemVersion = SystemUtil.getSystemVersion()
        var deviceCode = SystemUtil.getDeviceBrand()
        val auth = C2SAuth.Auth.newBuilder()
            .setAppId(UserInfoCache.appKey)
            .setToken(UserInfoCache.getToken()).setDeviceNo(deviceNo).setDeviceType("1").setDeviceCode(
                deviceCode
            ).setSystemVersion(systemVersion).build()
        val message = S2CSndMessage()
        message.cmd = SystemCmd.C2S_AUTH
        message.body = auth
        return message
    }

    companion object {
        var deviceNo: String = ""
    }

}