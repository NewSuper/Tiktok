package com.aitd.module_chat.listener

import android.app.Activity
import com.aitd.module_chat.lib.panel.QXExtension

interface IPluginCallback {

    val REQUEST_CODE_PERMISSION_PLUGIN: Int get() = 255

    fun onRequestPermissionResult(activity: Activity, extension: QXExtension,
                                  requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean
}