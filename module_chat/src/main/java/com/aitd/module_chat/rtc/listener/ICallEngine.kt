package com.aitd.module_chat.rtc.listener

import android.content.Context
import android.view.SurfaceView
import com.aitd.module_chat.rtc.QXCallState

interface ICallEngine {
    fun create( roomType:String,listener: IConnectListener)

    fun createRendererView(context: Context,overlay: Boolean): SurfaceView

    fun destory()

    fun setLocalVideo(localVideoView: SurfaceView)

    fun setRemoteVideo(remoteVideoView: SurfaceView)

    fun startPreview(): Int

    fun stopPreview(): Int

    fun switchCamera(): Int

    fun joinRoom(roomId: String)

    fun leaveRoom(state: QXCallState)

    fun muteLocalAudioStream(mute: Boolean)

    fun muteRemoteVideoStream(mute: Boolean)
}