package com.aitd.module_chat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.utils.StringUtil
import com.aitd.library_common.utils.ThreadPoolUtils
import com.aitd.module_chat.*
import com.aitd.module_chat.adapter.MessageAdapter
import com.aitd.module_chat.adapter.MessagePopupWindowUtil
import com.aitd.module_chat.lib.*
import com.aitd.module_chat.lib.boundary.FileSizeUtil
import com.aitd.module_chat.lib.boundary.QXConfigManager
import com.aitd.module_chat.lib.menu.QXMenu
import com.aitd.module_chat.lib.menu.QXMenuManager
import com.aitd.module_chat.lib.menu.QXMenuType
import com.aitd.module_chat.lib.panel.*
import com.aitd.module_chat.pojo.*
import com.aitd.module_chat.push.QXNotificationInterface
import com.aitd.module_chat.rtc.RTCModuleManager
import com.aitd.module_chat.ui.chat.*
import com.aitd.module_chat.ui.image.PicturePreviewActivity.RESULT_SEND
import com.aitd.module_chat.utils.LengthFilter
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.file.*
import com.aitd.module_chat.utils.file.FileUtil.getSuffixName
import com.aitd.module_chat.utils.qlog.QLog
import com.aitd.module_chat.view.bottom.BottomMenuItem
import com.aitd.module_chat.view.RetransmissionDialog
import com.aitd.module_chat.view.bottom.BottomMenuDialog
import com.aitd.module_chat.viewholder.ChatVoiceMessageHandler
import com.alibaba.android.arouter.facade.service.ClassLoaderService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.mylhyl.acp.Acp
import com.mylhyl.acp.AcpListener
import com.mylhyl.acp.AcpOptions
import kotlinx.android.synthetic.main.activity_chat_home.*
import kotlinx.android.synthetic.main.activity_chat_home.tv_title_bar_name
import kotlinx.android.synthetic.main.imui_activity_chat.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*
import kotlinx.android.synthetic.main.imui_include_add_layout.*
import kotlinx.android.synthetic.main.imui_layout_chat_bottom.*
import kotlinx.android.synthetic.main.imui_layout_chat_multiple_operation.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import top.zibin.luban.OnCompressListener
import java.io.File
import java.lang.Exception
import java.util.LinkedHashMap

abstract class BaseChatActivity : BaseActivity(), IExtensionClickListener,
    IExtensionBottomFocusCallBack {

    private val TAG = "BaseChatActivity"
    private var backgroundUrl: String = ""
    private val TEXT_MAX_LENGTH = QXConfigManager.getQxFileConfig().textMessageMaxLength
    private var mMessageReceiptListener: QXIMClient.MessageReceiptListener? = null
    private var mMessageReceiveListener: QXIMClient.OnMessageReceiveListener? = null
    private var toBeReplyMessage: Message? = null//将要被回复的消息
    var targetId: String? = ""
    var targetName: String = ""
    var conversation: Conversation? = null
    var conversationId = ""
    var offset = 0
    var pageSize = 50
    var locateMessage: Message? = null
    lateinit var mMessageAdapter: MessageAdapter    //消息列表适配器
    lateinit var mMessageViewManager: LinearLayoutManager
    var mMessageList = arrayListOf<Message>()
    lateinit var mChatPanelAdapter: ChatPanelAdapter    //聊天面板适配器
    lateinit var mChatPanelManager: GridLayoutManager
    val FLAG_NEW_MESSAGE = 0
    val FLAG_OFFLINE_MESSAGE = 1
    val FLAG_HISTORY_MESSAGE = 2
    var conversationType: String? = ""
    var mAtToList = ArrayList<Member>()
    var mForwardList = ArrayList<TargetItem>()
    val FORWARD_TYPE_SINGLE = 0//单条转发，实则发送
    val FORWARD_TYPE_MULTI_ONE_BY_ONE = FORWARD_TYPE_SINGLE + 1//多选一条条的转发
    val FORWARD_TYPE_MULTI_COMBINE = FORWARD_TYPE_MULTI_ONE_BY_ONE + 1//多选合并转发
    var mCurrForwordType = FORWARD_TYPE_SINGLE
    var isMultiple = false
    var checkMsgList = arrayListOf<Message>()
    var tempMsgList = arrayListOf<Message>()
    lateinit var qxExtension: QXExtension

    private var kitReceiver: QXKitReceiver? = null
    var isSmoothScrollToPosition = true  // 接收对方消息，默认滚动到最新的消息

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)  //支持svg尺量图
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        if (!targetId.isNullOrEmpty()) {
            initIMListener()
            initView()
            loadConversation()
            loadData()
            handleForward()
            QXNotificationInterface.removeAllNotification(this)
            QXNotificationInterface.removeAllPushNotification(this)
        }
        kitReceiver = QXKitReceiver()
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PHONE_STATE")
        try {
            registerReceiver(this.kitReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun loadData() {
        getMessages()
        initChatBackground()
    }

    open fun initView() {
        handlePermission()//申请权限
        initViews()
        initChatUi()
        initPopWindowListener()
    }

    private fun initChatUi() {
        QXIMKit.getInstance().imuiMessageCallback = sendMessageCallback
        qxExtension = QXExtension.with(this)
        qxExtension.addOnSoftKeyBoardVisibleListener()
        qxExtension.bindContentLayout(findViewById(R.id.ll_content))
            .bindttToSendButton(findViewById(R.id.btn_send))
            .bindEditText(findViewById(R.id.edt_content))
            .bindBottomLayout(findViewById(R.id.bottom_layout))
            .bindAddLayout(findViewById(R.id.ll_add))
            .bindToAddButton(findViewById(R.id.iv_add))
            .bindEmojiLayout(findViewById(R.id.rl_emotion))
            .bindToEmojiButton(findViewById(R.id.iv_emoj))
            .bindAudioBtn(findViewById(R.id.btn_record))
            .bindAudioIv(findViewById(R.id.iv_audio))
            .bindEmojiData(this)
        qxExtension.extensionClickListener = this
        qxExtension.bottomFocusCallBack = this
        val listPlugins = mutableListOf<IPluginModule>()
        val iterator = QXExtensionManager.instance.getExtensionModules().iterator()
        while (iterator.hasNext()) {
            listPlugins.clear() //todo 修复聊天面板底部插件重复
            val extensionModule = iterator.next()
            val listPlugin = extensionModule.getPluginModules(conversationType!!)
            listPlugins.addAll(listPlugin)
        }
        qxExtension.targetId = targetId!!
        qxExtension.conversationType = conversationType!!

        mChatPanelManager = GridLayoutManager(this, 4)
        mChatPanelAdapter = ChatPanelAdapter(listPlugins, qxExtension)
        mChatPanelAdapter.setOnChatPanelItemClickListener(object :
            ChatPanelAdapter.OnChatPanelItemClickListener {
            override fun onClick(pluginmodule: IPluginModule, position: Int) {
                outSideClickCloseKeyboard()
            }
        })
        qxExtension.bindChatPanelAdapter(mChatPanelAdapter)
        recycler_view_chat_panel.apply {
            setHasFixedSize(true)
            layoutManager = mChatPanelManager
            adapter = mChatPanelAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    var off_h = DensityUtil.dip2px(this@BaseChatActivity, 15f)
                    var off_v = DensityUtil.dip2px(this@BaseChatActivity, 10f)
                    outRect.set(off_h, off_v, off_h, off_v)
                }
            })
        }

        //触摸空白区域关闭键盘
        recycler_view_message.setOnTouchListener { v, event ->
            outSideClickCloseKeyboard()
            false
        }

        btn_record.setOnFinishedRecordListener { audioPath, time ->
            var uri = Uri.fromFile(File(audioPath))
            sendMediaMessage(MessageType.TYPE_AUDIO, uri)
        }

//        btn_record.setOnFinishedRecordListener2 { audioPath, time, word, type ->
//            if (type == 1) {  //腾讯云语音使用这个
//                var uri = Uri.fromFile(File(audioPath))
//                sendMediaMessage(MessageType.TYPE_AUDIO, uri)
//            } else if (type == 2) {
//                sendTextMsg2(word)
//            }
//        }


        //输入文本长度限制
        edt_content.filters = arrayOf(LengthFilter(TEXT_MAX_LENGTH))
    }

    /**
     *     PopupWindow事件长按处理
     */
    fun initPopWindowListener() {
        MessagePopupWindowUtil.setOnItemClickListener(object :
            MessagePopupWindowUtil.OnItemClickListener {
            @SuppressLint("ServiceCast")
            override fun onMenuItemClick(menu: QXMenu, message: Message) {
                if (menu.action != null) {
                    menu.action.onAction(this@BaseChatActivity, message)
                    return
                }
                when (menu.type) {
                    QXMenuType.MENU_TYPE_COPY -> {
                        if (message.messageContent is TextMessage) {
                            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            var text = message.messageContent as TextMessage
                            val mClipData: ClipData = ClipData.newPlainText("Label", text.content)
                            cm.setPrimaryClip(mClipData)
                            ToastUtil.toast(
                                this@BaseChatActivity,
                                getString(R.string.qx_copy_success)
                            )
                        } else {
                            ToastUtil.toast(this@BaseChatActivity, getString(R.string.qx_copy_fail))
                        }
                    }

                    QXMenuType.MENU_TYPE_REPLY -> {
                        //回复
                        toBeReplyMessage = if (message.messageType == MessageType.TYPE_REPLY) {
                            var reply = message?.messageContent as ReplyMessage
                            reply.answer
                        } else {
                            message
                        }
                        var text = UserInfoUtil.getMessageSimpleText(
                            this@BaseChatActivity,
                            message,
                            message.senderUserId
                        )
                        tv_reply.text = text
                        layout_reply.visibility = View.VISIBLE
                        //如果刚才是发送录音消息，回复时需要还原底部输入模式，否则底部回复内容会不显示
                        qxExtension.resetBottomInputModel()
                    }

                    QXMenuType.MENU_TYPE_FORWARD -> {
                        //转发
                        var msg = message.clone() as Message
                        checkMsgList.clear()
                        checkMsgList.add(removeReplyMsg(msg))
                        mCurrForwordType = FORWARD_TYPE_SINGLE
                        QXContext.getInstance().selectTargetProvider?.selectTarget(this@BaseChatActivity)
                    }

                    QXMenuType.MENU_TYPE_FAVORITE -> {
                        //收藏
                        if (message.messageType == MessageType.TYPE_REPLY) {
                            var replyMessage = message.messageContent as ReplyMessage
                            saveToFavorite(listOf(replyMessage.answer))
                        } else {
                            saveToFavorite(listOf(message))
                        }
                    }

                    QXMenuType.MENU_TYPE_CHECK -> {
                        //多选
                        mMessageAdapter.setMultipleCheckable(true)
                        isMultiple = true
                        updateMultipleUI()

                        //todo 在多选之后将已勾选的内容置为false-->等同于初始化
                        for(msg in mMessageList){
                            msg.isChecked = false
                        }
                    }

                    QXMenuType.MENU_TYPE_RECALL -> {
                        //撤销
                        recallMessage(message)
                    }

                    QXMenuType.MENU_TYPE_DELETE -> {
                        //删除
                        deleteRemoteMessageByMessageId(listOf(message))
                    }
//                    MenuType.ADD_EMO -> {
//                        //添加表情
//                        val imageMessage = message.messageContent as ImageMessage
//                        ThreadPoolUtils.run {
//                            val file = GlideUtil.getCacheFile(this@BaseChatActivity, imageMessage.originUrl)
//                            if (file != null) {
//                                imageMessage.localPath = file.path
//                                runOnUiThread {
//                                    StickerManager.instance.addFavSticker(this@BaseChatActivity, imageMessage.localPath, imageMessage.originUrl, imageMessage.width, imageMessage.height)
//                                }
//                            }
//                        }
//                    }
                }
            }
        })
    }

    private fun recallMessage(message: Message) {
        QXIMClient.instance!!.sendRecall(message, object : QXIMClient.OperationCallback() {
            override fun onSuccess() {
                message.messageType = MessageType.TYPE_RECALL
                runOnUiThread(kotlinx.coroutines.Runnable {
                    if (toBeReplyMessage != null) {
                        if (toBeReplyMessage == message) {
                            toBeReplyMessage = null
                            layout_reply.visibility = View.GONE
                        }
                    }
                    mMessageAdapter.notifyDataSetChanged()
                })

            }

            override fun onFailed(error: QXError) {
                runOnUiThread {
                    Toast.makeText(
                        this@BaseChatActivity,
                        getString(R.string.qx_recall_fail, error.code, error.msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    val REQUEST_CODE_GALLREY = 0//选择相册
    val REQUEST_CODE_FILE = REQUEST_CODE_GALLREY + 1//选择文件
    val REQUEST_CODE_GEO = REQUEST_CODE_FILE + 1
    val REQUEST_CODE_CAMERA = 100
    val REQUEST_CODE_ADVANCE = REQUEST_CODE_CAMERA + 1//进入聊天详情
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    REQUEST_CODE_FILE -> {
                        handleFileResult(data)
                    }
                    REQUEST_CODE_GALLREY -> {
                        handleAlbumResult(data)
                    }
                    REQUEST_CODE_GEO -> {
                        handleGeoResult(data!!)
                    }
                    REQUEST_CODE_CAMERA -> {

                    }
                    else -> {
                        qxExtension.onActivityForResult(requestCode, resultCode, data)
                    }
                }
            }
            RESULT_SEND -> {
                // 点击拍照插件后第一次返回拍摄完的图片地址到takephotoplugin进行跳转到picturepreview
                // picturepreview处理完后返回到这里获取参数
                qxExtension.onActivityForResult(requestCode, RESULT_OK, data)
            }
        }

        if (requestCode == REQUEST_CODE_ADVANCE) {
            when (resultCode) {
                QXIMKit.RESULT_CODE_ADVANCE_CLEAR_MESSAGE -> {
                    onClearMessage()
                }
                QXIMKit.RESULT_CODE_ADVANCE_EXIT_GROUP -> {
                    onExitGroup()
                }
                else -> {
                    initChatBackground()//刷新聊天背景
                    onAdvancedBack()
                }
            }
        }
    }

    /**
     * 处理地图选择
     */
    private fun handleGeoResult(data: Intent) {
        var message = data.getParcelableExtra<Message>("geo")
        if (message == null) {
            ToastUtil.toast(this, getString(R.string.qx_error_location_data))
            return
        }
        sendMediaMessage(message)
    }

    /**
     * 处理相册选择后
     */
    private fun handleAlbumResult(data: Intent?) {
        var uriList: ArrayList<Uri> = ArrayList()
        val imageNames = data!!.clipData
        if (imageNames != null) {
            for (i in 0 until imageNames.itemCount) {
                var uri = imageNames.getItemAt(i).uri
                uriList.add(uri)
            }
        } else {
            uriList.add(data.data!!)
        }
        for (uri in uriList) {
            var file = File(FilePathUtil.getPath(this, uri))
            var messageType = MessageType.TYPE_IMAGE
            when (MediaUtil.getMediaType(getSuffixName(file.absolutePath))) {
                LocalMedia.MediaType.MEDIA_TYPE_IMAGE -> {
                    messageType = MessageType.TYPE_IMAGE
                }
                LocalMedia.MediaType.MEDIA_TYPE_VIDEO -> {
                    messageType = MessageType.TYPE_VIDEO
                }
            }
            sendMediaMessage(messageType, uri)
        }
    }

    /**
     * 处理选择文件返回结果
     */
    private fun handleFileResult(data: Intent?) {
        var fileUriList: ArrayList<Uri> = ArrayList()
        val fileName = data!!.clipData
        if (fileName != null) {
            for (i in 0 until fileName.itemCount) {
                val uri = fileName.getItemAt(i).uri
                fileUriList.add(uri)
            }
        } else {
            fileUriList.add(data.data!!)
        }
        for (index in 0 until fileUriList.size) {
            var uri = fileUriList[index]
            val file = File(FilePathUtil.getPath(this, uri))     //判断文件大小是否超过边界值
            if (file != null) {
                var fileSize = FileSizeUtil.getFileOrFilesSize(file, 3);  //当前文件大小
                Log.i("sendMediaMessage", "要发送File文件大小：" + fileSize + "M")
                //限定最大文件大小，单位M
                var maxFileSize: Double = QXConfigManager.getQxFileConfig().getFileMessageMaxSize(
                    FileSizeUtil.SIZETYPE_MB
                );
                if (fileSize > maxFileSize) {
                    //超过限定大小
                    Toast.makeText(
                        this@BaseChatActivity, StringUtil.getResourceStr(
                            this@BaseChatActivity, R.string.qx_error_file_exceeded, maxFileSize
                        ), Toast.LENGTH_LONG
                    ).show()
                } else {
                    sendMediaMessage(MessageType.TYPE_FILE, uri)
                }
            } else {
                Toast.makeText(
                    this@BaseChatActivity,
                    getString(R.string.qx_error_file_uri_not_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onImageResult(selectedMedias: LinkedHashMap<String, Int>?, origin: Boolean) {
        if (selectedMedias == null)
            return
        val iterator = selectedMedias.iterator()
        while (iterator.hasNext()) {
            val media = iterator.next()
            val mediaUri = media.key
            when (media.value) {
                1 -> {// image
                    compressImage(Uri.parse(mediaUri), origin)
                }
                3 -> {// video
                    sendMediaMessage(MessageType.TYPE_VIDEO, Uri.parse(mediaUri))
                }
            }
        }
    }

    override fun onLocationResult(data: Intent) {
        handleGeoResult(data)
    }

    override fun onFileReuslt(data: Intent) {
        handleFileResult(data)
    }

    private fun compressImage(originUri: Uri, origin: Boolean) {
//        var newUri = originUri
//        if (KitStorageUtils.isBuildAndTargetForQ(this.applicationContext)) {
//            newUri = Uri.parse("content://${originUri.toString()}")
//        }
        if (originUri.toString().contains(".gif")) {
            sendMediaMessage(MessageType.TYPE_IMAGE, originUri)
        } else {
            QLog.d(TAG, "compressImage uri:$originUri")
            BitmapUtil.compressImage(
                this,
                originUri,
                KitStorageUtils.getImageSavePath(this),
                object :
                    OnCompressListener {
                    override fun onSuccess(file: File) {
                        if (origin) {
                            sendMediaMessage(MessageType.TYPE_IMAGE, originUri)
                        } else {
                            sendMediaMessage(MessageType.TYPE_IMAGE, Uri.parse(file.path))
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onStart() {
                    }

                })
        }
    }

    fun sendGifMessage(
        localPath: String?,
        originUri: String?,
        width: Int,
        height: Int,
        index: Int
    ) {
        if (TextUtils.isEmpty(localPath)) {
            if (!TextUtils.isEmpty(originUri)) {
                ThreadPoolUtils.run {
                    val file = GlideUtil.getCacheFile(this@BaseChatActivity, originUri)
                    if (file != null) {
                        val length = if (file != null && file.exists()) file.length() else 0
                        if (length != 0L) {
                            val message = MessageCreator.instance.createImageMessage(
                                conversationType!!, QXIMClient.instance.getCurUserId()!!,
                                targetId!!, file.path, originUri
                                    ?: "", "", width, height, length, "qx_emoji:$index"
                            )
                            QXIMKit.getInstance()
                                .sendMediaMessage(message, sendMediaMessageCallback)
                        }

                    }
                }
            }

        } else {
            val file = File(localPath)
            if (file != null) {
                val length = if (file != null && file.exists()) file.length() else 0
                if (length != 0L) {
                    val message = MessageCreator.instance.createImageMessage(
                        conversationType!!, QXIMClient.instance.getCurUserId()!!,
                        targetId!!, file.path, originUri
                            ?: "", "", width, height, length, "qx_emoji:$index"
                    )
                    QXIMKit.getInstance().sendMediaMessage(message, sendMediaMessageCallback)
                }

            }
        }
    }

    abstract fun onAdvancedBack()

    abstract fun onClearMessage()

    protected open fun onExitGroup() {

    }

    abstract fun setConversationType(): String

    private fun getIntentData() {
        if (intent == null) {
            finish()
            return
        }
        targetId = intent.getStringExtra("targetId")
        if (targetId.isNullOrEmpty()) {
            targetId = intent.data?.getQueryParameter("targetId")
        }
        conversationType = setConversationType()
        if (targetId.isNullOrEmpty()) {
            finish()
            return
        }
        handleLocateMessage()
    }

    /**
     * 设置TitleBarName
     */
    open fun setTitleBarName() {
        targetName = UserInfoUtil.getTargetName(this, conversationType!!, targetId!!).toString()
        tv_title_bar_name.text = targetName
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLocateMessage()
    }

    /**
     * 用于滚动定位消息
     */
    private fun handleLocateMessage() {
        locateMessage = intent.getParcelableExtra("locateMessage")
        if (locateMessage != null) {
            getMessageByTimestamp(locateMessage!!)
        }
    }

    /**
     * 判断是否为当前聊天对象的消息
     */
    fun isCurrentTargetIdMessage(message: Message): Boolean {
        QLog.d(
            TAG,
            "isCurrentTargetIdMessage message.conversationType = " + message.conversationType + " targetId:$targetId message.targetId=" + message.targetId
        )
        when (message.conversationType) {
            ConversationType.TYPE_PRIVATE -> {
                val curUserId = QXIMClient.instance.getCurUserId()
                if ((message.senderUserId == targetId && message.targetId == curUserId) || (message.targetId == targetId && message.senderUserId == curUserId)) {
                    return true
                }
            }
            ConversationType.TYPE_SYSTEM -> {
                return conversationType == ConversationType.TYPE_SYSTEM
            }
            else -> {
                //否则为群组消息、聊天室消息、系统消息，这时需要判断to是否和targetId一样
                if (message.targetId == targetId) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 如果为发送正在输入消息状态，则不处理
     */
    fun isTargetsMessage(message: Message): Boolean {
        if (message!!.messageType != MessageType.TYPE_STATUS) {
            if (message?.conversationType == conversationType && message?.targetId == targetId) {
                return true
            }
        }
        return false
    }

    /**
     * 初始化 接收消息事件监听
     */
    open fun initIMListener() {
        mMessageReceiveListener = object : QXIMClient.OnMessageReceiveListener {
            override fun onReceiveNewMessage(message: List<Message>) {
                QLog.i(TAG, "收到新消息")
                if (message.isNullOrEmpty()) {
                    return
                }
                if (!isCurrentTargetIdMessage(message[0])) {
                    return
                }
                sendReadReceipt()//发送已读回执
                refreshListForInsert(message.toTypedArray(), FLAG_NEW_MESSAGE)
            }

            override fun onReceiveHistoryMessage(message: List<Message>) {
                QLog.i(TAG, "收到历史消息，数量：" + message.size)
                if (message.isEmpty()) {
                    return
                }
                if (!isCurrentTargetIdMessage(message[0])) {
                    return
                }
                refreshListForInsert(message.toTypedArray(), FLAG_HISTORY_MESSAGE)
            }

            override fun onReceiveP2POfflineMessage(message: List<Message>) {
                QLog.i(TAG, "收到单聊离线消息，数量：" + message.size)
                if (!isCurrentTargetIdMessage(message[0])) {
                    return
                }
                refreshListForInsert(message.toTypedArray(), FLAG_HISTORY_MESSAGE)
                sendReadReceipt()
            }

            override fun onReceiveGroupOfflineMessage(message: List<Message>) {
                QLog.i(TAG, "收到群组离线消息，数量：" + message.size)
                if (!isCurrentTargetIdMessage(message[0])) {
                    return
                }
                refreshListForInsert(message.toTypedArray(), FLAG_OFFLINE_MESSAGE)
                sendReadReceipt()
            }

            override fun onReceiveSystemOfflineMessage(message: List<Message>) {
                QLog.i(TAG, "收到系统离线消息，数量：" + message.size)
                if (!isCurrentTargetIdMessage(message[0])) {
                    return
                }
                refreshListForInsert(message.toTypedArray(), FLAG_OFFLINE_MESSAGE)
                sendReadReceipt()
            }

            override fun onReceiveRecallMessage(message: Message) {
                QLog.i(TAG, "收到撤回消息")
                try {
                    if (!isCurrentTargetIdMessage(message)) {
                        return
                    }
                    refreshListForUpdate(arrayOf(message))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onReceiveInputStatusMessage(from: String) {
                if (conversationType == ConversationType.TYPE_PRIVATE && from == targetId) {
                    tv_title_bar_name.text = getString(R.string.qx_input_ing)
                    tv_title_bar_name.postDelayed(
                        Runnable { tv_title_bar_name.text = targetName },
                        10000
                    )
                }
            }
        }
        mMessageReceiptListener = object : QXIMClient.MessageReceiptListener {
            override fun onMessageReceiptReceived(message: Message?) {
                if (message != null) {
                    if (isTargetsMessage(message)) {
                        when (message!!.state) {
                            Message.State.STATE_SENT -> {
                                QLog.i(TAG, "收到【已发送】消息回执：state=" + message!!.state)
                                refreshListForUpdate(arrayOf(message))
                            }
                            Message.State.STATE_RECEIVED -> {
                                QLog.i(TAG, "收到【已送达】消息回执：state=" + message!!.state)
                                refreshListForUpdate(arrayOf(message))
                            }
                        }
                    }
                }
            }

            override fun onMessageReceiptRead() {
                QLog.i(TAG, "收到消息【已阅读】回执 threadId=" + Thread.currentThread().id)
                //更新状态
                for (msg in mMessageList) {
                    if (msg.state != Message.State.STATE_SENDING && msg.state != Message.State.STATE_FAILED) {
                        msg.state = Message.State.STATE_READ
                    }
                }
                mMessageAdapter.notifyDataSetChanged()
            }
        }
        QXIMKit.setOnReceiveMessageListener(mMessageReceiveListener!!)
        QXIMClient.instance!!.setMessageReceiptListener(mMessageReceiptListener!!)
    }

    /**
     * 去重
     */
    open fun removeDuplicateMessage(messages: Array<Message>): ArrayList<Message> {
        var temp = arrayListOf<Message>()
        for (remote in messages) {
            if (mMessageList.contains(remote)) {
                var index = mMessageList.indexOf(remote)
                mMessageList[index] = remote
                mMessageAdapter.notifyItemChanged(index)
            } else {
                temp.add(remote)
            }
            //处理@消息已读状态，如果页面中收到的@消息，则设置为已读
            if (remote.conversationType == Conversation.Type.TYPE_GROUP) {
                if (remote.messageContent is TextMessage) {
                    var textMessage = remote.messageContent as TextMessage
                    if (textMessage != null && !textMessage.atToMessageList.isNullOrEmpty()) {  //设置已读
                        QXIMKit.getInstance().updateAtMessageReadState(
                            remote.messageId,
                            conversationId,
                            1,
                            object : QXIMClient.OperationCallback() {
                                override fun onSuccess() {

                                }

                                override fun onFailed(error: QXError) {

                                }

                            })
                    }
                }
            }
        }
        if (temp.size > 0) {
            mMessageList.addAll(temp)
            mMessageList.sort()
        }
        return temp
    }

    /**
     * 检测是否滑动到底部
     */
    private fun isVisBottom(): Boolean {
        return recycler_view_message.canScrollVertically(1)
    }

    /**
     * 刷新数据源并插DB库
     */
    open fun refreshListForInsert(messages: Array<Message>, flag: Int) {
        var isFirstTimeLoad = false
        if (mMessageList.isNullOrEmpty()) {
            isFirstTimeLoad = true
        }
        var temp = removeDuplicateMessage(messages)
        if (mMessageList.size > 0) {
            when (flag) {
                FLAG_NEW_MESSAGE -> {
                    mMessageAdapter.notifyDataSetChanged()
                    if (isSmoothScrollToPosition || !isVisBottom()) {
                        recycler_view_message.smoothScrollToPosition(mMessageList.size - 1)
                    }
                }
                FLAG_OFFLINE_MESSAGE -> {

                }
                FLAG_HISTORY_MESSAGE -> {
                    if (isFirstTimeLoad) {
                        mMessageAdapter.notifyDataSetChanged()
                        recycler_view_message.smoothScrollToPosition(mMessageList.size - 1)
                    } else {
                        mMessageAdapter.notifyDataSetChanged()
                        recycler_view_message.scrollToPosition(temp.size - 1)
                    }
                }
            }
        }
    }

    /**
     * 刷新数据源并更新
     */
    private fun refreshListForUpdate(messages: Array<Message>) {
        mMessageList.removeAll(messages)
        mMessageList.addAll(messages)
        mMessageList.sort()
        mMessageAdapter.notifyDataSetChanged()
    }

    /**
     * 刷新数据源并del
     */
    private fun refreshListForDelete(messages: List<Message>) {
        mMessageList.removeAll(messages)
        mMessageList.sort()
        mMessageAdapter.notifyDataSetChanged()
    }

    /**
     * 发送回执
     */
    fun sendReadReceipt() {
        if (conversation == null) return
        val currentUserId: String? = QXIMClient.instance.getCurUserId()
        if (TextUtils.isEmpty(currentUserId)) {
            return
        }
        QXIMClient.instance!!.sendMessageReadReceipt(
            conversation!!.conversationType,
          //  targetId!!,
            currentUserId!!,  //消息已读回执添加时间戳
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {

                }

                override fun onFailed(error: QXError) {
                    QLog.d(TAG, "发送回执失败，错误码：${error.code} 错误信息：${error.msg}")
                }
            })
    }

    /**
     * 获取本地消息
     *
     * @param offset 从第几条开始查
     * @param pageSize 每次查多少条
     */
    private fun getMessages() {
        QXIMClient.instance.getMessages(
            conversationType!!,
            targetId!!,
            offset,
            pageSize,
            object : QXIMClient.ResultCallback<List<Message>>() {
                override fun onSuccess(data: List<Message>) {
                    if (data.isNotEmpty()) {
                        var isFirstTimeLoad = false
                        if (mMessageList.isNullOrEmpty()) {
                            isFirstTimeLoad = true
                        }
                        mMessageList.addAll(data)
                        mMessageList.sort()
                        mMessageAdapter.notifyDataSetChanged()
                        if (isFirstTimeLoad) {
                            recycler_view_message.scrollToPosition(mMessageList.size - 1)
                        } else {
                            recycler_view_message.scrollToPosition(data.size - 1)
                        }
                    }
                }

                override fun onFailed(error: QXError) {

                }
            })
    }

    /**
     * 根据时间戳获取会话消息
     */
    fun getMessageByTimestamp(message: Message) {
        QXIMKit.getInstance().getMessagesByTimestamp(
            conversationType,
            targetId,
            message!!.timestamp,
            1,
            pageSize,
            object : QXIMClient.ResultCallback<List<Message>>() {
                override fun onSuccess(data: List<Message>) {
                    if (data.isNotEmpty()) {
                        var isFirstTimeLoad = false
                        if (mMessageList.isNullOrEmpty()) {
                            isFirstTimeLoad = true  //第一次加载
                        }
                        removeDuplicateMessage(data.toTypedArray())
                        mMessageAdapter.notifyDataSetChanged()
                        if (isFirstTimeLoad) {
                            recycler_view_message.smoothScrollToPosition(mMessageList.size - 1)
                        } else {
                            scrollToMessage(message) //滚动到@消息
                        }
                    }
                }

                override fun onFailed(error: QXError) {

                }
            })
    }

    /**
     * 滚动到@消息
     */
    fun scrollToMessage(message: Message) {
        if (message != null) {
            var index = mMessageList.indexOf(message)
            if (index != -1) {
                recycler_view_message.scrollToPosition(index)
            }
        }
    }

    /**
     * 检查type 属于单聊  or 群聊
     */
    fun checkType(type: String): String {
        if (type == Conversation.Type.TYPE_PRIVATE) {
            return Conversation.Type.TYPE_PRIVATE
        }
        if (type == Conversation.Type.TYPE_GROUP) {
            return Conversation.Type.TYPE_GROUP
        }
        return ""
    }

    /**
     * 监听转发  单一、一对一、合并转发
     *
     */
    private fun handleForward() {
        QXIMKit.QXFowardCallBack {
            mForwardList.clear()
            for (data in it) {
                when (mCurrForwordType) {
                    FORWARD_TYPE_SINGLE -> {
                        var msg = checkMsgList.first().clone() as Message
                        msg.targetId = data.targetId
                        if (checkType(data.type).isNotEmpty()) {
                            msg.conversationType = checkType(data.type)
                            send(msg)
                        } else {
                            ToastUtil.toast(
                                this,
                                getString(R.string.qx_error_param, "${data.type}")
                            )
                        }
                    }
                    FORWARD_TYPE_MULTI_ONE_BY_ONE -> {
                        mMessageAdapter.setMultipleCheckable(false)
                        for (index in 0 until checkMsgList.size) {
                            var msg = removeReplyMsg(checkMsgList[index].clone() as Message)
                            msg.targetId = data.targetId
                            if (checkType(data.type).isNotEmpty()) {
                                msg.conversationType = checkType(data.type)
                                send(msg)
                            } else {
                                ToastUtil.toast(
                                    this,
                                    getString(R.string.qx_error_param, "${data.type}")
                                )
                            }
                        }
                        hideMultipleView()
                    }
                    FORWARD_TYPE_MULTI_COMBINE -> {
                        mMessageAdapter.setMultipleCheckable(false)
                        var userId = arrayListOf<String>()
                        if (conversationType == Conversation.Type.TYPE_PRIVATE) {
                            userId.add(QXIMKit.getInstance().curUserId)
                            userId.add(targetId!!)
                        }
                        //传入扩展字段
                        var extra = Gson().toJson(RecordExtra(conversationType!!, userId))
                        var retransmissionMessage =
                            MessageCreator.instance.createRetransmissionMessage(
                                conversationType!!,
                                QXIMClient.instance.getCurUserId()!!,
                                data.targetId!!,
                                removeReplyMsgs(checkMsgList),
                                extra
                            )

                        if (checkType(data.type).isNotEmpty()) {
                            retransmissionMessage.conversationType = checkType(data.type)
                            send(retransmissionMessage)
                        } else {
                            ToastUtil.toast(
                                this,
                                getString(R.string.qx_error_param, "${data.type}")
                            )
                        }
                        hideMultipleView()
                    }
                }
            }
            checkMsgList.clear()
        }
    }

    /**
     * 移除回复消息
     */
    private fun removeReplyMsg(message: Message): Message {
        if (message.messageType == MessageType.TYPE_REPLY) {
            var reply = message.messageContent as ReplyMessage
            message.messageType = reply.answer.messageType
            message.messageContent = reply.answer.messageContent
        }
        return message
    }

    /**
     * 过滤回复消息
     */
    private fun removeReplyMsgs(messages: ArrayList<Message>): ArrayList<Message> {
        var list = ArrayList<Message>()
        for (msg in messages) {
            list.add(removeReplyMsg(msg))
        }
        return list
    }

    /**
     * 隐藏多选view
     */
    private fun hideMultipleView() {
        for (msg in checkMsgList) {
            msg.isChecked = false
        }
        isMultiple = false
        mMessageAdapter.setMultipleCheckable(false)
        updateMultipleButton(false)
        updateMultipleUI()
    }

    private fun updateMultipleButton(isEnabled: Boolean) {
        btn_retransmission.isEnabled = isEnabled
        btn_favorite.isEnabled = isEnabled
        btn_delete.isEnabled = isEnabled
    }

    private fun updateMultipleUI() {
        if (isMultiple) {
            layout_input_panel.visibility = View.GONE
            bottom_layout.visibility = View.GONE
            layout_multiple_operation.visibility = View.VISIBLE
        } else {
            layout_input_panel.visibility = View.VISIBLE
            layout_multiple_operation.visibility = View.GONE
        }
    }

    /**
     * 发送消息
     */
    fun sendTextMsg() {
        var text = edt_content.text.toString()
        if (text.length > TEXT_MAX_LENGTH) {
            ToastUtil.toast(
                this,
                String.format(resources.getString(R.string.qx_msg_text_max_length), TEXT_MAX_LENGTH)
            )
            return
        }
        QXIMKit.getInstance()
            .checkSensitiveWord(text, object : QXIMClient.ResultCallback<SensitiveWordResult>() {
                override fun onSuccess(result: SensitiveWordResult) {
                    //如果该消息包含被禁发的敏感词
                    if (result.isBan) {
                        ToastUtil.toast(
                            this@BaseChatActivity,
                            resources.getString(R.string.qx_chat_bottom_bar_send_tips_ban)
                        )
                        return
                    }
                    send(result.text)
                }

                override fun onFailed(error: QXError) {
                    send(text)
                }
            })
    }

    fun send(text: String) {
        if (text.isNotEmpty()) {
            var atToList = buildAtToList(text)
            var message = MessageCreator.instance.createTextMessage(
                conversationType!!,
                QXIMClient.instance.getCurUserId()!!,
                targetId!!,
                text,
                "",
                atToList
            )
            send(message)
            edt_content.setText("")
            layout_reply.visibility = View.GONE
        }
    }

    fun sendTextMsg2(text: String) {
        if (text.length > TEXT_MAX_LENGTH) {
            ToastUtil.toast(
                this,
                String.format(resources.getString(R.string.qx_msg_text_max_length), TEXT_MAX_LENGTH)
            )
            return
        }
        GlobalScope.launch {
            QXIMKit.getInstance()
                .checkSensitiveWord(
                    text,
                    object : QXIMClient.ResultCallback<SensitiveWordResult>() {
                        override fun onSuccess(result: SensitiveWordResult) {
                            btn_send.isClickable = true
                            //如果该消息包含被禁发的敏感词
                            if (result.isBan) {
                                ToastUtil.toast(
                                    this@BaseChatActivity,
                                    resources.getString(R.string.qx_chat_bottom_bar_send_tips_ban)
                                )
                                return
                            }
                            Log.e("SensitiveWordsUtils", "onSuccess: 过滤敏感词完成，开始发送")
                            send(result.text)
                        }

                        override fun onFailed(error: QXError) {
                            btn_send.isClickable = true
                            send(text)
                        }

                    })
        }

    }
    fun send(message: Message) {
        mAtToList.clear()
        var targetMessage = message
        if (message.conversationType == ConversationType.TYPE_CHAT_ROOM) {
            recycler_view_message.post {
                mMessageList.add(message)
                mMessageAdapter.notifyDataSetChanged()
                if (mMessageList.size > 0) {
                    recycler_view_message.smoothScrollToPosition(mMessageList.size - 1)
                }
            }
        }
        if (toBeReplyMessage != null) {
            //被回复消息不为空，则说明为本次发送为回复消息
            //创建回复消息体，其中包含被回复的消息和回复的消息
            var replyMessage = MessageCreator.instance.createReplyMessage(
                conversationType!!, QXIMClient.instance.getCurUserId()!!,
                targetId!!, toBeReplyMessage!!, message, ""
            )
            targetMessage = replyMessage
        }
        QXIMKit.getInstance().sendMessage(targetMessage, sendMessageCallback)
        toBeReplyMessage = null
        layout_reply.visibility = View.GONE
    }

    /**
     * 发送媒体消息
     */
    private fun sendMediaMessage(messageType: String, uri: Uri) {
        QXIMKit.getInstance().sendMediaMessage(
            this, conversationType!!, targetId!!,
            messageType, uri, sendMediaMessageCallback
        )
    }

    private fun sendMediaMessage(message: Message) {
        QXIMKit.getInstance().sendMediaMessage(message, sendMediaMessageCallback)
    }


    private fun buildAtToList(text: String): List<String> {
        //发送前先处理@逻辑，先遍历内容中的@ 是否存在在list中
        var list = arrayListOf<String>()
        for (at in mAtToList) {
            var name = "@" + at.name + " "//空格一定要加
            if (text.contains(name)) {
                list.add(at.id)
            }
        }
        //如果文字中包含@所有人，则加一个-1
        if (text.contains(resources.getString(R.string.qx_at_to_all))) {
            list.add("-1")
        }
        return list
    }

    private val sendMessageCallback = object : QXIMClient.SendMessageCallback() {
        override fun onAttached(message: Message?) {
            recycler_view_message.post {
                //如果为发送正在输入消息状态，则不处理
                if (message != null) {
                    if (isTargetsMessage(message)) {
                        updateList(message)
                    }
                }
            }
        }

        override fun onSuccess() {

        }

        override fun onError(error: QXError, message: Message?) {
            if (message != null) {
                refreshListForUpdate(arrayOf(message))
            }
            var errorMsg =
                ErrorMessageUtil.getErrorMessage(conversationType!!, error, this@BaseChatActivity)
            if (!TextUtils.isEmpty(errorMsg)) {
                ToastUtil.toast(this@BaseChatActivity, errorMsg)
            }
            QLog.e(TAG, "发送消息失败，错误码：${error.code} 错误信息：${error.msg}")
        }
    }

    private val sendMediaMessageCallback = object : MediaMessageEmitter.SendMediaMessageCallback {
        override fun onProgress(progress: Int) {
        }

        override fun onUploadCompleted(message: Message?) {
            runOnUiThread {
                if (message != null) {
                    refreshListForUpdate(arrayOf(message!!))
                }
            }
        }

        override fun onUploadFailed(errorCode: Int, msg: String, message: Message?) {
            runOnUiThread {
                mMessageAdapter.notifyDataSetChanged()
            }
        }

        override fun onAttached(message: Message?) {
            recycler_view_message.post {
                //如果为发送正在输入消息状态，则不处理
                if (message != null) {
                    if (isTargetsMessage(message)) {
                        updateList(message)
                    }
                }
            }
        }

        override fun onSuccess() {

        }

        override fun onError(error: QXError, message: Message?) {
            runOnUiThread {
                QLog.e(TAG, "发送消息失败，错误码：${error.code} 错误信息：${error.msg}")
                if (message != null) {
                    refreshListForUpdate(arrayOf(message))
                }
                var errorMsg = ErrorMessageUtil.getErrorMessage(
                    conversationType!!,
                    error,
                    this@BaseChatActivity
                )
                if (!TextUtils.isEmpty(errorMsg)) {
                    ToastUtil.toast(this@BaseChatActivity, errorMsg)
                }
            }
        }
    }

    /**
     * 发送完消息后，更新数据源，刷新列表，并滚动到最新一条消息位置
     */
    private fun updateList(message: Message) {
        if (!mMessageList.contains(message)) {
            mMessageList.add(message)
        }
        mMessageAdapter.notifyDataSetChanged()
        if (mMessageList.size > 0) {
            recycler_view_message.smoothScrollToPosition(mMessageList.size - 1)
        }
    }

    /**
     * 获取会话
     */
    private fun loadConversation() {
        if (conversationType.isNullOrEmpty() || targetId.isNullOrEmpty()) return
        QXIMClient.instance.getConversation(
            conversationType!!,
            targetId!!,
            object : QXIMClient.ResultCallback<Conversation>() {
                override fun onSuccess(data: Conversation) {
                    conversation = data
                    if (conversation != null) {
                        if (!TextUtils.isEmpty(conversation?.background)) {
                            setBackground(conversation?.background!!)
                        }
                        conversationId = conversation!!.conversationId
                        edt_content.setText(conversation!!.draft)
                        edt_content.setSelection(edt_content.text.length)
                        qxExtension.updateDraft(conversation!!.draft)
                        loadUnReadMessage()
                    }
                }

                override fun onFailed(error: QXError) {

                }
            })
    }

    /**
     * 更新草稿
     */
    open fun updateDraft() {
        var text = edt_content.text.toString().trim()
        QXIMClient.instance.updateConversationDraft(
            conversationId,
            text,
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                }

                override fun onFailed(error: QXError) {
                }

            })
    }

    private fun initViews() {
        mMessageViewManager = LinearLayoutManager(this)
        mMessageAdapter = MessageAdapter(this@BaseChatActivity, mMessageList)
        recycler_view_message.apply {
            setHasFixedSize(true)
            layoutManager = mMessageViewManager
            /****** 第一种，直接取消动画 ，  处理聊天面板弹起输入字符过多，列表抖动****/
            val animator: RecyclerView.ItemAnimator? = recycler_view_message.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            // 第二种，设置动画时间为0
            recycler_view_message.itemAnimator?.changeDuration = 0
            /***** 处理聊天面板弹起输入字符过多，列表抖动****/
            adapter = mMessageAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.set(0, 0, 0, DensityUtil.dip2px(this@BaseChatActivity, 13f))
                }
            })
        }
        mMessageAdapter.setOnItemClickListener(object : MessageAdapter.ItemListener {
            override fun onItemClick(position: Int) {
                //Item空白区域点击事件
                outSideClickCloseKeyboard()
            }

            //会话列表点击事件
            override fun onClick(position: Int, v: View) {
                handleMessageClick(mMessageList[position], v, false)
            }

            //会话列表内容长按响应事件------> //判断----》消息是否可以撤回
            override fun onLongClick(position: Int, v: View): Boolean {
                var message = mMessageList[position]
                var menuList = QXMenuManager.getInstance().getMenuList(message)
                if (menuList != null && menuList.size > 0) {
                    MessagePopupWindowUtil.show(
                        this@BaseChatActivity,
                        message,
                        menuList,
                        layout_title_bar.height.toFloat(),
                        v
                    )
                }
                return true
            }

            override fun onResend(position: Int, v: View) {
                var message = mMessageList[position]
                if (isMediaMessage(message)) {
                    var uri = getMediaMessageUri(message)
                    if (uri == null) {
                        ToastUtil.toast(
                            this@BaseChatActivity,
                            resources.getString(R.string.qx_file_uri_not_found)
                        )
                    } else {
                        sendMediaMessage(message)
                    }
                } else {
                    send(message)
                }
            }

            override fun onChecked(message: Message, isCheck: Boolean) {
                message.isChecked = isCheck
                if (isCheck) {
                    if (!checkMsgList.contains(removeReplyMsg(message))) {
                        checkMsgList.add(message)
                    }
                } else {
                    if (checkMsgList.contains(message)) {
                        checkMsgList.remove(message)
                    }
                }
                updateMultipleButton(checkMsgList.isNullOrEmpty())
            }

            override fun onAvatarClick(userId: String) {
                var provider = QXContext.getInstance().uiEventProvider
                provider?.onAvatarClick(this@BaseChatActivity, conversationType, targetId, userId)
            }

            override fun onUserPortraitLongClick(
                context: Context,
                conversationType: String,
                userInfo: QXUserInfo,
                targetId: String
            ) {
                if (userInfo != null) onAvatarLongClick(userInfo)
            }

            //回复消息UI处理
            override fun onReplyMessageClick(message: Message, v: View) {
                var replyMessage = message.messageContent as ReplyMessage
                replyMessage.origin.conversationId = message.conversationId
                handleMessageClick(replyMessage.origin, v, true)
            }
        })

        swipe_refresh_layout.setOnRefreshListener {
            offset = mMessageList.size
            loadData()
            swipe_refresh_layout.isRefreshing = false
        }

        iv_reply_cancel.setOnClickListener {
            layout_reply.visibility = View.GONE
            toBeReplyMessage = null
        }
        //发送
        btn_send.setOnClickListener {
            sendTextMsg()
        }
        //返回
        iv_back.setOnClickListener {
            onBackPressed()
        }

        //菜单
        iv_menu.setOnClickListener {
            var provider = QXContext.getInstance().uiEventProvider
            provider?.onChatMenuClick(
                this@BaseChatActivity,
                REQUEST_CODE_ADVANCE,
                conversationType,
                targetId,
                conversationId
            )
        }

        //多选栏 、转发
        btn_retransmission.setOnClickListener {
            showFowardDialog()
        }
        //收藏
        btn_favorite.setOnClickListener {
            saveToFavorite(checkMsgList)
        }
        //删除
        btn_delete.setOnClickListener {
            confirmDeleteDialog()
        }
    }

    private fun confirmDeleteDialog() {
        val menuItemList: MutableList<BottomMenuItem> = java.util.ArrayList<BottomMenuItem>()
        menuItemList.add(
            BottomMenuItem(
                1,
                StringUtil.getResourceStr(this, R.string.qx_msg_pop_delete)
            )
        )
        val bottomMenuDialog = BottomMenuDialog()
        bottomMenuDialog.setTitleText(
            StringUtil.getResourceStr(
                this,
                R.string.chat_confirm_dialog_title
            )
        )
        bottomMenuDialog.setMenuColor(R.color.delete_confirm_menu_color)
        bottomMenuDialog.setBottomMenuList(this, menuItemList)
        bottomMenuDialog.setOnMenuItemClickListener { itemId, ob ->
            when (itemId) {
                1 -> {
                    hideMultipleView()
                    deleteRemoteMessageByMessageId(checkMsgList)
                }
            }
        }
        bottomMenuDialog.show(supportFragmentManager, BottomMenuDialog::class.java.getSimpleName())
    }

    private fun saveToFavorite(list: List<Message>) {
        var provider = QXContext.getInstance().favoriteProvider
        if (provider == null) {
            ToastUtil.toast(
                this@BaseChatActivity,
                getString(R.string.qx_provider_collect_not_implement)
            )
            return
        }
        var favorites = ConvertUtil.convertToFavorite(list)
        provider.onSave(favorites, object : QXIMKit.QXFavoriteProvider.QXFavoriteCallback {
            override fun onSuccess() {
                ToastUtil.toast(this@BaseChatActivity, getString(R.string.qx_collect_success))
            }

            override fun onFailed(code: Int, msg: String?) {
                ToastUtil.toast(this@BaseChatActivity, getString(R.string.qx_collect_fail))
            }
        })
        checkMsgList.clear()
        hideMultipleView()
    }

    /**
     * 处理消息点击事件
     */
    private fun handleMessageClick(message: Message, v: View, isReply: Boolean) {
        when (message.messageType) {
            MessageType.TYPE_REPLY -> {
                //被回复文本消息->查看文字
                var replyMessage = message.messageContent as ReplyMessage
                if (replyMessage.answer.messageContent is TextMessage) {
                    var textMessage = replyMessage.answer
                    var intent = Intent(this, ChatTextActivity::class.java)
                    intent.putExtra("message", textMessage)
                    startActivity(intent)
                }
            }
            MessageType.TYPE_TEXT -> {
                //查看文字
                if (message.messageContent is TextMessage) {
                    var intent = Intent(this, ChatTextActivity::class.java)
                    intent.putExtra("message", message)
                    startActivity(intent)
                }
            }

            MessageType.TYPE_RECORD -> {
                var intent = Intent(this, ChatRecordActivity::class.java)
                intent.putExtra("message", message)
                startActivity(intent)
            }
            MessageType.TYPE_GEO -> {
                LocationDetailActivity.startActivity(this, message)
            }
            MessageType.TYPE_IMAGE -> {
                val imageMsg = message.messageContent as ImageMessage
                val extra = imageMsg.extra
                if (!extra.isNullOrEmpty()) {
                    val jsonObjec = JSONObject(extra)
                    val type = jsonObjec.optString("type")
                    if (!type.isNullOrEmpty() && type == "emoji") {
                        ChatGifActivity.startActivity(this, message)
                    }
                } else {
                    ImagePageActivity.startActivity(this, message)
                }
            }
            MessageType.TYPE_IMAGE_AND_TEXT -> {
                var uri = Uri.parse(
                    (message.messageContent as ImageTextMessage)!!.redirectUrl
                )
                var intent = Intent(Intent.ACTION_VIEW, uri)
                intent.data = uri
                startActivity(intent)
            }
            MessageType.TYPE_FILE -> {
                ChatFileActivity.startActivity(this, message)
            }
            MessageType.TYPE_AUDIO -> {
//                if (isReply) {
//                    //回复语音消息二级页面播放语音
//                    var intent = Intent(this, ChatVoiceActivity::class.java)
//                    intent.putExtra("message", message)
//                    startActivity(intent)
//                } else {
//                    var voiceMessageHandler: ChatVoiceMessageHandler.ViewHolder? = null
//                    if (v.tag is ChatVoiceMessageHandler.ViewHolder) {
//                        voiceMessageHandler = v.tag as ChatVoiceMessageHandler.ViewHolder
//                    }
//                    val voiceMessage = message.messageContent as AudioMessage
//                    if (AudioPlayManager.getInstance().isPlaying) {
//                        if (AudioPlayManager.getInstance().playingUri != Uri.parse(voiceMessage.localPath)) {
//                            AudioPlayManager.getInstance().stopPlay()
//                            AudioPlayManager.getInstance().startPlay(
//                                this, Uri.parse(voiceMessage.localPath),
//                                ChatVoiceMessageHandler().VoiceMessagePlayListener(
//                                    this, message, voiceMessageHandler!!))return
//                        }
//                        AudioPlayManager.getInstance().stopPlay()
//                    }
//                    if (!AudioPlayManager.getInstance()
//                            .isInNormalMode(this) && AudioPlayManager.getInstance().isInVOIPMode(this)
//                    ) {
//                        ToastUtil.toast(this, getString(R.string.qx_voip_occupying))
//                    } else {
//                        if (voiceMessageHandler != null) {
//                            AudioPlayManager.getInstance().startPlay(
//                                this, Uri.parse(voiceMessage.localPath),
//                                ChatVoiceMessageHandler().VoiceMessagePlayListener(this, message, voiceMessageHandler!!)) } } }

                //修复会话点击语音2次不停止，且重新播放
                if (isReply) {
                    //回复语音消息二级页面播放语音
                    var intent = Intent(this, ChatVoiceActivity::class.java)
                    intent.putExtra("message", message)
                    startActivity(intent)
                } else {
                    var voiceMessageHandler: ChatVoiceMessageHandler.ViewHolder? = null
                    if (v.tag is ChatVoiceMessageHandler.ViewHolder) {
                        voiceMessageHandler = v.tag as ChatVoiceMessageHandler.ViewHolder
                    }
                    val voiceMessage = message.messageContent as AudioMessage
                    if (!AudioPlayManager.getInstance()
                            .isInNormalMode(this) && AudioPlayManager.getInstance()
                            .isInVOIPMode(this)
                    ) {
                        ToastUtil.toast(this, getString(R.string.qx_voip_occupying))
                    } else {
                        //如果正在播放中,点击就使其暂停
                        if (AudioPlayManager.getInstance().isPlaying) {
                            //如果当前播放的不是当前点击的语音，使其播放后return.再停止播放
                            if (AudioPlayManager.getInstance().playingUri != Uri.parse(voiceMessage.localPath)) {
                                AudioPlayManager.getInstance().startPlay(
                                    this, Uri.parse(voiceMessage.localPath),
                                    ChatVoiceMessageHandler().VoiceMessagePlayListener(
                                        this,
                                        message,
                                        voiceMessageHandler!!
                                    )
                                )
                                return
                            }
                            AudioPlayManager.getInstance().stopPlay()
                        } else {
                            //没有播放，就使其播放
                            AudioPlayManager.getInstance().startPlay(
                                this, Uri.parse(voiceMessage.localPath),
                                ChatVoiceMessageHandler().VoiceMessagePlayListener(
                                    this,
                                    message,
                                    voiceMessageHandler!!
                                )
                            )
                        }
                    }
                }
            }
            MessageType.TYPE_VIDEO -> {
                VideoPlayActivity.startActivity(this, message)
            }
            MessageType.TYPE_AUDIO_CALL -> {
                RTCModuleManager.INSTANCE.onClick(this, conversationType!!, targetId!!, 0)
            }

            MessageType.TYPE_VIDEO_CALL -> {
                RTCModuleManager.INSTANCE.onClick(this, conversationType!!, targetId!!, 1)
            }
        }
    }

    private fun showFowardDialog() {
        var dialog = RetransmissionDialog(this@BaseChatActivity)
        dialog.setOnButtonClickListener(object : RetransmissionDialog.OnButtonClickListener {
            override fun onOneByOne() {
                mCurrForwordType = FORWARD_TYPE_MULTI_ONE_BY_ONE
                QXContext.getInstance().selectTargetProvider?.selectTarget(this@BaseChatActivity)
            }

            override fun onCombine() {
                mCurrForwordType = FORWARD_TYPE_MULTI_COMBINE
                QXContext.getInstance().selectTargetProvider?.selectTarget(this@BaseChatActivity)
            }
        })
        dialog.show()
    }

    /**
     * 关闭底部面板和软键盘
     */
    fun outSideClickCloseKeyboard() {
        if (qxExtension != null) qxExtension.outSideClickCloseKeyboard()
    }

    open fun onAvatarLongClick(userInfo: QXUserInfo) {

    }

    /**
     * 消息已显示
     */
    open fun messageDisplay(message: Message) {

    }

    private fun isMediaMessage(message: Message): Boolean {
        return message.messageType == MessageType.TYPE_FILE || message.messageType == MessageType.TYPE_VIDEO
                || message.messageType == MessageType.TYPE_IMAGE || message.messageType == MessageType.TYPE_AUDIO
                || message.messageType == MessageType.TYPE_GEO
    }

    private fun getMediaMessageUri(message: Message): Uri? {
        if (message.messageContent == null) {
            return null
        }
        when (message.messageType) {
            MessageType.TYPE_IMAGE -> {
                if (message.messageContent is ImageMessage) {
                    var path = (message.messageContent as ImageMessage).localPath
                    return parseUri(path)
                }
            }
            MessageType.TYPE_AUDIO -> {
                if (message.messageContent is AudioMessage) {
                    var path = (message.messageContent as AudioMessage).localPath
                    return parseUri(path)
                }
            }
            MessageType.TYPE_VIDEO -> {
                if (message.messageContent is VideoMessage) {
                    var path = (message.messageContent as VideoMessage).localPath
                    return parseUri(path)
                }
            }
            MessageType.TYPE_FILE -> {
                if (message.messageContent is FileMessage) {
                    var path = (message.messageContent as FileMessage).localPath
                    return parseUri(path)
                }
            }
            MessageType.TYPE_GEO -> {
                if (message.messageContent is GeoMessage) {
                    var path = (message.messageContent as GeoMessage).localPath
                    return parseUri(path)
                }
            }
        }
        return null
    }

    private fun parseUri(path: String?): Uri? {
        return if (TextUtils.isEmpty(path)) {
            null
        } else {
            Uri.parse(path)
        }
    }

    /**
     * 背景图
     */
    private fun initChatBackground() {
        var provider = QXContext.getInstance().chatBackgroundProvider
        if (provider == null) ToastUtil.toast(
            this@BaseChatActivity,
            getString(R.string.qx_provider_chat_bg_not_implement)
        )
        var type = when (conversationType) {
            Conversation.Type.TYPE_GROUP -> {
                1
            }
            Conversation.Type.TYPE_PRIVATE -> {
                0
            }
            else -> {
                -1
            }
        }
        provider.getBackground(
            type,
            targetId,
            object : QXIMKit.QXChatBackgroundProvider.QXChatBackgroundCallback {
                override fun onSuccess(imgUrl: String?) {
                    backgroundUrl = imgUrl!!
                    saveBackground(backgroundUrl)
                    setBackground(imgUrl)
                }

                override fun onFailed(code: Int, msg: String?) {
                }
            })
    }

    /**
     * 设置DecorView背景图片
     */
    private fun setBackground(path: String) {
        val myOptions: RequestOptions = RequestOptions();
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        myOptions.override(width, height)
        QLog.d(TAG, "setBackground: $path")
        Glide.with(this).load(path)
            .centerCrop()
            .into(imui_chat_iv_background)
//        Glide.with(this@BaseChatActivity)
//                .asBitmap()
//                .load(path)
//                .centerCrop()
//                .apply(myOptions)
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
//                        QLog.d(TAG,"setBackground onResourceReady")
//                        val bitmap: BitmapDrawable = BitmapDrawable(this@BaseChatActivity.resources, bitmap)
//                        if (bitmap != null) {
//                            imui_chat_iv_background?.setBackgroundDrawable(bitmap)
//                        }
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        QLog.d(TAG,"setBackground onLoadCleared")
//                    }
//
//                    override fun onLoadFailed(errorDrawable: Drawable?) {
//                        super.onLoadFailed(errorDrawable)
//                        QLog.d(TAG,"setBackground onLoadFailed")
//                    }
//                })
    }

    /**
     * 更新当前用户，会话，所设置的背景
     */
    private fun saveBackground(backgroundUrl: String) {
        if (!TextUtils.isEmpty(conversationId)) {
            QXIMKit.getInstance().updateConversationBackground(
                conversationId,
                backgroundUrl,
                object : QXIMClient.OperationCallback() {
                    override fun onSuccess() {

                    }

                    override fun onFailed(error: QXError) {

                    }
                })
        }
    }

    /**
     * 获取会话未读消息数量成功后---->>发送消息回执
     */
    open fun loadUnReadAtToMessage() {
        sendReadReceipt()
    }

    //相册 拍照权限
    fun handlePermission() {
        Acp.getInstance(this@BaseChatActivity).request(
            AcpOptions.Builder().setPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
            ).setRationalMessage(resources.getString(R.string.qx_permission_request))
                .setRationalBtn(resources.getString(R.string.qx_permission_ok))
                .setDeniedCloseBtn(resources.getString(R.string.qx_permission_close))
                .setDeniedSettingBtn(resources.getString(R.string.qx_permission_setting))
                .setDeniedMessage(resources.getString(R.string.qx_permission_record_camera))
                .build(),
            object : AcpListener {
                override fun onGranted() {

                }

                override fun onDenied(permissions: List<String>) {}
            })
    }

    /**
     * 删除服务器端消息
     */
    private fun deleteRemoteMessageByMessageId(messages: List<Message>) {
        var messageIds = ArrayList<String>()
        for (element in messages) {
            messageIds.add(element.messageId)
        }
        QXIMClient.instance!!.deleteRemoteMessageByMessageId(conversationType!!,
            targetId!!,
            messageIds,
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                    deleteLocalMessage(messages) //删除远程消息成功后，删除本地消息
                }

                override fun onFailed(error: QXError) {
                    checkMsgList.clear()
                    Toast.makeText(
                        this@BaseChatActivity,
                        getString(R.string.qx_delete_fail),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * 删除本地消息
     */
    private fun deleteLocalMessage(messages: List<Message>) {
        var messageIds = ArrayList<String>()
        for (element in messages) {
            messageIds.add(element.messageId)
        }
        QXIMClient.instance!!.deleteLocalMessageById(
            messageIds.toTypedArray(),
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                    Toast.makeText(
                        this@BaseChatActivity,
                        getString(R.string.qx_delete_success),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.i("DeleteMessage", "本地消息删除成功");

                    refreshListForDelete(messages)
                    checkMsgList.clear()
                }

                override fun onFailed(error: QXError) {
                    Log.i("DeleteMessage", "本地消息删除失败：" + error.msg);
                    checkMsgList.clear()
                    Toast.makeText(
                        this@BaseChatActivity,
                        getString(R.string.qx_delete_fail),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * 监听底部获取焦点
     */
    override fun onBottomFocusCallBack() {
        recycler_view_message.postDelayed(kotlinx.coroutines.Runnable {
            if (mMessageAdapter != null && mMessageAdapter.itemCount > 0) {
                if (!recycler_view_message.canScrollVertically(1)) {
                    return@Runnable
                }
                var index = mMessageAdapter.itemCount - 1
                if (index > -1) {
                    recycler_view_message.smoothScrollToPosition(index)
                }
            }
        }, 200)
    }

    /**
     * 检测禁言缓存
     */
    fun updateInputViewByMute(tips: String, isMute: Boolean) {
        if (tips.isNotEmpty()) {
            tv_mute.text = tips
        }
        tv_mute.visibility = if (isMute) {
            View.VISIBLE
        } else {
            View.GONE
        }
        iv_audio.isEnabled = !isMute
        iv_emoj.isEnabled = !isMute
        iv_add.isEnabled = !isMute
        btn_record.isEnabled = !isMute
        edt_content.isEnabled = !isMute
        btn_send.isEnabled = !isMute

        if (isMute) {
            layout_reply.visibility = View.GONE
        }
    }

    // 异步返回用户相关信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventUserUpdate(userinfo: QXUserInfo) {
        if (userinfo != null) {
            recycler_view_message.post {
                mMessageAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventGroupUserUpdate(userinfo: QXGroupUserInfo) {
        if (userinfo != null) {
            recycler_view_message.post {
                mMessageAdapter.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMessage(message: Message) {
        refreshListForInsert(arrayOf(message), FLAG_NEW_MESSAGE)
    }

    override fun onResume() {
        super.onResume()
        setTitleBarName()
        checkAndroid11Permission()
    }

    /**
     * 检查Android 11所需权限
     */
    private fun checkAndroid11Permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                var isManager = Environment.isExternalStorageManager()
                if (!isManager) {
                    ToastUtil.toast(
                        this, String.format(
                            resources.getString(R.string.qx_permission_manage_all_file),
                            LibStorageUtils.getAppName(this)
                        )
                    )
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        AudioPlayManager.getInstance().stopPlay()
    }

    override fun onDestroy() {
        QXIMKit.setOnReceiveMessageListener(null)
        QXIMClient.instance.removeMessageReceiptListener()
        MediaMessageEmitter.removeMediaMessageCallback()
        QXIMKit.removeCallBack()
        if (kitReceiver != null) {
            try {
                unregisterReceiver(kitReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }

    open fun loadUnReadMessage() {

    }
}