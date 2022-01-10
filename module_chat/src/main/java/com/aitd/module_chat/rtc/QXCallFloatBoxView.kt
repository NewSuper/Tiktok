package com.aitd.module_chat.rtc

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.rtc.listener.IQXCallListener
import com.aitd.module_chat.utils.qlog.QLog
import java.util.*
import kotlin.math.abs



class QXCallFloatBoxView {

    companion object {
        private val TAG = "QXCallFloatBoxView"
        private var context: Context? = null
        private var mWindowManager: WindowManager? = null
        private var mView: View? = null
        private var timer: Timer? = null
        private var mTime: Long = 0
        private var isShown = false
        private var mBundle: Bundle? = null
        private val mapLastClickTime = hashMapOf<String, Long>()
        private var callTimeTv: TextView? = null
        private var mRemoteVideoContainer: FrameLayout? = null
        private var mediaType:String? = ""

        fun showFB(context: Context, bundle: Bundle) {
            if (QXCallKitUtils.isDial) {
                // 拨打后还未接通时显示浮窗
                showFloatBoxToCall(context, bundle)
            } else {
                // 已经接通后显示浮窗
                showFloatBox(context, bundle)
            }

        }

        /**
         * 已经接通后显示浮窗
         */
        private fun showFloatBox(context: Context, bundle: Bundle) {
            if (isShown)
                return
            this.context = context
            mBundle = bundle
            isShown = true
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val callSession = QXCallClient.getInstance().getCallSession()
            mediaType = bundle.getString("mediaType")
            if (mediaType == "2" && callSession != null && callSession.conversionType == ConversationType.TYPE_PRIVATE) {
                var remoteVideoView: SurfaceView? = null
                for (userprofile in callSession!!.userProfie) {
                    if (userprofile.userId != QXIMClient.instance.getCurUserId()!!) {
                        remoteVideoView = userprofile.surfaceView
                    }
                }
                val params = createRemoteVideoLayoutParams(context)
                if (remoteVideoView != null) {
                    (remoteVideoView.parent as ViewGroup).removeView(remoteVideoView)
                    val resources: Resources = context.resources
                    params.width = resources.getDimensionPixelSize(R.dimen.callkit_floatbox_video_width)
                    params.height = resources.getDimensionPixelSize(R.dimen.callkit_floatbox_video_height)
                }
                mRemoteVideoContainer = FrameLayout(context)
                mRemoteVideoContainer?.addView(
                    remoteVideoView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
                mRemoteVideoContainer!!.setOnTouchListener(createOnTouchListener())
                mWindowManager!!.addView(mRemoteVideoContainer, params)
            }
            mView = LayoutInflater.from(context).inflate(R.layout.qx_voip_float_box, null)
            val param = createLayoutParams(context)
            mView!!.setOnTouchListener(createOnTouchListener())
            mWindowManager!!.addView(mView, param)

            val activeTime = if (callSession?.activeTime!! > 0) callSession?.activeTime else 0
            mTime = if (activeTime == 0L) 0 else (System.currentTimeMillis() - activeTime!!) / 1000
            if (mTime > 0) {
                setAudioMode(AudioManager.MODE_IN_COMMUNICATION)
            }
            callTimeTv = mView!!.findViewById<TextView>(R.id.audioStateTv)
            setupTime(callTimeTv!!)
            QLog.e(TAG,"showFloatBox  isDial:${QXCallKitUtils.isDial},isShown:${isShown}")
            QXCallClient.getInstance().qxCallListener = object : IQXCallListener {
                override fun onCallOutgoing(localView: SurfaceView) {

                }

                override fun onCallConnecting() {
                    callTimeTv?.text = context.getString(R.string.qx_voip_call_ing)
                }

                override fun onCallConnected(remoteView: SurfaceView) {
                    if (QXCallKitUtils.isDial && isShown) {
                        QXCallKitUtils.isDial = false
                        showFloatBoxToCallTime()
                    }
                    setAudioMode(AudioManager.MODE_IN_COMMUNICATION)
                }

                override fun onCallDisconnected(callState: QXCallState) {
                    setAudioMode(AudioManager.MODE_NORMAL)
                    hideFloatBox()
                }

                override fun onRemoteUserRinging(userId: String) {
                }

                override fun onRemoteUserJoined(viewType: Int, remoteView: SurfaceView) {
                }

                override fun onRemoteUserInvited(remoteView: SurfaceView) {
                }

                override fun mediaTypeChange() {
                    mediaType = "1"
                    if (mRemoteVideoContainer != null) {
                        mWindowManager!!.removeView(mRemoteVideoContainer)
                        mRemoteVideoContainer = null
                    }
                }
            }
        }

        /**
         * 拨打后还未接通时显示浮窗
         */
        private fun showFloatBoxToCall(context: Context, bundle: Bundle) {
            if (isShown)
                return
            this.context = context
            mBundle = bundle
            isShown = true
            mediaType = bundle.getString("mediaType")
            mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            mView = LayoutInflater.from(context).inflate(R.layout.qx_voip_float_box, null)
            val param = createLayoutParams(context)
            mView!!.setOnTouchListener(createOnTouchListener())
            callTimeTv = mView!!.findViewById<TextView>(R.id.audioStateTv)
            callTimeTv!!.text = context.getString(R.string.qx_voip_call_ing)
            mWindowManager!!.addView(mView, param)
            QLog.e(TAG,"showFloatBoxToCall  isDial:${QXCallKitUtils.isDial},isShown:${isShown}")
            QXCallClient.getInstance().qxCallListener = object : IQXCallListener {
                override fun onCallOutgoing(localView: SurfaceView) {

                }

                override fun onCallConnecting() {
                    callTimeTv?.text = context.getString(R.string.qx_voip_call_ing)
                }

                override fun onCallConnected(remoteView: SurfaceView) {
                    QLog.e(TAG,"onCallConnected isDial:${QXCallKitUtils.isDial},isShown:${isShown}")
                    if (QXCallKitUtils.isDial && isShown) {
                        QXCallKitUtils.isDial = false
                        showFloatBoxToCallTime()
                        if (mediaType == "2") {
                            val callSession = QXCallClient.getInstance().getCallSession()
                            var remoteVideoView: SurfaceView? = null
                            for (userprofile in callSession!!.userProfie) {
                                if (userprofile.userId != QXIMClient.instance.getCurUserId()!!) {
                                    remoteVideoView = userprofile.surfaceView
                                }
                            }
                            val params = createRemoteVideoLayoutParams(context)
                            if (remoteVideoView != null) {
                                val resources: Resources = context.resources
                                params.width = resources.getDimensionPixelSize(R.dimen.callkit_floatbox_video_width)
                                params.height = resources.getDimensionPixelSize(R.dimen.callkit_floatbox_video_height)
                            }
                            mRemoteVideoContainer = FrameLayout(context)
                            mRemoteVideoContainer?.addView(
                                remoteVideoView,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT)
                            mRemoteVideoContainer!!.setOnTouchListener(createOnTouchListener())
                            mWindowManager!!.addView(mRemoteVideoContainer, params)
                            QLog.e(TAG,"onCallConnected addView ")
                        }
                    }
                    setAudioMode(AudioManager.MODE_IN_COMMUNICATION)

                }

                override fun onCallDisconnected(callState: QXCallState) {
                    setAudioMode(AudioManager.MODE_NORMAL)
                    hideFloatBox()
                }

                override fun onRemoteUserRinging(userId: String) {
                }

                override fun onRemoteUserJoined(viewType: Int, remoteView: SurfaceView) {
                }

                override fun onRemoteUserInvited(remoteView: SurfaceView) {
                }

                override fun mediaTypeChange() {
                    mediaType = "1"
                    if (mRemoteVideoContainer != null) {
                        mWindowManager!!.removeView(mRemoteVideoContainer)
                        mRemoteVideoContainer = null
                    }
                }
            }
        }

        private fun showFloatBoxToCallTime() {
            if (!isShown) {
                return
            }
            val callSession = QXCallClient.getInstance().getCallSession()
            val activeTime = if (callSession?.activeTime!! > 0) callSession?.activeTime else 0
            mTime = if (activeTime == 0L) 0 else (System.currentTimeMillis() - activeTime!!) / 1000
            callTimeTv?.apply {
                setupTime(this)
            }
        }

        private fun setupTime(timeView: TextView) {
            val handler = Handler(Looper.getMainLooper())
            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    handler.post {
                        mTime++
                        if (mTime >= 3600) {
                            timeView.text = String.format(
                                "%d:%02d:%02d",
                                mTime / 3600,
                                mTime % 3600 / 60,
                                mTime % 60)
                            timeView.visibility = View.VISIBLE
                        } else {
                            timeView.text = String.format(
                                "%02d:%02d",
                                mTime % 3600 / 60,
                                mTime % 60)
                            timeView.visibility = View.VISIBLE
                        }
                    }
                }
            }
            timer = Timer()
            timer!!.schedule(task, 0, 1000)
        }

        private fun createLayoutParams(context: Context): WindowManager.LayoutParams {
            val params = WindowManager.LayoutParams()
            var type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 24) {
                WindowManager.LayoutParams.TYPE_TOAST
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            params.type = type
            params.flags = (WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            params.format = PixelFormat.TRANSLUCENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            params.x = context.resources.displayMetrics.widthPixels
            params.y = 0
            return params
        }


        private fun createRemoteVideoLayoutParams(context: Context): WindowManager.LayoutParams {
            val params = WindowManager.LayoutParams()
            var type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 24) {
                WindowManager.LayoutParams.TYPE_TOAST
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            params.type = type
            params.flags = (WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            params.format = PixelFormat.TRANSLUCENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.LEFT
            params.x = 0
            params.y = 150
            return params
        }

        private fun createOnTouchListener(): View.OnTouchListener {
            return object : View.OnTouchListener {
                private var lastX = 0.0f
                private var lastY = 0.0f
                private var oldOffsetX = 0
                private var oldOffsetY = 0
                private var tag = 0
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    val action = event.action
                    var x = event.x
                    var y = event.y
                    var params = v.layoutParams ?: return true
                    params as WindowManager.LayoutParams
                    if (tag == 0) {
                        oldOffsetX = params.x
                        oldOffsetY = params.y
                    }
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastX = x
                            lastY = y
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x += ((x - lastX) / 3).toInt()
                            params.y += ((y - lastY) / 3).toInt()
                            tag = 1
                            mWindowManager!!.updateViewLayout(v, params)
                        }
                        MotionEvent.ACTION_UP -> {
                            var newOffsetX = params.x
                            var newOffsetY = params.y
                            if (abs(oldOffsetX - newOffsetX) <= 20 && abs(oldOffsetY - newOffsetY) <= 20) {
                                if (!isFastDoubleClick("default")) {
                                    clickToResume()
                                }
                            } else {
                                tag = 0
                            }
                        }
                    }
                    return true
                }

            }
        }

        fun hideFloatBox() {
            if (isShown) {
                try {
                    mView?.apply {
                        mWindowManager?.removeView(this)
                    }
                    mRemoteVideoContainer?.apply {
                        mWindowManager?.removeView(this)
                    }
                }catch (e : Exception) {
                    e.printStackTrace()
                }
                timer?.apply {
                    cancel()
                }
                mTime = 0
                timer = null
                mView = null
                isShown = false
                QXCallClient.getInstance().qxCallListener = QXCallProxy.getInstance
            }
        }

        private fun clickToResume() {
            mBundle?.putBoolean("isDial", QXCallKitUtils.isDial)
            val intent = Intent(mBundle?.getString("action"))
            intent.setPackage(context?.packageName)
            intent.putExtra("floatbox", mBundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("callAction", QXCallAction.ACTION_RESUME_CALL.name)
            context!!.startActivity(intent)
        }

        private fun isFastDoubleClick(eventType: String): Boolean {
            var lastClickTime = mapLastClickTime[eventType]
            if (lastClickTime == null) {
                lastClickTime = 0L
            }
            val curTime = System.currentTimeMillis()
            val timeD = curTime - lastClickTime
            if (timeD in 1..799) {
                return true
            }
            mapLastClickTime["eventType"] = curTime
            return false
        }

        @JvmStatic
        fun isCallFloatBoxShown(): Boolean {
            return isShown
        }

        private fun setAudioMode(mode: Int) {
            val audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager != null) {
                audioManager.mode = mode
            }
        }
    }


}