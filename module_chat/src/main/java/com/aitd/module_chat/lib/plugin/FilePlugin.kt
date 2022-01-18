package com.aitd.module_chat.lib.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.lib.panel.QXExtension
import com.aitd.module_chat.listener.IPluginCallback
import com.aitd.module_chat.utils.PermissionCheckUtil
import com.aitd.module_chat.utils.file.LibStorageUtils


class FilePlugin : IPluginModule, IPluginCallback {
    override fun obtainDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.vector_file)
    }

    override fun obtainTitle(context: Context): String {
        return context.resources.getString(R.string.qx_chat_add_panel_file)
    }

    override fun onClick(context: Activity, extension: QXExtension) {
        if (LibStorageUtils.isBuildAndTargetForQ(context)) {
            val intent = Intent("android.intent.action.OPEN_DOCUMENT")
            intent.addCategory("android.intent.category.OPENABLE")
            intent.type = "*/*"
            extension.startActivityForPluginResult(intent, 101, this)
        } else {
            val permissions = arrayOf(
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
            )
            if (PermissionCheckUtil.checkPermissions(context, permissions)) {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT  //实现相册多选
                extension.startActivityForPluginResult(intent, 100, this)
            } else {
                extension.requestPermissionForPluginResult(permissions, 255, this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("onActivityResult()", "resultCode:" + resultCode)
    }

    override fun onRequestPermissionResult(
        activity: Activity,
        extension: QXExtension,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        if (PermissionCheckUtil.checkPermissions(activity, permissions)) {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT  //实现相册多选
            extension.startActivityForPluginResult(intent, 100, this)
        } else {
            extension.showRequestPermissionFailedAlter(
                PermissionCheckUtil.getNotGrantedPermissionMsg(
                    activity,
                    permissions,
                    grantResults
                )
            )
        }
        return true
    }
}