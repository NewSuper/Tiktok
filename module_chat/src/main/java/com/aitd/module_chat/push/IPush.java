package com.aitd.module_chat.push;

import android.content.Context;

public interface IPush {
    void register(Context context, PushConfig pushConfig);
}
