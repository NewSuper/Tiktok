package com.aitd.module_chat.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.*
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.lib.boundary.FileSizeUtil
import com.aitd.module_chat.listener.DownloadCallback
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.file.FileUtil
import kotlinx.android.synthetic.main.imui_activity_chat_file.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*
import java.io.File

/**
 * 打开查看聊天页面的文件
 */
open class ChatFileActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context, message: Message) {
            val intent = Intent(context, ChatFileActivity::class.java)
            intent.putExtra("message", message)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.imui_activity_chat_file
    private var message: Message? = null

    //本地下载的路径
//    private var localDownloadPath: String? = null

    //后缀名
    private var fileSufixStr: String? = null

    //Tbs支持的类型:txt、doc、excel、ppt、pdf
    private var tbsX5SupportType = listOf<String>(".txt", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf")

    //App支持的图片类型
    private var appSupportImageType = listOf<String>(".png", ".jpg", ".jpeg", ".gif", ".bmp")

    //App支持的视频类型
    private var appSupportVideoType = listOf<String>(".mp4", ".avi", ".3gp", ".flv", ".rm", ".rmvb", ".mov", ".mpeg", ".wmv", ".mkv", ".f4v", ".mpg")

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iv_back.setOnClickListener {
            finish()
        }
        initData()
        initView()
    }

    override fun init(saveInstanceState: Bundle?) {
    }

    open fun initData() {
        message = intent.getParcelableExtra("message")
//        localDownloadPath = AlbumUtils.getAlbumTypeFileByUrl(AlbumType.TEMP, getOriginUrl())
        if (TextUtils.isEmpty(getOriginUrl())) {
            //文件消息没有发出去，url是空的
            fileSufixStr = FileUtil.getFileSuffix(getLocalPath())
        } else {
            fileSufixStr = FileUtil.getFileSuffix(getOriginUrl())
        }
    }


    open fun initView() {
        iv_menu.visibility = View.GONE

        iv_file_icon.setImageResource(getMessageType())
        tv_file_name.text = getFileName()
        tv_file_size.text = getSize()

        if (TextUtils.isEmpty(getOriginUrl())) {
            //没有发送成功的FileMessage
            updateUiState()
        } else {
            if (!checkIsDownload()) {
                //下载
                download()
            } else {
                updateUiState()
            }
        }

        btn_action.setOnClickListener {
            if (checkIsDownload() || TextUtils.isEmpty(getOriginUrl())) {
                //打开预览
                if (checkIsDownload()) {
                    openPreview(getLocalPath())
                } else if (TextUtils.isEmpty(getOriginUrl())) {
                    openPreview(getLocalPath())
                }
            } else {
                //下载
                download()
            }
        }
    }

    //打开预览
    open fun openPreview(previewUrl: String) {
        if (isSupportX5FileType()) {
            //打开预览
            PreviewX5WebViewActivity.start(this, previewUrl)
        } else if (isSupportAppFileType()) {
            if (isSupportAppImageType()) {
                //查看图片
                var intent = Intent(this@ChatFileActivity, RecordImagePageActivity::class.java)
                intent.putExtra("url", previewUrl)
                startActivity(intent)
            } else if (isSupportAppVideoType()) {
                //查看视频
                VideoPlayActivity.startActivity(this, previewUrl, getOriginUrl())
            }
        } else {
            //外部应用打开
            FileUtil.openFileByPath(this, previewUrl)
        }
    }

    open fun updateUiState() {
        loading_progress.visibility = View.GONE
        btn_action.visibility = View.VISIBLE
        if (!checkIsDownload() && !TextUtils.isEmpty(getOriginUrl())) {
            //未下载
            btn_action.text = getString(R.string.chat_file_detail_download)
        } else {
            var isSupptType = (isSupportX5FileType() || isSupportAppFileType())
            if (!isSupptType) {
                //用其他应用打开
                btn_action.text = getString(R.string.qx_open_other_app)
            } else {
                //内置打开
                btn_action.text = getString(R.string.chat_file_detail_open)
            }
        }
    }

    /**
     * 检查当前文件类型是否支持Tbs X5内核打开
     */
    open fun isSupportX5FileType(): Boolean {
        if (tbsX5SupportType.contains(getFileSufixStr())) {
            return true;
        }
        return false
    }

    /**
     * 检查当前文件类型是否支持app内打开
     */
    open fun isSupportAppFileType(): Boolean {
        if (appSupportImageType.contains(getFileSufixStr()) || appSupportVideoType.contains(getFileSufixStr())) {
            return true;
        }
        return false
    }

    /**
     * 检查当前文件类型是否支持图片打开
     */
    open fun isSupportAppImageType(): Boolean {
        if (appSupportImageType.contains(getFileSufixStr())) {
            return true;
        }
        return false
    }

    /**
     * 检查当前文件类型是否支持视频打开
     */
    open fun isSupportAppVideoType(): Boolean {
        if (appSupportVideoType.contains(getFileSufixStr())) {
            return true;
        }
        return false
    }


    //检查是否已下载
    open fun checkIsDownload(): Boolean {
        return try {
            var localFile = File(getLocalPath())
            localFile.exists()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }

    }

    /**
     * 检查当前文件类型是否支持视频打开
     */
    open fun getFileSufixStr(): String? {
        return fileSufixStr
    }

    open fun getFileName(): String {
        val fileMessage = message!!.messageContent as FileMessage
        return fileMessage.fileName
    }

    open fun getLocalPath(): String {
        val fileMessage = message!!.messageContent as FileMessage
        return fileMessage.localPath
    }

    open fun setLocalPath(path : String) {
        val fileMessage = message!!.messageContent as FileMessage
        fileMessage.localPath = path
    }
    open fun getOriginUrl(): String {
        val fileMessage = message!!.messageContent as FileMessage
        return fileMessage.originUrl
    }

    open fun getSize(): String {
        val fileMessage = message!!.messageContent as FileMessage
        return FileSizeUtil.FormetFileSize(fileMessage.size)
    }

    open fun getMessageType(): Int {
        var content: MessageContent? = getMessageContent(message!!)
        content = content as FileMessage
        return FileUtil.getResource(content)
    }

    open fun updateLocalPath(url: String) {
        val fileMessage = message!!.messageContent as FileMessage
        fileMessage.localPath = url
        QXIMClient.instance.updateFileLocalPath(message!!, object : QXIMClient.OperationCallback() {
            override fun onSuccess() {
                runOnUiThread {
                    ToastUtil.toast(this@ChatFileActivity, getString(R.string.qx_download_save_path, url))
                    EventBusUtil.post(message!!)
                }
            }

            override fun onFailed(error: QXError) {
            }

        })

    }


    /**
     * 处理消息内容，注意区分回复、转发等类型
     */
    fun getMessageContent(message: Message): MessageContent? {
        try {
            return when (message.messageType) {
                MessageType.TYPE_REPLY -> {
                    //回复类型
                    (message.messageContent as ReplyMessage).answer.messageContent
                }
                else -> {
                    message.messageContent
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    open fun download() {
        loading_progress.visibility = View.VISIBLE
        btn_action.visibility = View.GONE
        var length : Long = -1
        if(message != null) {
            length = (message?.messageContent as FileMessage).size
        }
        QXContext.getInstance().downloadProvider.download(QXIMKit.FileType.TYPE_FILE, length, getOriginUrl(), object :
            DownloadCallback {
            override fun onFailed(errorCode: Int, errorMsg: String?) {
                runOnUiThread {
                    updateUiState()
                    ToastUtil.toast(this@ChatFileActivity, getString(R.string.qx_download_fail, errorCode, errorCode
                        ?: ""))
                }
            }

            override fun onProgress(progress: Int) {
                runOnUiThread {
                    loading_progress.setProgress(progress)
                }
            }

            override fun onCompleted(path: String?) {
                //更新数据库local字段
                if(!TextUtils.isEmpty(path)) {
                    updateLocalPath(path!!)
                }
                runOnUiThread {
                    getLocalPath()
                    updateUiState()
                }
            }

        })
    }
}