package com.aitd.module_chat.lib.panel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable

interface IPluginModule {

    fun obtainDrawable(context: Context): Drawable

    fun obtainTitle(context: Context): String

    fun onClick(context: Activity, extension: QXExtension)

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}