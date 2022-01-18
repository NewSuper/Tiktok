package com.aitd.module_chat.lib

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.text.TextUtils
import com.aitd.library_common.utils.GlobalContextManager

class QXIMService:Service(),Thread.UncaughtExceptionHandler {

    private var defaultExceptionHandler :Thread.UncaughtExceptionHandler? = null
    override fun onBind(intent: Intent): IBinder? {
        val appKey = intent.getStringExtra("appKey");
        return LibHandlerStub(this,appKey!!)
    }

    override fun onCreate() {
        super.onCreate()
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        GlobalContextManager.instance.cacheApplicationContext(this)
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        var reason = e.toString()
        if (TextUtils.isEmpty(reason) && reason.contains(":")){
            reason = reason.substring(0,reason.indexOf(":"))
        }
        this.defaultExceptionHandler?.uncaughtException(t,e)
    }

    override fun onDestroy() {
        Process.killProcess(Process.myPid())
    }
}