package com.aitd.module_chat.push;

import com.aitd.module_chat.push.google.FCMPush;
import com.aitd.module_chat.push.hms.HWPush;
import com.aitd.module_chat.push.meizu.MZPush;
import com.aitd.module_chat.push.oppo.OPPOPush;
import com.aitd.module_chat.push.vivo.VIVOPush;
import com.aitd.module_chat.push.xiaomi.XMPush;

public class PushFactory {

    private static final String TAG = PushFactory.class.getSimpleName();

    public PushFactory() {
    }

    public static boolean isOnlyDefaultPushOS(PushConfig pushConfig) {
        String os = PushUtils.getDeviceManufacturer();
        return (os.contains("Xiaomi") || os.contains("HUAWEI") || os.contains("Meizu"))  && !pushConfig.getEnabledPushTypes().contains(PushType.GOOGLE_FCM);
    }

    public static IPush getPushCenterByType(PushType pushType) {
        if (pushType.equals(PushType.GOOGLE_FCM)) {
            return new FCMPush();
        } else if (pushType.equals(PushType.HUAWEI)) {
            return new HWPush();
        } else if (pushType.equals(PushType.XIAOMI)) {
            return new XMPush();
        } else if (pushType.equals(PushType.MEIZU)) {
            return new MZPush();
        }  else if (pushType.equals(PushType.VIVO)) {
            return new VIVOPush();
        } else if (pushType.equals(PushType.OPPO)) {
            return new OPPOPush();
        } else {
            return null;
        }
    }
}