package com.aitd.module_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.AudioMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.AudioPlayManager
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.listener.DownloadCallback
import com.aitd.module_chat.listener.IAudioPlayListener
import com.aitd.module_chat.utils.ToastUtil
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.imui_activity_chat_voice.*


/**
 * 查看聊天文字页面
 */
open class ChatVoiceActivity : BaseActivity() {
    private var message: Message? = null
    private var audioMessage: AudioMessage? = null
    private var anim: AnimationDrawable? = null

    //是否下载中
    private var isDownloading = false

    //路径
    private var originUrl: String = ""
    private var localUrl: String = ""

    override fun getLayoutId(): Int = R.layout.imui_activity_chat_voice


    companion object {
        fun startActivity(context: Context, message: Message) {
            val intent = Intent(context, ChatVoiceActivity::class.java)
            intent.putExtra("message", message)
            context.startActivity(intent)
        }
    }

    override fun init(saveInstanceState: Bundle?) {
        voice_iv.setImageDrawable(resources.getDrawable(R.drawable.animation_list_voice))
        anim = voice_iv.drawable as AnimationDrawable?

        activity_layout.setOnClickListener {
            finish()
        }
        message = intent.getParcelableExtra("message")
        audioMessage = message!!.messageContent as AudioMessage

        localUrl = audioMessage!!.localPath
        originUrl = audioMessage!!.originUrl

        if (StringUtils.isEmpty(localUrl) && !StringUtils.isEmpty(originUrl)) {
            //默认先下载
            downloadVoice(originUrl)
        }

        lly_playVoice.setOnClickListener {
            if (!StringUtils.isEmpty(localUrl)) {
                //播放
                playVoice(localUrl)
            } else {
                //下载
                if (!StringUtils.isEmpty(originUrl) && !isDownloading) {
                    downloadVoice(originUrl)
                }
            }
        }
        voice_time_tv.text = getDuration()  //修复回复语音，语音时长显示
    }

    /**
     * 获取时长
     */
    open fun getDuration(): String {
        val audioMsg = message!!.messageContent as AudioMessage
        var second = resources.getString(R.string.qx_message_voice_duration)
        if (audioMsg.duration < 1) {
            audioMsg.duration = 1
        } else if (audioMsg.duration == 59 || audioMsg.duration > 60) {
            audioMsg.duration = 60
        }
        return String.format(second, audioMsg.duration)
    }


    open fun setDownloadState() {
        if (isDownloading) {
            download_progress.visibility = View.VISIBLE
        } else {
            download_progress.visibility = View.GONE
        }
    }

    /**
     * 下载中
     */
    private fun downloadVoice(originUrl: String) {
        isDownloading = true
        setDownloadState()
        var length: Long = -1;
        if (message != null) {
            length = (message?.messageContent as AudioMessage).size
        }
        QXContext.getInstance().downloadProvider.download(
            QXIMKit.FileType.TYPE_VOICE,
            length,
            originUrl,
            object :
                DownloadCallback {
                override fun onFailed(errorCode: Int, errorMsg: String?) {
                    isDownloading = false
                    runOnUiThread {
                        setDownloadState()
                    }
                    ToastUtil.toast(
                        this@ChatVoiceActivity, getString(
                            R.string.qx_download_fail, errorCode, errorCode
                                ?: ""
                        )
                    )
                }

                override fun onProgress(progress: Int) {

                }

                override fun onCompleted(path: String) {
                    //下载完成
                    isDownloading = false
                    localUrl = path
                    runOnUiThread {
                        setDownloadState()
                    }
                }
            })
    }


    /**
     * 播放
     */
    private fun playVoice(localUrl: String) {
        if (AudioPlayManager.getInstance().isPlaying) {
            //播放中先停止播放
            anim!!.stop()
            AudioPlayManager.getInstance().stopPlay()
            return
        }

        //声音通道占用检查
        if (!AudioPlayManager.getInstance().isInNormalMode(this) && AudioPlayManager.getInstance()
                .isInVOIPMode(this)
        ) {
            ToastUtil.toast(this, getString(R.string.qx_voip_occupying))
            return
        }
        val uri: Uri = Uri.parse(localUrl)
        AudioPlayManager.getInstance().startPlay(this, uri, object : IAudioPlayListener {
            override fun onStart(uri: Uri?) {
                anim!!.start()
            }

            override fun onStop(uri: Uri?) {
                anim!!.selectDrawable(0)
                anim!!.stop()
            }

            override fun onComplete(uri: Uri?) {
                //选择当前动画的第一帧，然后停止
                anim!!.selectDrawable(0)
                anim!!.stop()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //停止播放
        anim!!.stop()
        AudioPlayManager.getInstance().stopPlay()
    }

}