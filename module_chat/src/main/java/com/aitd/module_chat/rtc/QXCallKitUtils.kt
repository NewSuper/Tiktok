package com.aitd.module_chat.rtc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.app.AppOpsManagerCompat

object QXCallKitUtils  {

    /** 拨打true or 接听false */
    var isDial = true
    var shouldShowFloat = false
    /** 是否已经建立通话连接 默认没有，为了修改接听之后将情景模式切换成震动 在通话界面一直震动的问题  */
    var callConnected = false
    /** 当前 免提 是否打开的状态 true：打开中  */
    var speakerphoneState = false

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return !(networkInfo == null || !networkInfo.isConnected || !networkInfo.isAvailable)
    }

    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (permissions == null || permissions.size == 0) {
            return true
        }
        for (permission in permissions) {
            if (!hasPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        val opStr = AppOpsManagerCompat.permissionToOp(permission) ?: return true
        return (context.checkCallingOrSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun getCallpermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}