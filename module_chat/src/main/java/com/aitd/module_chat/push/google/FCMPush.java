package com.aitd.module_chat.push.google;

import android.content.Context;

import com.aitd.module_chat.push.IPush;
import com.aitd.module_chat.push.PushConfig;
import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushType;
import com.aitd.module_chat.push.PushUtils;
import com.google.firebase.messaging.FirebaseMessaging;


public class FCMPush implements IPush {
    @Override
    public void register(Context context, PushConfig pushConfig) {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        long result = PushUtils.checkPlayServices(context);
        if (result != 0L) {
            PushManager.getInstance().onErrorResponse(context, PushType.GOOGLE_FCM, "checkPlayServices", result);
        }
    }
}
