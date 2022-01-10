package com.aitd.module_chat.rtc.listener

import android.view.SurfaceView
import com.aitd.module_chat.rtc.QXCallState

/**
 * rtc层渲染好的surfaceview 回给ui层添加到对应视图
 */
interface IQXCallListener {

    fun onCallOutgoing(localView: SurfaceView)

    /**
     * 双方正在进行连接
     */
    fun onCallConnecting()

    /**
     * 双方都已经连接上
     */
    fun onCallConnected(remoteView: SurfaceView)

    fun onCallDisconnected(callState: QXCallState)

    fun onRemoteUserRinging(userId: String)

    fun onRemoteUserJoined(viewType:Int, remoteView: SurfaceView)

    fun onRemoteUserInvited(remoteView: SurfaceView)

    fun mediaTypeChange()
}