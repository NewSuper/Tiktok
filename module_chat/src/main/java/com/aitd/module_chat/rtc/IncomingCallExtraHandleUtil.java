package com.aitd.module_chat.rtc;

import android.content.Context;

import com.aitd.module_chat.lib.QXContext;
import com.aitd.module_chat.push.QXNotificationInterface;

/**
 * 适配 Android 10 以上不允许后台启动 Activity 的工具类
 */
public class IncomingCallExtraHandleUtil {

    public final static int VOIP_NOTIFICATION_ID = 3000; //VoIP类型的通知消息。
    public final static int VOIP_REQUEST_CODE = 30001;

    private static QXCallSession cachedCallSession = null;
    private static boolean checkPermissions = false;

    public static void removeNotification(Context context) {
        QXNotificationInterface.removeNotification(context, VOIP_NOTIFICATION_ID);
    }

    public static QXCallSession getCallSession() {
        return cachedCallSession;
    }

    public static void cacheCallSession(QXCallSession callSession, boolean permissions) {
        cachedCallSession = callSession;
        checkPermissions = permissions;
    }

    public static boolean isCheckPermissions() {
        return checkPermissions;
    }

    public static void clear() {
        cachedCallSession = null;
        checkPermissions = false;
        removeNotification(QXContext.getInstance().getContext());
    }

    public static boolean needNotify() {
        return cachedCallSession != null && !QXCallFloatBoxView.isCallFloatBoxShown();
    }
}
