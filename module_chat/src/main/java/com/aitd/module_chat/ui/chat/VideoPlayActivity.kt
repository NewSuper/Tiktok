package com.aitd.module_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.MediaController
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.Message
import com.aitd.module_chat.QXError
import com.aitd.module_chat.R
import com.aitd.module_chat.VideoMessage
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.listener.DownloadCallback
import com.aitd.module_chat.pojo.event.EventManage
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.aitd.module_chat.view.BottomPop
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.imui_activity_video_play.*
import org.greenrobot.eventbus.EventBus

open class VideoPlayActivity :BaseActivity(){

    override fun getLayoutId(): Int = R.layout.imui_activity_video_play

    private val TAG = "VideoPlayActivity"

    private var message: Message? = null

    private var video_localUrl: String? = null
    private var video_origiUrl: String? = null

    companion object {
        fun startActivity(context: Context, message: Message) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("message", message)
            context.startActivity(intent)
        }

        fun startActivity(context: Context, localUrl: String?, origiUrl: String) {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra("video_local_url", localUrl)
            intent.putExtra("video_origi_url", origiUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
    }

    override fun init(saveInstanceState: Bundle?) {
        message = intent.getParcelableExtra("message")
        video_localUrl = intent.getStringExtra("video_local_url")
        video_origiUrl = intent.getStringExtra("video_origi_url")

        val localMediaController = MyMediaController(findViewById(R.id.ll_closed), ll_save, ll_forward, ll_collection, this)
        video_view.setMediaController(localMediaController)

        if (TextUtils.isEmpty(getLocalPath())) {
            pb.visibility = View.VISIBLE
            download()
        } else {
            //加载本地路径
            playVideo(getLocalPath())
        }
        ll_closed.setOnClickListener {
            onBackPressed()
        }
        ll_save.setOnClickListener {
            val localPath = (message!!.messageContent as VideoMessage).localPath
            if (!localPath.isNullOrEmpty()) {
                localMediaController.hide()
                val pop = BottomPop(this@VideoPlayActivity, localPath!!, "video")
                XPopup.Builder(this@VideoPlayActivity)
                    .asCustom(pop)
                    .show()
            }
        }

        ll_forward.setOnClickListener {
            //点击转发
            EventBus.getDefault().post(EventManage.OnVideoPlayClick(1, message, false))
        }
        ll_collection.setOnClickListener {
            //点击收藏
            EventBus.getDefault().post(EventManage.OnVideoPlayClick(2, message, false))
        }
    }

    open fun getOriginUrl(): String? {
        if (!TextUtils.isEmpty(video_origiUrl)) {
            return video_origiUrl
        } else {
            val videoMsg = message?.messageContent as VideoMessage
            return videoMsg.originUrl
        }
    }

    open fun getLocalPath(): String? {
        if (!TextUtils.isEmpty(video_localUrl)) {
            return video_localUrl
        } else {
            val videoMsg = message?.messageContent as VideoMessage
            return videoMsg.localPath
        }
    }

    open fun playVideo(path: String?) {
        runOnUiThread {
            pb.visibility = View.GONE
            video_view.setVideoURI(Uri.parse(path))
            video_view.start()
        }
    }

    open fun download() {
        var length: Long = -1
        if (message != null) {
            length = (message?.messageContent as VideoMessage).size
        }

        QXContext.getInstance().downloadProvider.download(QXIMKit.FileType.TYPE_VIDEO, length, getOriginUrl(), object :
            DownloadCallback {
            override fun onFailed(errorCode: Int, errorMsg: String?) {
                runOnUiThread {
                    ToastUtil.toast(this@VideoPlayActivity, String.format(resources.getString(R.string.qx_download_failed), errorMsg))
                    finish()
                }
            }

            override fun onProgress(progress: Int) {

            }

            override fun onCompleted(path: String?) {
                updateLocalPath(path!!)
            }

        })
    }

    open fun updateLocalPath(path: String) {
        if (message != null) {
            val videoMsg = message?.messageContent as VideoMessage
            videoMsg.localPath = path
            QXIMClient.instance.updateFileLocalPath(message!!, object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                    EventBusUtil.post(message!!)
                }

                override fun onFailed(error: QXError) {
                    QLog.e(TAG, "onError : ${error.code} ,msg:${error.msg}")
                }
            })
        }
        playVideo(path)
    }

    class MyMediaController(var view: View?, var saveView: View?, var ll_forward: View?, var ll_collection: View?, context: Context?) : MediaController(context) {

        override fun show() {
            super.show()
            view?.visibility = View.VISIBLE
            saveView?.visibility = View.VISIBLE
        }

        override fun hide() {
            super.hide()
            view?.visibility = View.GONE
            saveView?.visibility = View.GONE
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}