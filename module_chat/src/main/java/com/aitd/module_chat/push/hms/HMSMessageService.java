package com.aitd.module_chat.push.hms;

import android.util.Log;

import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushType;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class HMSMessageService extends HmsMessageService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("HMSMessageService", "onMessageReceived:" + remoteMessage.toString());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("HMSMessageService", "onNewToken:" + s);
        PushManager.getInstance().onReceiveToken(this, PushType.HUAWEI, s);
    }

}
