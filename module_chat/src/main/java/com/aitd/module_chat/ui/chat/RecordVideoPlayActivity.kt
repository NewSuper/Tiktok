package com.aitd.module_chat.ui.chat

import android.os.Bundle
import android.text.TextUtils
import com.aitd.module_chat.Message
import com.aitd.module_chat.VideoMessage
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.ui.emotion.RecordItem


class RecordVideoPlayActivity : VideoPlayActivity() {

    var videoMessage: VideoMessage? = null
    var message: Message? = null
    var provider: QXIMKit.QXFavoriteProvider? = QXContext.getInstance().favoriteProvider

    override fun init(saveInstanceState: Bundle?) {
        super.init(saveInstanceState)
        try {
            message = intent.getParcelableExtra("message")
            if (message == null) {
                return
            }
            videoMessage = message?.messageContent as VideoMessage
            if (videoMessage == null) {
                return
            }
            videoMessage?.localPath = ""
            var recordItem = provider?.queryRecord(videoMessage?.originUrl)
            videoMessage?.localPath = recordItem?.localPath

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun getOriginUrl(): String {
        if (!TextUtils.isEmpty(videoMessage?.originUrl)) {
            return videoMessage?.originUrl!!
        }
        return ""
    }

    override fun getLocalPath(): String {
        if (!TextUtils.isEmpty(videoMessage?.localPath)) {
            return videoMessage?.localPath!!
        }
        return ""
    }

    override fun updateLocalPath(path: String) {
        if (provider != null && message != null) {
            provider?.insertRecord(
                RecordItem(
                    message?.messageId,
                    path,
                    videoMessage?.originUrl,
                    "",
                    QXIMKit.getInstance().curUserId
                )
            )
        }
        playVideo(path)
    }

    override fun download() {
        if (TextUtils.isEmpty(getLocalPath())) {
            super.download()
        }
    }
}