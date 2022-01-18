package com.aitd.module_chat.lib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class QXKitReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            it.action?.let {
                AudioPlayManager.getInstance().stopPlay()//电话响铃，关闭语音播放
            }
        }
    }
}