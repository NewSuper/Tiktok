package com.aitd.module_chat.rtc

import android.Manifest
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.rtc.listener.IQXCallListener
import com.aitd.module_chat.utils.PermissionCheckUtil
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.qlog.QLog

/**
 * 音视频通话基类
 * 初始化相关配置以及回调
 */
open abstract class BaseCallActivity : BaseActivity(), IQXCallListener {

    private val TAG = "BaseCallActivity"
    protected var isOpenStatusBar = true
    companion object {
        const val REQUEST_CODE_PERMISSIONS = 100

        val VIDEO_CALL_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA
        )
        val AUDIO_CALL_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
    }
    private var shouldRestoreFloat = false

    // 是否是请求开启悬浮窗权限的过程中
    private var checkingOverlaysPermission = false

    protected var isCalling = false

    open fun setShouldShowFloat(ssf: Boolean) {
        QXCallKitUtils.shouldShowFloat = ssf
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isOpenStatusBar = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        super.onCreate(savedInstanceState)
        QXCallProxy.getInstance.callListener = this
        shouldRestoreFloat = true
        QXCallKitUtils.shouldShowFloat = false

    }

    override fun onStart() {
        super.onStart()
        if (shouldRestoreFloat) {
            val bundle = intent.getBundleExtra("floatbox")
            if (shouldRestoreFloat && bundle != null) {
                onRestoreFloatBox(bundle)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (shouldRestoreFloat) {
            QXCallFloatBoxView.hideFloatBox()
        }
    }

    override fun onStop() {
        super.onStop()
        if (QXCallKitUtils.shouldShowFloat && !checkingOverlaysPermission) {
            val bundle = Bundle()
            val action = onSaveFloatBoxState(bundle)
            if (FloatPermissionManager.isRequestFloatPermission(this)) {
                checkingOverlaysPermission = false
                if (!action.isNullOrEmpty()) {
                    bundle.putString("action", action)
                    QXCallFloatBoxView.showFB(this, bundle)
                    if (!this.isFinishing) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask()
                        } else {
                            finish()
                        }
                    }
                }
            } else {
                checkingOverlaysPermission = true
                FloatPermissionManager.requestFloatPermission(this)
            }
        }

    }


    override fun onCallOutgoing(view: SurfaceView) {
        QXCallKitUtils.shouldShowFloat = true
        QXCallKitUtils.isDial = true
    }

    override fun onCallConnecting() {

    }

    override fun onCallConnected(view: SurfaceView) {
        QXCallKitUtils.shouldShowFloat = true
        QXCallKitUtils.isDial = false
        QXCallKitUtils.callConnected = true
    }

    override fun onCallDisconnected(callState: QXCallState) {
        QXCallProxy.getInstance.callListener = null
//        QXCallClient(this).qxCallListener = null
        QXCallKitUtils.shouldShowFloat = false
        QXCallKitUtils.callConnected = false
        if (callState == QXCallState.ERROR_NET) {
            ToastUtil.toast(this, R.string.qc_voip_network_error)
        } else if (callState == QXCallState.MESSAGE_OBJECT_BUSING) {
            ToastUtil.toast(this, R.string.qx_voip_busy)
        } else if (callState == QXCallState.NOT_FRIEND) {
            ToastUtil.toast(this, R.string.qx_voip_not_friend)
        } else if (callState == QXCallState.ERROR_PARAM) {
            ToastUtil.toast(this, R.string.qx_voip_error_param)
        } else if (callState == QXCallState.TIME_OUT) {

        }
    }

    override fun onRemoteUserRinging(userId: String) {
    }

    override fun onRemoteUserJoined(viewType: Int, view: SurfaceView) {
    }

    override fun onRemoteUserInvited(view: SurfaceView) {
    }

    override fun mediaTypeChange() {

    }

    open fun onSaveFloatBoxState(bundle: Bundle): String? {
        return null
    }

    /** onStart时恢复浮窗 *  */
    open fun onRestoreFloatBox(bundle: Bundle) {
    }

    protected fun minimizeClick() {
        val permission = FloatPermissionManager.isRequestFloatPermission(this)
        if (permission) {
            checkingOverlaysPermission = false
            finish()
        } else {
            checkingOverlaysPermission = true
            FloatPermissionManager.requestFloatPermission(this)
        }
    }

    @TargetApi(23)
    open fun requestCallPermissions(requestCode: Int): Boolean {
        var permissions = QXCallKitUtils.getCallpermissions()
        var result = false
        if (permissions != null) {
            val granted: Boolean = QXCallKitUtils.checkPermissions(this, permissions)
            if (granted) {
                result = true
            } else {
                return PermissionCheckUtil.requestPermissions(this, permissions, requestCode)
            }
        }
        return result
    }


    /**
     * 禁止返回关闭界面
     */
    override fun onBackPressed() {
//        super.onBackPressed()
    }

}