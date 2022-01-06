package com.aitd.module_chat.lib.menu;

import android.content.Context;

import com.aitd.module_chat.Message;

public interface QXMenuAction {
    void onAction(Context context, Message message);
}

