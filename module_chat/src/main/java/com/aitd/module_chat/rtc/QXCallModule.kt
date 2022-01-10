package com.aitd.module_chat.rtc

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.aitd.library_common.utils.SystemUtil
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.lib.plugin.AudioPlugin
import com.aitd.module_chat.lib.plugin.VideoPlugin
import com.aitd.module_chat.rtc.listener.IReceiveCallListener
import com.aitd.module_chat.utils.qlog.QLog

class QXCallModule : IExternalModule {

    companion object {
        private const val TAG = "QXCallModule"

        @JvmStatic
        fun createIntent(context: Context, session: QXCallSession): Intent {
            var action = ""
            action = if (session.conversionType.equals("private", true)) {
                if (session.callType == "1") {
                    "qx.rtc.intent.action.voip.SINGLEAUDIO"
                } else {
                    "qx.rtc.intent.action.voip.SINGLEVIDEO"
                }
            } else {
                if (session.callType == "1") {
                    "qx.rtc.intent.action.voip.MULTIAUDIO"
                } else {
                    "qx.rtc.intent.action.voip.MULTIVIDEO"
                }
            }

            val intent = Intent(action)
            intent.putExtra("conversionType", session.conversionType)
            intent.putExtra("callSession", session)
            intent.putExtra("callAction", QXCallAction.ACTION_INCOME_CALL.name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage(context.packageName)
            return intent
        }


        private fun isAppOnForeground(context: Context): Boolean {
            return if (context == null) {
                false
            } else {
                val activityManager =
                    context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val appProcesses = activityManager.runningAppProcesses
                if (appProcesses == null) {
                    false
                } else {
                    val apkName = context.packageName
                    val iterator: Iterator<*> = appProcesses.iterator()
                    var app: ActivityManager.RunningAppProcessInfo
                    do {
                        if (!iterator.hasNext()) {
                            return false
                        }
                        app = iterator.next() as ActivityManager.RunningAppProcessInfo
                    } while (!TextUtils.equals(
                            apkName,
                            app.processName
                        ) || ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND != app.importance
                    )
                    true
                }
            }
        }
    }

    fun initModule(context: Context) {
        val currentProcess = SystemUtil.getCurrentProcessName(context)
        val mainProcess = context.packageName
        if (!TextUtils.isEmpty(currentProcess) && !TextUtils.isEmpty(mainProcess) && mainProcess == currentProcess) {
            // 来电监听
            val receiveCallListener = object : IReceiveCallListener {
                override fun onReceivedCall(session: QXCallSession) {
                    QLog.d(TAG, "IReceiveCallListener onReceivedCall")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isAppOnForeground(context)) {
                        // 发送通知
                        QLog.d(TAG, "系统API大于29 并且app在后台")
                        val intent = createIntent(context, session)
                        val pendingIntent = PendingIntent.getActivity(
                            context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val notifyManager =
                            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val channelId = IncomingCallExtraHandleUtil.VOIP_NOTIFICATION_ID.toString()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channelName = "12223"
                            val notificationChannel = NotificationChannel(
                                channelId,
                                channelName, NotificationManager.IMPORTANCE_HIGH
                            )
                            notificationChannel.enableLights(true)
                            notificationChannel.setShowBadge(true)
                            notifyManager.createNotificationChannel(notificationChannel)
                        }
                        val notificationBuilder = Notification.Builder(context, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(context.getString(R.string.qx_voip_request))
                            .setContentText(
                                if (session.callType == "1") context.getString(R.string.qx_voip_call_audio) else context.getString(
                                    R.string.qx_voip_call_video
                                )
                            )
                            .setFullScreenIntent(pendingIntent, true).build()

                        notifyManager.notify(
                            IncomingCallExtraHandleUtil.VOIP_NOTIFICATION_ID,
                            notificationBuilder
                        )
                        IncomingCallExtraHandleUtil.cacheCallSession(session, false)
                    } else {
                        context.startActivity(createIntent(context, session))
                    }
                }
            }
            QXCallClient.setReceivedCallListener(receiveCallListener)
            ActivityStartCheckUtils.getInstance().registerActivityLifecycleCallbacks(context)
        }
    }

    override fun onCreate(context: Context) {
        initModule(context)
    }

    override fun onInitialized(token: String) {
    }

    override fun onConnected(token: String, host: String) {
        QXCallClient.getInstance().qxCallListener = QXCallProxy.getInstance
        QXCallClient.getInstance().setUserToken(token)
        QXCallClient.getInstance().setServerHost(host)
    }


    override fun onViewCreated() {
    }

    override fun onDisconnected() {
    }

    override fun getPlugins(conversationType: String): List<IPluginModule> {
        val pluginModuleList = mutableListOf<IPluginModule>()
        pluginModuleList.add(AudioPlugin())
        pluginModuleList.add(VideoPlugin())
        return pluginModuleList
    }

    override fun onClick(context: Context, conversationType: String, target: String, media: Int) {
        QXCallKit.startSignleCall(
            context, conversationType, target, if (media == 0)
                QXCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO else QXCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO
        )
    }
}