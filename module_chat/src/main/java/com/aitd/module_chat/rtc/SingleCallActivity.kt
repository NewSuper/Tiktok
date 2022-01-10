package com.aitd.module_chat.rtc

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.aitd.library_common.utils.click
import com.aitd.library_common.utils.dp
import com.aitd.library_common.utils.dp2px
import com.aitd.module_chat.QXUserInfo
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXUserInfoManager
import com.aitd.module_chat.utils.PermissionCheckUtil
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.file.GlideUtil
import com.aitd.module_chat.utils.qlog.QLog
import kotlinx.android.synthetic.main.qx_voip_activity_call.*
import kotlinx.android.synthetic.main.qx_voip_float_box.*
import kotlinx.android.synthetic.main.qx_voip_float_box.audioStateTv
import kotlinx.android.synthetic.main.qx_voip_stub_audio.*
import kotlinx.android.synthetic.main.qx_voip_stub_video.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class SingleCallActivity : BaseCallActivity() {

    private val TAG = "SingleCallActivity"

    private var targetId: String = ""
    private var conversionType: String = ""

    private var muteEnable = true
    private var handsFree = false

    private var callTime = 0L

    private lateinit var mediaType: QXCallMediaType
    private var localSurfaceView: SurfaceView? = null
    private var remoteSurfaceView: SurfaceView? = null
    private var callSession: QXCallSession? = null
    private var callAction: String = ""

    private val startForCheckPermissions = false

    // 通话计时
    private var callTimeTimer: Timer? = null
    private var callTimeTimerTask: TimerTask? = null

    // 连接中
    private var waitingTimer: Timer? = null
    private var waitingTimerTask: TimerTask? = null
    private var waitingCount: Int = 1

    private var audioInflateView: View? = null
    private var videoInflatView: View? = null
    private var _isFinishing = false
    private var changeSurfaceView = false

    override fun getLayoutId(): Int = R.layout.qx_voip_activity_call
    override fun init(saveInstanceState: Bundle?) {
  clearNotify()
        val tempCallAction = intent.getStringExtra("callAction")
        if (!tempCallAction.isNullOrEmpty()) {
            callAction = tempCallAction!!
        }
        if (callAction.isNullOrEmpty()) {
            finish()
            return
        }
        if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
            callSession = intent.getParcelableExtra("callSession")
            targetId = QXIMClient.instance.getCurUserId()!!
            if (callSession == null) {
                finish()
                return
            }
            callSession?.apply {
                conversionType = this.conversionType
                mediaType = if (this.callType == "1") QXCallMediaType.AUDIO else QXCallMediaType.VIDEO
            }
            QXCallPlayer.start(this)
        } else if (callAction == QXCallAction.ACTION_OUTGOING_CALL.name) {
            targetId = intent.getStringExtra("targetId")!!
            conversionType = intent.getStringExtra("conversionType")!!
            mediaType = if (intent.action == "qx.rtc.intent.action.voip.SINGLEAUDIO") QXCallMediaType.AUDIO else QXCallMediaType.VIDEO
        } else {
            // resume call
            callSession = QXCallClient.getInstance().getCallSession()
            if (callSession == null) {
                finish()
                return
            }
            callSession?.apply {
                mediaType = if (this.callType == "1") QXCallMediaType.AUDIO else QXCallMediaType.VIDEO
                QLog.e(TAG, ">>>> oncreate  resume call")
            }
        }
        if (mediaType != null) {
            if (requestCallPermissions(REQUEST_CODE_PERMISSIONS)) {
                getIntentData()
            }
        }
        initView()
        setViewClick()
        IncomingCallExtraHandleUtil.clear()
    }

    private fun getAudioInflateView() {
        if (audioInflateView == null) {
            audioInflateView = vsCallAudioLayout.inflate()
        }
    }

    private fun getVideoInflateView() {
        if (videoInflatView == null) {
            videoInflatView = vsCallVideoLayout.inflate()
        }
    }

    private fun clearNotify() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun getIntentData() {
        if (callAction == QXCallAction.ACTION_OUTGOING_CALL.name) {
            QXCallClient.startCall(conversionType, targetId, mutableListOf(targetId), mediaType)
        }
    }

    private fun initView() {
        var qxUserInfo: QXUserInfo? = null
        if (mediaType == QXCallMediaType.AUDIO) {
            getAudioInflateView()
            if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(callSession?.callId)
                audioStateTv.text = getString(R.string.qx_voip_audio_invite_hint)
                audioStateTv.visibility = View.VISIBLE
                hangUpBtn.visibility = View.VISIBLE
                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams.leftMargin = dp2px(this, 60)
                hangUpBtn.layoutParams = layoutParams
                acceptAudioBtn.visibility = View.VISIBLE

            } else if (callAction == QXCallAction.ACTION_RESUME_CALL.name) {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
                callSession?.apply {
                    if (activeTime > 0) {
                        hangUpBtn.visibility = View.VISIBLE
                        mutebtn.visibility = View.VISIBLE
                        mutebtn.isEnabled = true
                        handsFreeBtn.visibility = View.VISIBLE
                    } else {
                        hangUpBtn.visibility = View.VISIBLE
                        mutebtn.visibility = View.VISIBLE
                        mutebtn.isEnabled = false
                        handsFreeBtn.visibility = View.VISIBLE
                    }
                }

            } else {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.text = getString(R.string.qx_voip_cancel)
                mutebtn.visibility = View.VISIBLE
                mutebtn.isEnabled = false
                handsFreeBtn.visibility = View.VISIBLE
                startWaitingTimer()


            }
            setUserInfo(qxUserInfo)
        } else {
            getVideoInflateView()
            if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(callSession!!.callId)
                videoStateTv.text = getString(R.string.qx_voip_video_invite_hint)
                qxUserInfo?.let {
                    GlideUtil.showBlurTransformation(this, callVideoImage, it.avatarUri)
                }
                switchAudio.visibility = View.VISIBLE

                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams.addRule(RelativeLayout.BELOW, R.id.switchAudio)
                layoutParams.leftMargin = dp2px(this, 60)
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.text = getString(R.string.qx_voip_refresu)

                val layoutParams2 = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams2.addRule(RelativeLayout.BELOW, R.id.switchAudio)
                layoutParams2.rightMargin = dp2px(this, 60)
                acceptVideoBtn.layoutParams = layoutParams2
                acceptVideoBtn.visibility = View.VISIBLE
            } else if (callAction == QXCallAction.ACTION_RESUME_CALL.name) {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
                callSession?.apply {
                    if (activeTime > 0) {
                        mutebtn.visibility = View.GONE
                        acceptVideoBtn.visibility = View.GONE
                        callVideoImage.visibility = View.GONE
                        switchAudio.visibility = View.VISIBLE
                        val layoutParamsLeft = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        layoutParamsLeft.addRule(RelativeLayout.CENTER_VERTICAL)
                        layoutParamsLeft.leftMargin = dp2px(this@SingleCallActivity, 30)
                        switchAudio.layoutParams = layoutParamsLeft

                        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                        hangUpBtn.visibility = View.VISIBLE
                        hangUpBtn.layoutParams = layoutParams
                        hangUpBtn.text = getString(R.string.qx_voip_hangup)
                        switchCameraBtn.visibility = View.VISIBLE

                    } else {
                        switchAudio.visibility = View.VISIBLE
                        hangUpBtn.visibility = View.VISIBLE
                        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        layoutParams.leftMargin = dp2px(this@SingleCallActivity, 60)
                        hangUpBtn.layoutParams = layoutParams
                        hangUpBtn.text = getString(R.string.qx_voip_cancel)
                    }
                }
                if (mediaType == QXCallMediaType.VIDEO) {
                    setVideoUserInfoLayout()
                }
            } else {
                qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
                switchAudio.visibility = View.VISIBLE
                hangUpBtn.visibility = View.VISIBLE
                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams.leftMargin = dp2px(this, 60)
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.text = getString(R.string.qx_voip_cancel)
                startWaitingTimer()
                setVideoUserInfoLayout()
            }
            setUserInfo(qxUserInfo)
        }
    }


    private fun setVideoUserInfoLayout() {
        val avatarLayoutParams = RelativeLayout.LayoutParams(75f.dp.toInt(), 75f.dp.toInt())
        avatarLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        avatarLayoutParams.topMargin = 60f.dp.toInt()
        videoAvatraIv.layoutParams = avatarLayoutParams

        val nameLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        nameLayoutParams.addRule(RelativeLayout.RIGHT_OF, videoAvatraIv.id)
        nameLayoutParams.addRule(RelativeLayout.ALIGN_TOP, videoAvatraIv.id)
        nameLayoutParams.leftMargin = 10f.dp.toInt()
        nameLayoutParams.rightMargin = 10f.dp.toInt()
        videoNameTv.layoutParams = nameLayoutParams

        val stateLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        stateLayoutParams.addRule(RelativeLayout.RIGHT_OF, videoAvatraIv.id)
        stateLayoutParams.addRule(RelativeLayout.BELOW, videoNameTv.id)
        stateLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, videoAvatraIv.id)
        stateLayoutParams.leftMargin = 10f.dp.toInt()
        stateLayoutParams.topMargin = 10f.dp.toInt()
        videoStateTv.layoutParams = stateLayoutParams
    }

    private fun setUserInfo(user: QXUserInfo?) {
        user?.let {
            var name = it.displayName
            if (mediaType == QXCallMediaType.AUDIO) {
                audioNameTv.text = name
                GlideUtil.showAvatar(this, audioAvatraIv, it.avatarUri)
                GlideUtil.showBlurTransformation(this, callAudioImage, it.avatarUri)
            } else {
                videoNameTv.text = name
                GlideUtil.showAvatar(this, videoAvatraIv, it.avatarUri)
            }
        }

    }

    private fun setViewClick() {
        // 静音
        mutebtn.click {
            if (_isFinishing)
                return@click
            muteEnable = !muteEnable
            QXCallClient.mute(muteEnable)
            it.isSelected = !muteEnable
        }
        // 挂断 取消 取消呼叫
        hangUpBtn.click {
            if (_isFinishing)
                return@click
            _isFinishing = true
            if (isCalling) {
                // 挂断
                ToastUtil.toast(this, getString(R.string.qx_voip_call_end))
                QXCallClient.hangUpCall()
            } else {
                ToastUtil.toast(this, getString(R.string.qx_voip_call_cancel))
                if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
                    // 拒接
                    QXCallPlayer.stop(this)
                    QXCallClient.refuseCall()
                } else {
                    // 取消呼叫
                    QXCallClient.cancelCall()
                }

            }
        }
        // 接受音频通话
        acceptAudioBtn.click {
            if (_isFinishing)
                return@click
            QXCallPlayer.stop(this)
            QXCallClient.acceptCall()
        }
        // 接受视频通话
        acceptVideoBtn.click {
            if (_isFinishing)
                return@click
            // 接听
            QXCallPlayer.stop(this)
            QXCallClient.acceptCall()
        }
        // 扬声器
        handsFreeBtn.click {
            if (_isFinishing)
                return@click
            handsFree = !handsFree
            if (handsFree) {
                muteEnable = true
                mutebtn.isSelected = !muteEnable
                QXCallClient.mute(muteEnable)
                ToastUtil.toast(this, R.string.qx_voip_voice_hint)
            }
            QXCallClient.handsFree(handsFree)
            it.isSelected = handsFree
        }

        // 切换摄像头
        switchCameraBtn.click {
            if (_isFinishing)
                return@click
            switchCameraBtn.isSelected = !switchCameraBtn.isSelected
            QXCallClient.switchCamera()
        }

        // 切换到语音
        switchAudio.click {
            if (_isFinishing)
                return@click
            videoAvatraIv.visibility = View.GONE
            videoNameTv.visibility = View.GONE
            QXCallPlayer.stop(this)
            QXCallClient.switchAudio()
        }

        // 最小化
        ivMinimize.click {
            if (_isFinishing)
                return@click
            minimizeClick()
        }

        callSmallPreview?.click {
            if (_isFinishing)
                return@click
            it.postDelayed({
                (localSurfaceView!!.parent as ViewGroup).removeView(localSurfaceView)
                (remoteSurfaceView!!.parent as ViewGroup).removeView(localSurfaceView)
                callSmallPreview.removeAllViews()
                callLargePreview.removeAllViews()
                changeSurfaceView = !changeSurfaceView
                if (changeSurfaceView) {
                    localSurfaceView?.setZOrderMediaOverlay(false)
                    remoteSurfaceView?.setZOrderMediaOverlay(true)
                    callLargePreview.addView(localSurfaceView)
                    callSmallPreview.addView(remoteSurfaceView)
                } else {
                    localSurfaceView?.setZOrderMediaOverlay(true)
                    remoteSurfaceView?.setZOrderMediaOverlay(false)
                    callLargePreview.addView(remoteSurfaceView)
                    callSmallPreview.addView(localSurfaceView)
                }
            }
                ,1000)
        }
    }

    private fun startTime() {
        if (callTimeTimer == null) {
            callTimeTimer = Timer()
            callTimeTimerTask = object : TimerTask() {
                override fun run() {
                    QLog.e(TAG, " >>> startTime  ")
                    callTime++
                    runOnUiThread {
                        callTimeTv.visibility = View.VISIBLE
                        callTimeTv.text = getString(R.string.qx_voip_time, "${getFormatTime()}")
                    }
                }
            }
            callTimeTimer!!.schedule(callTimeTimerTask, 0, 1 * 1000)
        } else {
            QLog.d(TAG, " >>> startTime callTimeTimer is not null ")
        }
    }

    private fun startWaitingTimer() {
        QLog.d(TAG, " >>> startWaitingTimer ")
        waitingTimer = Timer()
        waitingTimerTask = object : TimerTask() {
            override fun run() {
                QLog.d(TAG, " >>> startWaitingTimer run ")
                if (waitingCount > 3) {
                    waitingCount = 1
                }
                var point = ""
                when (waitingCount) {
                    1 -> point = "."
                    2 -> point = ".."
                    3 -> point = "..."
                }
                runOnUiThread {
                    if (mediaType == QXCallMediaType.AUDIO) {
                        audioStateTv.visibility = View.VISIBLE
                        audioStateTv.text = "${getString(R.string.qx_voip_audio_waiting_for_answer)}$point"
                    } else {
                        videoStateTv.visibility = View.VISIBLE
                        videoStateTv.text = "${getString(R.string.qx_voip_video_waiting_for_answer)}$point"
                    }
                }
                waitingCount++
            }
        }
        waitingTimer!!.schedule(waitingTimerTask, 0, 1 * 1000)
    }

    private fun stopWaiting() {
        waitingTimer?.cancel()
        waitingTimerTask?.cancel()
        waitingTimer = null
        waitingTimerTask = null
        if (mediaType == QXCallMediaType.AUDIO) {
            audioStateTv.visibility = View.GONE
        } else {
            videoStateTv.visibility = View.GONE
            videoNameTv.visibility = View.GONE
            videoAvatraIv.visibility = View.GONE
        }
    }

    private fun getFormatTime(): String {
        return if (callTime >= 3600) {
            String.format(
                "%d:%02d:%02d",
                callTime / 3600,
                callTime % 3600 / 60,
                callTime % 60)
        } else {
            String.format(
                "%02d:%02d",
                callTime % 3600 / 60,
                callTime % 60)
        }
    }

    override fun onCallConnecting() {
        super.onCallConnecting()
        if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
            if (mediaType == QXCallMediaType.VIDEO) {
                callSmallPreview.visibility = View.VISIBLE
                callVideoImage.visibility = View.GONE
                switchAudio.visibility = View.VISIBLE
                val layoutParamsLeft = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParamsLeft.addRule(RelativeLayout.CENTER_VERTICAL)
                layoutParamsLeft.leftMargin = dp2px(this, 30)
                switchAudio.layoutParams = layoutParamsLeft

                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.text = getString(R.string.qx_voip_hangup)

                switchCameraBtn.visibility = View.VISIBLE
                acceptVideoBtn.visibility = View.GONE
            } else {
                mutebtn.visibility = View.VISIBLE
                mutebtn.isEnabled = true
                handsFreeBtn.visibility = View.VISIBLE
                acceptAudioBtn.visibility = View.GONE

                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.text = getString(R.string.qx_voip_hangup)
            }

        }
        callTimeTv.visibility = View.VISIBLE
        callTimeTv.text = getString(R.string.qx_voip_call_ing)
    }

    override fun onCallConnected(remoteView: SurfaceView) {
        super.onCallConnected(remoteView)
        QLog.d(TAG, "onCallConnected...")
        isCalling = true
        if (mediaType == QXCallMediaType.VIDEO) {
            callSmallPreview.visibility = View.VISIBLE
            remoteSurfaceView = remoteView
            changeView()

            callVideoImage.visibility = View.GONE
            switchAudio.visibility = View.VISIBLE
            val layoutParamsLeft = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            layoutParamsLeft.addRule(RelativeLayout.CENTER_VERTICAL)
            layoutParamsLeft.leftMargin = dp2px(this, 30)
            switchAudio.layoutParams = layoutParamsLeft

            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            hangUpBtn.visibility = View.VISIBLE
            hangUpBtn.layoutParams = layoutParams
            hangUpBtn.text = getString(R.string.qx_voip_hangup)

            switchCameraBtn.visibility = View.VISIBLE
            acceptVideoBtn.visibility = View.GONE

        } else {
            mutebtn.visibility = View.VISIBLE
            mutebtn.isEnabled = true
            handsFreeBtn.visibility = View.VISIBLE
            acceptAudioBtn.visibility = View.GONE

            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            hangUpBtn.visibility = View.VISIBLE
            hangUpBtn.layoutParams = layoutParams
            hangUpBtn.text = getString(R.string.qx_voip_hangup)
        }
        startTime()
        stopWaiting()
    }

    override fun onCallDisconnected(callState: QXCallState) {
        super.onCallDisconnected(callState)
        QLog.d(TAG, "onCallDisconnected...")
        if (mediaType == QXCallMediaType.AUDIO) {

        } else {
            callSmallPreview.removeAllViews()
            callLargePreview.removeAllViews()
        }
        finish()
    }

    override fun onCallOutgoing(localView: SurfaceView) {
        super.onCallOutgoing(localView)
        ivMinimize.visibility = View.VISIBLE
        if (mediaType == QXCallMediaType.AUDIO) {

        } else {
            // 呼叫
            localSurfaceView = localView
            callLargePreview.visibility = View.VISIBLE
            callLargePreview.addView(localView)
        }
    }

    override fun onRemoteUserInvited(view: SurfaceView) {
        super.onRemoteUserInvited(view)
    }

    override fun onRemoteUserJoined(viewType: Int, view: SurfaceView) {
        super.onRemoteUserJoined(viewType, view)
    }

    override fun onRemoteUserRinging(userId: String) {
        super.onRemoteUserRinging(userId)
    }

    override fun mediaTypeChange() {
        super.mediaTypeChange()
        QLog.d(TAG, " >> mediaTypeChange ")
        getAudioInflateView()
        videoAvatraIv.visibility = View.GONE
        videoNameTv.visibility = View.GONE
        acceptVideoBtn.visibility = View.GONE
        switchCameraBtn.visibility = View.GONE
        switchAudio.visibility = View.GONE

        mediaType = QXCallMediaType.AUDIO
        if (!isCalling) {
            val loginUserId = QXIMClient.instance.getCurUserId()
            if (targetId != loginUserId) {
                mutebtn.visibility = View.VISIBLE
                mutebtn.isEnabled = false
                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.text = getString(R.string.qx_voip_cancel)
                handsFreeBtn.visibility = View.VISIBLE
                stopWaiting()
                startWaitingTimer()
            } else {
                // 接受方显示接听
                val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams.leftMargin = dp2px(this, 60)
                hangUpBtn.layoutParams = layoutParams
                hangUpBtn.visibility = View.VISIBLE
                hangUpBtn.text = getString(R.string.qx_voip_refresu)
                acceptAudioBtn.visibility = View.VISIBLE
                audioStateTv.text = getString(R.string.qx_voip_audio_invite_hint)
                audioStateTv.visibility = View.VISIBLE
            }
        } else {
            stopWaiting()
            mutebtn.visibility = View.VISIBLE
            handsFreeBtn.visibility = View.VISIBLE
        }

        var qxUserInfo: QXUserInfo? = null
        if (callAction == QXCallAction.ACTION_INCOME_CALL.name) {
            qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(callSession?.callId)
        } else {
            qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
        }
        qxUserInfo?.let {
            audioInflateView?.let { view ->
                view.visibility = View.VISIBLE
                setUserInfo(qxUserInfo)
            }
            videoInflatView?.visibility = View.GONE

        }
    }

    private fun changeView() {
        (localSurfaceView!!.parent as ViewGroup).removeView(localSurfaceView)
        callSmallPreview.addView(localSurfaceView)
        callLargePreview.removeAllViews()
        callLargePreview.addView(remoteSurfaceView)
    }

    override fun onSaveFloatBoxState(bundle: Bundle): String? {
        super.onSaveFloatBoxState(bundle)
        callSession = QXCallClient.getInstance().getCallSession()
        bundle.putBoolean("muted", muteEnable)
        bundle.putBoolean("handFree", handsFree)
        bundle.putString("mediaType", callSession?.callType)
        return intent.action
    }

    override fun onRestoreFloatBox(bundle: Bundle) {
        super.onRestoreFloatBox(bundle)
        QLog.d(TAG, "onRestoreFloatBox")
        if (bundle == null) return
        muteEnable = bundle.getBoolean("muted")
        handsFree = bundle.getBoolean("handFree")
        setShouldShowFloat(true)
        callSession = QXCallClient.getInstance().getCallSession()
        if (callSession == null) {
            setShouldShowFloat(false)
            finish()
            return
        }
        val loginUserId = QXIMClient.instance.getCurUserId()
        targetId = if (!loginUserId.isNullOrEmpty() && loginUserId == callSession!!.callId) {
            callSession!!.targetId
        } else {
            callSession!!.callId
        }

        mediaType = callSession!!.getMediaType()
        // 恢复UI
        if (callSession!!.activeTime > 0L) {
            callTime = (System.currentTimeMillis() - callSession!!.activeTime) / 1000
            startTime()
            isCalling = true
            if (mediaType == QXCallMediaType.VIDEO) {
                // 已接通
            } else {
                handsFreeBtn.visibility = View.VISIBLE
                mutebtn.visibility = View.VISIBLE
            }
        } else {
            isCalling = false
        }
        if (!isCalling) {
            startWaitingTimer()
        }
        ivMinimize.visibility = View.VISIBLE
        hangUpBtn.visibility = View.VISIBLE

        for (userprofile in callSession!!.userProfie) {
            if (userprofile.userId == QXIMClient.instance.getCurUserId()!!) {
                localSurfaceView = userprofile.surfaceView
            } else {
                remoteSurfaceView = userprofile.surfaceView
            }
        }

        if (localSurfaceView != null && localSurfaceView!!.parent != null) {
            (localSurfaceView!!.parent as ViewGroup).removeView(localSurfaceView)
        }
        if (mediaType == QXCallMediaType.VIDEO) {
            getVideoInflateView()
            callLargePreview.visibility = View.VISIBLE
            callLargePreview.addView(localSurfaceView)
        }
        if (remoteSurfaceView != null) {
            if (remoteSurfaceView!!.parent != null) {
                (remoteSurfaceView!!.parent as ViewGroup).removeView(remoteSurfaceView)
            }
        }
        if (mediaType == QXCallMediaType.VIDEO) {
            if (isCalling) {

                callSmallPreview.visibility = View.VISIBLE
                changeView()
            }
        }

        var qxUserInfo = QXUserInfoManager.getInstance().getUserInfo(targetId)
        qxUserInfo?.let {
            if (mediaType == QXCallMediaType.VIDEO) {
                audioInflateView?.apply {
                    visibility = View.GONE
                }
                videoInflatView?.apply {
                    visibility = View.VISIBLE
                }
                if (!isCalling) {
                    setUserInfo(qxUserInfo)
                } else {
                    videoStateTv.visibility = View.GONE
                }

            } else {
                videoInflatView?.apply {
                    visibility = View.GONE
                }

                audioInflateView?.apply {
                    visibility = View.VISIBLE
                }
                setUserInfo(qxUserInfo)
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        QLog.d(TAG, "onRequestPermissionsResult $requestCode,permissions:${permissions.size},grantResults:${grantResults.size}")
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val permissions: Array<String>
            if (mediaType.name == QXCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO.name) {
                permissions = AUDIO_CALL_PERMISSIONS
            } else {
                permissions = VIDEO_CALL_PERMISSIONS
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
//                    RongCallClient.getInstance().onPermissionGranted()
                } else {
                    getIntentData()
                }
            } else {
                if (startForCheckPermissions) {
//                    RongCallClient.getInstance().onPermissionDenied()
                } else {
                    finish()
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        QLog.d(TAG, "onActivityResult $requestCode,resultCode:$resultCode")
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val permissions: Array<String>
            if (mediaType.name == QXCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO.name) {
                permissions = AUDIO_CALL_PERMISSIONS
            } else {
                permissions = VIDEO_CALL_PERMISSIONS
            }
            if (PermissionCheckUtil.checkPermissions(this, permissions)) {
                if (startForCheckPermissions) {
//                    RongCallClient.getInstance().onPermissionGranted()
                } else {
                    getIntentData()
                }
            } else {
                if (startForCheckPermissions) {
//                    RongCallClient.getInstance().onPermissionDenied()
                } else {
                    finish()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventUserUpdate(userinfo: QXUserInfo) {
        setUserInfo(userinfo)
    }


    override fun onDestroy() {
        super.onDestroy()
        QLog.e(TAG,"ondestory")
        callTimeTimer?.cancel()
        callTimeTimerTask?.cancel()
        callTimeTimer = null
        callTimeTimerTask = null
        stopWaiting()
        QXCallPlayer.stop(this)
    }

}
