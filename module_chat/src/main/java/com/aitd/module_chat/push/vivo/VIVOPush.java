package com.aitd.module_chat.push.vivo;

import android.content.Context;
import android.util.Log;

import com.aitd.module_chat.push.IPush;
import com.aitd.module_chat.push.PushConfig;
import com.aitd.module_chat.push.PushManager;
import com.aitd.module_chat.push.PushType;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;


public class VIVOPush implements IPush {
    private final String TAG = VIVOPush.class.getSimpleName();
    public VIVOPush() {
    }
    @Override
    public void register(Context context, PushConfig pushConfig) {
        PushClient.getInstance(context.getApplicationContext()).initialize();
        PushClient.getInstance(context.getApplicationContext()).turnOnPush(new IPushActionListener() {
            public void onStateChanged(int i) {
                Log.d(VIVOPush.this.TAG, "Vivo push onStateChanged:" + i);
                if (i == 0) {
                    PushManager.getInstance().onReceiveToken(context, PushType.VIVO, PushClient.getInstance(context.getApplicationContext()).getRegId());
                } else if (i == 101) {
                    PushManager.getInstance().onErrorResponse(context, PushType.VIVO, "request_token", 101);
                } else {
                    PushManager.getInstance().onErrorResponse(context, PushType.VIVO, "request_token", (long)i);
                }

            }
        });
    }
}
