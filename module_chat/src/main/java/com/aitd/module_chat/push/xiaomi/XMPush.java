package com.aitd.module_chat.push.xiaomi;

import android.content.Context;
import android.text.TextUtils;

import com.aitd.module_chat.push.IPush;
import com.aitd.module_chat.push.PushConfig;
import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushType;
import com.xiaomi.mipush.sdk.MiPushClient;


public class XMPush implements IPush {
    @Override
    public void register(Context context, PushConfig pushConfig) {
        if (!TextUtils.isEmpty(pushConfig.getMiAppId()) && !TextUtils.isEmpty(pushConfig.getMiAppKey())) {
            MiPushClient.registerPush(context, pushConfig.getMiAppId(), pushConfig.getMiAppKey());
        } else {
            PushManager.getInstance().onErrorResponse(context, PushType.XIAOMI, "request_token", 1);
        }
    }
}
