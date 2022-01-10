package com.aitd.module_chat.rtc

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.utils.PermissionCheckUtil

class QXCallKit {

    companion object {

        /**
         * 一对一 音视频通话
         */
        @JvmStatic
        fun startSignleCall(context: Context, conversionType:String, tragetId:String, mediaType:CallMediaType) {
            if(checkEnvironment(context,mediaType)) {
                val action = if (mediaType ==CallMediaType.CALL_MEDIA_TYPE_AUDIO) {
                    "qx.rtc.intent.action.voip.SINGLEAUDIO"
                } else {
                    "qx.rtc.intent.action.voip.SINGLEVIDEO"
                }
                val intent = Intent(action)
                intent.putExtra("conversionType",conversionType)
                intent.putExtra("targetId", tragetId)
                intent.putExtra("callAction", QXCallAction.ACTION_OUTGOING_CALL.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage(context.packageName)
                context.startActivity(intent)
            }

        }

        @JvmStatic
        fun startMultiCall(context: Context, conversionType:String, tragetId:String, userIds:ArrayList<String>, mediaType:CallMediaType) {
            val action = if (mediaType ==CallMediaType.CALL_MEDIA_TYPE_AUDIO) {
                "qx.rtc.intent.action.voip.MULTIAUDIO"
            } else {
                "qx.rtc.intent.action.voip.MULTIVIDEO"
            }
            val intent = Intent(action)
            intent.putExtra("conversionType",conversionType)
            intent.putExtra("targetId", tragetId)
            intent.putStringArrayListExtra("inviteUserIds", userIds)
            intent.putExtra("callAction", QXCallAction.ACTION_OUTGOING_CALL.name)
            intent.setPackage(context.packageName)
            context.startActivity(intent)
        }

        /**
         * 检查应用音视频授权信息 检查网络连接状态 检查是否在通话中
         *
         * @param context 启动的 activity
         * @param mediaType 启动音视频的媒体类型
         * @return 是否允许启动通话界面
         */
        private fun checkEnvironment(context: Context, mediaType: CallMediaType): Boolean {
            if (context is Activity) {
                val permissions: Array<String> = if (mediaType == CallMediaType.CALL_MEDIA_TYPE_AUDIO) {
                    arrayOf(Manifest.permission.RECORD_AUDIO)
                } else {
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                }
                if (!PermissionCheckUtil.requestPermissions(context, permissions)) {
                    return false
                }
            }
            if (isInVoipCall(context)) {
                return false
            }
            if (QXIMClient.instance.mConnectStatus != QXIMClient.ConnectionStatusListener.Status.CONNECTED) {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.qc_voip_disconnected),
                    Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            return true
        }

        /**
         * 是否在VOIP通话中
         *
         * @param context
         * @return 是否在VOIP通话中
         */
        fun isInVoipCall(context: Context): Boolean {
            var callSession = QXCallClient.getInstance().getCallSession()
            if (callSession != null ) {
                Toast.makeText(
                    context,
                    if (callSession.getMediaType() === QXCallMediaType.AUDIO) context.resources
                        .getString(R.string.qc_voip_audio_start_fail) else context.resources
                        .getString(R.string.qc_voip_video_start_fail),
                    Toast.LENGTH_SHORT)
                    .show()
                return true
            }
            return false
        }
    }


    enum class CallMediaType {
        CALL_MEDIA_TYPE_AUDIO,CALL_MEDIA_TYPE_VIDEO
    }

}