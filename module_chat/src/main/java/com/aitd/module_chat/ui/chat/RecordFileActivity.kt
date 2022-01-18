package com.aitd.module_chat.ui.chat

import android.text.TextUtils
import com.aitd.module_chat.FileMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.ui.emotion.RecordItem
import com.aitd.module_chat.utils.file.SizeUtil

class RecordFileActivity : ChatFileActivity() {
    var fileMessage: FileMessage? = null
    var message: Message? = null
    var provider: QXIMKit.QXFavoriteProvider? = QXContext.getInstance().favoriteProvider

    override fun initData() {
        try {
            message = intent.getParcelableExtra("message")
            if (message == null) {
                return
            }
            fileMessage = message?.messageContent as FileMessage
            if (fileMessage == null) {
                return
            }
            fileMessage?.localPath = ""
            var recordItem = provider?.queryRecord(fileMessage?.originUrl)
            fileMessage?.localPath = recordItem?.localPath

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFileName(): String {
        if(fileMessage != null) {
            return fileMessage?.fileName!!
        }
        return ""
    }

    override fun getSize(): String {
        if(fileMessage != null) {
            return SizeUtil.getDisplaySize(fileMessage?.size!!)
        }
        return ""
    }

    override fun getOriginUrl(): String {
        if (!TextUtils.isEmpty(fileMessage?.originUrl)) {
            return fileMessage?.originUrl!!
        }
        return ""
    }

    override fun getLocalPath(): String {
        if (!TextUtils.isEmpty(fileMessage?.localPath)) {
            return fileMessage?.localPath!!
        }
        return ""
    }

    override fun updateLocalPath(path: String) {
        if (provider != null && message != null) {
            provider?.insertRecord(RecordItem(message?.messageId, path, fileMessage?.originUrl, "", QXIMKit.getInstance().curUserId))
        }
    }

    override fun download() {
        if (TextUtils.isEmpty(getLocalPath())) {
            super.download()
        }
    }
}