package com.aitd.module_chat.push;

import android.content.Context;
import android.content.SharedPreferences;

public class PushSharedPreferencesUtils {

    private static final String TAG = "SharedPreferencesUtils";
    private static Boolean result = null;

    public PushSharedPreferencesUtils() {
    }

    public static SharedPreferences get(final Context context, final String name, int mode) {
        if (context == null) {
            return null;
        } else {
            return context.getSharedPreferences(name, mode);
        }
    }

}
