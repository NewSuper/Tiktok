package com.aitd.module_chat.push.meizu;

import android.content.Context;
import android.text.TextUtils;
import com.meizu.cloud.pushsdk.PushManager;
import com.aitd.module_chat.push.IPush;
import com.aitd.module_chat.push.PushConfig;
import com.aitd.module_chat.push.PushType;

public class MZPush implements IPush {
    @Override
    public void register(Context context, PushConfig pushConfig) {
        if (!TextUtils.isEmpty(pushConfig.getMzAppId()) && !TextUtils.isEmpty(pushConfig.getMzAppKey())) {
            String pushId =PushManager.getPushId(context);
            if (TextUtils.isEmpty(pushId)) {
                PushManager.register(context, pushConfig.getMzAppId(), pushConfig.getMzAppKey());
            } else {
                com.aitd.module_chat.push.PushManager.getInstance().onReceiveToken(context, PushType.MEIZU, pushId);
            }
        } else {
            com.aitd.module_chat.push.PushManager.getInstance().onErrorResponse(context, PushType.MEIZU, "request_token", 1);
        }
    }
}
