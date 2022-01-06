package com.aitd.module_chat.push.vivo;

import android.content.Context;
import android.util.Log;

import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushNotificationMessage;
import com.aitd.module_chat.push.PushType;
import com.aitd.module_chat.push.PushUtils;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushMessageReceiver extends OpenClientPushMessageReceiver {
    private static final String TAG = VivoPushMessageReceiver.class.getSimpleName();

    public VivoPushMessageReceiver() {
    }

    public void onNotificationMessageClicked(Context context, UPSNotificationMessage message) {
        Log.v(TAG, "onNotificationMessageClicked is called. " + message.getSkipContent());
        PushNotificationMessage pushNotificationMessage = PushUtils.transformToPushMessage(message.getSkipContent());
        if (pushNotificationMessage != null) {
            PushManager.getInstance().onNotificationMessageClicked(context, PushType.VIVO, pushNotificationMessage);
        }

    }

    public void onReceiveRegId(Context context, String token) {
        Log.d(TAG, "Vivo onReceiveRegId:" + token);
        PushManager.getInstance().onReceiveToken(context, PushType.VIVO, token);
    }
}
