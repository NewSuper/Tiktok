package com.aitd.module_chat.push.hms;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.aitd.module_chat.push.IPush;
import com.aitd.module_chat.push.PushConfig;
import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushType;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;


public class HWPush implements IPush {

    private static final String TAG = HWPush.class.getSimpleName();

    @Override
    public void register(Context context, PushConfig pushConfig) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            Log.e(TAG, "Huawei getMainLooper != ");
            this.action(context);
        } else {
            (new Thread(new Runnable() {
                public void run() {
                    HWPush.this.action(context);
                }
            })).start();
        }
    }

    public void action(Context context) {
        try {
            Log.e(TAG, "Huawei action>>>");
            String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
            Log.e(TAG, "Huawei action appId:"+appId);
            String pushtoken = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
            Log.e(TAG, "Huawei action appId:"+appId+",pushtoken:"+pushtoken);
            if (!TextUtils.isEmpty(pushtoken)) {
                PushManager.getInstance().onReceiveToken(context, PushType.HUAWEI, pushtoken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
