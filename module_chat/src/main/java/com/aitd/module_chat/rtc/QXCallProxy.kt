package com.aitd.module_chat.rtc

import android.view.SurfaceView
import com.aitd.module_chat.rtc.listener.IQXCallListener

/**
 * UI层和RTC层转发通知
 */
class QXCallProxy private constructor():IQXCallListener{

    var callListener: IQXCallListener? = null

    companion object {
        val getInstance: QXCallProxy by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            QXCallProxy()
        }
    }

    override fun onCallOutgoing(view: SurfaceView) {
        callListener?.onCallOutgoing(view)
    }

    override fun onCallConnecting() {
        callListener?.onCallConnecting()
    }

    override fun onCallConnected(view: SurfaceView) {
        callListener?.onCallConnected(view)
    }

    override fun onCallDisconnected(callState: QXCallState) {
        callListener?.onCallDisconnected(callState)
        IncomingCallExtraHandleUtil.clear()
    }

    override fun onRemoteUserRinging(userId: String) {
        callListener?.onRemoteUserRinging(userId)
    }

    override fun onRemoteUserJoined(viewType: Int, view: SurfaceView) {
        callListener?.onRemoteUserJoined(viewType,view)
    }

    override fun onRemoteUserInvited(view: SurfaceView) {
        callListener?.onRemoteUserInvited(view)
    }

    override fun mediaTypeChange() {
        callListener?.mediaTypeChange()
    }

}