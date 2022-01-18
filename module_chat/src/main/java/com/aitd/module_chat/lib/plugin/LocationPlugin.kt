package com.aitd.module_chat.lib.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.lib.panel.QXExtension
import com.aitd.module_chat.listener.IPluginCallback
import com.aitd.module_chat.ui.chat.LocationActivity
import com.aitd.module_chat.utils.PermissionCheckUtil
import com.aitd.module_chat.utils.file.LibStorageUtils


class LocationPlugin : IPluginModule, IPluginCallback {
    override fun obtainDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.vector_location)
    }

    override fun obtainTitle(context: Context): String {
        return context.resources.getString(R.string.qx_chat_add_panel_location)
    }

    override fun onClick(context: Activity, extension: QXExtension) {
        val permissions: Array<String> = if (LibStorageUtils.isBuildAndTargetForQ(context)) {
            arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION", "android.permission.ACCESS_NETWORK_STATE")
        } else {
            arrayOf("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_NETWORK_STATE")
        }
        if (PermissionCheckUtil.checkPermissions(context, permissions)) {
            startLocation(context,extension)
        } else {
            extension.requestPermissionForPluginResult(permissions, 255, this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun onRequestPermissionResult(activity: Activity, extension: QXExtension, requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean {
        if (PermissionCheckUtil.checkPermissions(activity, permissions)) {
            this.startLocation(activity, extension)
        } else{
            extension.showRequestPermissionFailedAlter(PermissionCheckUtil.getNotGrantedPermissionMsg(activity, permissions, grantResults))
        }
        return true
    }

    private fun startLocation(context: Activity, extension: QXExtension) {
        val intent = Intent(context, LocationActivity::class.java)
        intent.putExtra("targetId", extension.targetId)
        intent.putExtra("conversationType", extension.conversationType)
        extension.startActivityForPluginResult(intent, 23,this)
    }
}