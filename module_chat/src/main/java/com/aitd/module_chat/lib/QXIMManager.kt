package com.aitd.module_chat.lib

import androidx.lifecycle.MutableLiveData

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import com.aitd.library_common.utils.GlobalContextManager
import com.aitd.library_common.utils.ThreadPoolUtils
import com.aitd.module_chat.*
import com.aitd.module_chat.http.HttpCallback
import com.aitd.module_chat.lib.*
import com.aitd.module_chat.lib.QXIMKit.*
import com.aitd.module_chat.lib.boundary.FileConfig
import com.aitd.module_chat.lib.boundary.FileSizeUtil
import com.aitd.module_chat.lib.boundary.QXConfigManager
import com.aitd.module_chat.lib.menu.QXMenuConfig
import com.aitd.module_chat.lib.panel.DemoExtensionModules
import com.aitd.module_chat.lib.panel.IExtensionModule
import com.aitd.module_chat.lib.panel.QXDefaultExtensionModule
import com.aitd.module_chat.lib.panel.QXExtensionManager
import com.aitd.module_chat.lib.provider.*
import com.aitd.module_chat.listener.ConnectionStatusListener
import com.aitd.module_chat.listener.DownloadCallback
import com.aitd.module_chat.listener.UploadCallback
import com.aitd.module_chat.pojo.*
import com.aitd.module_chat.pojo.AccountConfig.Companion.getAccount
import com.aitd.module_chat.push.PushConfig
import com.aitd.module_chat.push.QXPushClient
import com.aitd.module_chat.ui.emotion.*
import com.aitd.module_chat.ui.emotion.db.Sticker
import com.aitd.module_chat.utils.OkhttpUtils
import com.aitd.module_chat.utils.qlog.QLog
import com.google.gson.Gson

import org.json.JSONObject
import java.util.concurrent.TimeUnit

class QXIMManager private constructor() {
    private val TAG = "QXIMManager"
    private lateinit var mContext: Context
    private lateinit var mUserCache: UserCache
    private var autoLoginReuslt = MutableLiveData<Boolean>()
    private var mConnectListenerList = arrayListOf<ConnectionListener>()
    private val kickedOffline: MutableLiveData<Boolean> = MutableLiveData()

    fun init(context: Context) {
        this.mContext = context.applicationContext
        initUserCache()
        initPush()
        initQXIM()
        initListener()
        initProvider()
        cacheConnect()
        initExtensionModules(mContext)
       // testRegisterCustomMessage()
       // testSystemCustomTextMessage()
        initStatusBar()
        QXIMKit.getInstance().setCustomDomainProvider {
            "https://qx-thirdpart.aitdcoin.com/" //  return "https://qx-thirdpart-beta.aitdcoin.com/"
        }
        //初始化菜单
        initMenu()
        //自定义基础参数
        QXConfigManager.initConfig(
            FileConfig.newBuilder()
                .videoShotMax(15, TimeUnit.SECONDS)     //拍摄最长时间(单位S)
                .videoShotMin(0, TimeUnit.SECONDS)      //拍摄最短时间(单位S)
                .videoMaxSize(100, FileSizeUtil.SIZETYPE_MB)      //视频最大限定(单位M)
                .imageMaxSize(10, FileSizeUtil.SIZETYPE_MB)      //图片最大限定(单位M)
                .textMsgMaxLength(4096)   //文本消息最多可以发送的字符长度
                .voiceMsgMaxDuration(60, TimeUnit.SECONDS)  //语音消息最大长度(单位S)
                .fileMsgMaxSize(100, FileSizeUtil.SIZETYPE_MB)  //文件消息大小(单位M)
                .build()
        )
        QXIMClient.instance.openDebugLog()
    }

    private fun initMenu() {
        QXMenuConfig.init()
    }

    private fun initStatusBar() {
//        QXIMKit.getInstance().setStatusBar {
//            StatusBarUtil.setStatusBarColor(it, it.resources.getColor(R.color.white))
//            StatusBarUtil.setStatusBarDarkTheme(it, true)
//        }
    }

    private fun initUserCache() {
        mUserCache = UserCache(mContext)
    }

    private fun initPush() {
        val config = PushConfig.Builder()
            .enableHWPush(true)
            .enableVivoPush(true)
            .enableFCM(true)
            .enableMiPush("2882303761518819802", "5721881952802")
            .enableOppoPush("b04056fc46be4a7a9effa29a90cfd3bf", "32cc4a863a8f4efab77539183a1bdbb2")
            .enableMeiZuPush("137137", "7cce38a2b45244a7a114a2bbd24f7eba")
            .build()
        QXPushClient.setPushConfig(config)
    }

    private fun initQXIM() {
        // QXIMKit.init(mContext, "4bccc67e99b2434096740c52e5e878bd", "https://qx-api.aitdcoin.com/")   // 生产环境
        QXIMKit.init(
            mContext,
            "c2a6758158cd4fcca12a0c91254ec84b",
            "https://qx-api-beta.aitdcoin.com/"
        )// 测试环境
    }

    private fun testRegisterCustomMessage() {
        CustomMessageManager.registerMessageProvider(RedPackMessageProvider())
    }

    private fun testSystemCustomTextMessage() {
        CustomMessageManager.registerMessageProvider(SystemTextMessageProvider())
    }

    private fun initListener() {
        /**
         * 连接状态
         */
        QXIMKit.setConnectionStatusListener(object : QXIMClient.ConnectionStatusListener {
            override fun onChanged(status: Int) {

                for (listener in mConnectListenerList) {
                    listener.onChanged(status)
                }
                when (status) {
                    QXIMClient.ConnectionStatusListener.STATUS_REMOTE_SERVICE_CONNECTED -> {
                        initCustomPushProvider()
                        cacheConnect()
                    }
                    ConnectionStatusListener.STATUS_LOGOUT -> {
                        clearUserCache()
                    }
                    ConnectionStatusListener.STATUS_KICKED -> {
                        //被其他提出时，需要返回登录界面
                        kickedOffline.postValue(true)
                        clearUserCache()
                    }
                }
                if (status == QXIMClient.ConnectionStatusListener.STATUS_LOGOUT || status == QXIMClient.ConnectionStatusListener.STATUS_DISCONNECTED) {
                    mConnectListenerList.clear()
                }
            }
        })
        /**
         * 表情事件监听
         */
        QXIMKit.setConversationStickerClickListener(object :
            QXIMKit.ConversationStickerClickListener {
            override fun addSticker(
                context: Context?,
                stickerItem: StickerItem?,
                callback: QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback?
            ) {
                // 会话界面添加表情回调
                try {
                    val sticker = Sticker.obtain(stickerItem)
                    DBHelper.getInstance().daoSession.stickerDao.save(sticker)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun managerSticker(context: Context) {
                // 表情面板点击添加回调
            }

            override fun onLongClickSticker(
                context: Context?,
                stickerItem: StickerItem?,
                callback: QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback?
            ) {

            }

            override fun delSticker(
                context: Context?,
                stickerItem: StickerItem?,
                callback: QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback?
            ) {
            }
        })

        QXIMKit.getInstance()
            .setCheckSendingMessageListener(object : QXIMClient.CheckSendingMessageListener {
                override fun onChecked(messages: List<Message>) {
                    QLog.d(TAG, "setCheckSendingMessageListener onChecked size=" + messages.size)
                    for (message in messages) {
                        if (isNeedUpload(message)) {
                            sendMediaMessage(message)
                        } else {
                            sendMessage(message)
                        }
                    }
                }
            })

        QXIMKit.setConversationClickListener(object : QXIMKit.ConversationClickListener {
            override fun onUserPortraitLongClick(
                context: Context,
                conversationType: String,
                user: QXUserInfo,
                targetId: String
            ): Boolean {

                return false
            }

            override fun onMessageLinkClick(
                context: Context,
                targetId: String,
                message: Message
            ): Boolean {
                return false
            }

            override fun onMessageLongClick(
                context: Context,
                view: View,
                message: Message
            ): Boolean {
                return false
            }

            override fun onUserPortraitClick(
                context: Context,
                conversationType: String,
                user: QXUserInfo,
                targetId: String
            ): Boolean {
                return false
            }

            override fun onMessageClick(context: Context, view: View, message: Message): Boolean {
                return false
            }
        })

        QXIMKit.getInstance().setSendMessageListener(object : QXIMKit.OnSendMessageListener {
            override fun onSend(message: Message): Message {
                QLog.d(TAG, "onSend")
                if (message.messageContent is ImageMessage) {
                    val imgMessage = message.messageContent as ImageMessage
                    if (imgMessage.extra.contains("qx_emoji")) {
                        try {
                            val emojiData = imgMessage.extra.split(":")
                            imgMessage.extra =
                                Gson().toJson(EmojiData("emoji", emojiData[1].toInt()))
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                return message
            }

            override fun onSent(message: Message?, error: String?): Boolean {
                return false
            }

        })
    }

    private fun sendMediaMessage(message: Message) {
        QXIMKit.getInstance()
            .sendMediaMessage(message, object : MediaMessageEmitter.SendMediaMessageCallback {
                override fun onProgress(progress: Int) {
                }

                override fun onUploadCompleted(message: Message?) {
                }

                override fun onUploadFailed(errorCode: Int, msg: String, message: Message?) {
                }

                override fun onAttached(message: Message?) {
                }

                override fun onSuccess() {
                }

                override fun onError(error: QXError, message: Message?) {
                }

            })
    }

    private fun sendMessage(message: Message) {
        QXIMKit.getInstance().sendMessage(message, object : QXIMClient.SendMessageCallback() {
            override fun onAttached(message: Message?) {
            }

            override fun onSuccess() {
            }

            override fun onError(error: QXError, message: Message?) {
            }
        })
    }

    private fun isNeedUpload(message: Message): Boolean {
        try {
            when (message.messageContent) {
                is GeoMessage -> {
                    var content = message.messageContent as GeoMessage
                    if (TextUtils.isEmpty(content.previewUrl)) {
                        return true
                    }
                }
                is AudioMessage -> {
                    var content = message.messageContent as AudioMessage
                    if (TextUtils.isEmpty(content.originUrl)) {
                        return true
                    }
                }
                is VideoMessage -> {
                    var content = message.messageContent as VideoMessage
                    if (TextUtils.isEmpty(content.originUrl)) {
                        return true
                    }
                }
                is ImageMessage -> {
                    var content = message.messageContent as ImageMessage
                    if (TextUtils.isEmpty(content.originUrl)) {
                        return true
                    }
                }
                is FileMessage -> {
                    var content = message.messageContent as FileMessage
                    if (TextUtils.isEmpty(content.originUrl)) {
                        return true
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 被踢监听, true 为当前为被提出状态， false 为不需要处理踢出状态
     *
     * @return
     */
    fun getKickedOffline(): LiveData<Boolean> {
        return kickedOffline
    }

    /**
     * 重置被提出状态为 false
     */
    fun resetKickedOfflineState() {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            kickedOffline.setValue(false)
        } else {
            kickedOffline.postValue(false)
        }
    }

    fun addConnectionListener(listener: ConnectionListener) {
        mConnectListenerList.add(listener)
    }

    fun clearUserCache() {
        mUserCache.saveUserCache(UserInfo(""))
    }

    fun isCacheUser(): Boolean {
        if (this::mUserCache.isInitialized){
            val userCache = mUserCache.getUserCache()
            if (userCache == null || userCache!!.loginToken.isNullOrEmpty()) {
                autoLoginReuslt.value = false
                return false
            }
        }

        return true
    }

    fun cacheConnect() {
        val userCache = mUserCache.getUserCache()
        if (userCache == null || userCache!!.loginToken.isNullOrEmpty()) {
            autoLoginReuslt.value = false
            return
        }
        UIUserInfo.userId = userCache.userId
        QXIMKit.connect(userCache!!.loginToken, object : QXIMClient.ConnectCallBack() {
            override fun onSuccess(result: String?) {
                autoLoginReuslt.value = true
            }

            override fun onError(errorCode: String?) {
                autoLoginReuslt.value = false
            }

            override fun onDatabaseOpened(code: Int) {
            }

        })
    }

    fun getAutoLoginResult(): LiveData<Boolean> {
        return autoLoginReuslt
    }

    private fun initProvider() {
        initUiEventProvider()
        initInfoProvider() //初始化用户、群组、群成员信息提供者
        initFileProvider() //初始化文件上传、下载提供者
        initFavoriteProvider()//初始化收藏提供者
        initChatBackgroundProvider()//初始化聊天背景提供者
        initSelectGroupMemberProvider()//初始化选择群成员提供者
        initSelectTargetProvider()//初始化聊天背景提供者
        initGetGroupNoticeProvider()//初始化获取群公告提供者
        initEmotionStickerProvider()// 聊天自定义表情
        initSendMessageFailedProvider()
    }

    private fun initCustomPushProvider() {
        QXIMKit.getInstance().registerCustomEventProvider(
            NewFriendEventProvider(),
            object : QXIMClient.OperationCallback() {
                override fun onSuccess() {
                    QLog.d(TAG, "注册自定义事件消息成功")
                }

                override fun onFailed(error: QXError) {
                    QLog.d(TAG, "注册自定义事件消息失败")
                }
            })
    }

    private fun initSendMessageFailedProvider() {
        QXIMKit.setSendMessageFailedProvider { errorMsg, message, errorRootView ->
            BlackListMessageTipProvider.handleView(
                errorMsg,
                message,
                errorRootView
            )
        }
    }

    private fun initUiEventProvider() {
        QXIMKit.setUIEventProvider(object : QXIMKit.QXUIEventProvider {
            override fun onChatBackClick(
                activity: Activity,
                type: String?,
                targetId: String?,
                conversationId: String?
            ) {
            }

            override fun onChatMenuClick(
                activity: Activity?,
                requestCode: Int,
                type: String?,
                targetId: String?,
                conversationId: String?
            ) {
            }

            override fun onGroupNoticeClick(
                activity: Activity?,
                targetId: String?,
                conversationId: String?,
                noticeContent: String?
            ) {
            }

            override fun onAvatarClick(
                activity: Activity?,
                friendId: String?,
                groupId: String?,
                userId: String?
            ) {

            }

        })

    }

    private fun initEmotionStickerProvider() {
        StickerManager.placeHolderId = R.mipmap.ic_launcher
        QXIMKit.setEmotionProvider(object : QXStickerProvider {
            override fun getAllSticker(
                userId: String?,
                category: String,
                callback: QXStickerProvider.QXStickerCallback?
            ) {
                val list = mutableListOf<StickerItem>()
                for (i in 0 until 20) {
                    val stickerItem = StickerItem(
                        category,
                        "$i",
                        "",
                        "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201712%2F19%2F20171219234358_VRdrH.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618380686&t=b83101b75bce0e6a35a97c5cc350af63",
                        500,
                        500,
                        i + 1
                    )
                    list.add(stickerItem)
                }
                callback?.onSticker(list)
            }

            override fun isHasSticker(originUrl: String?): Boolean {
                return DBHelper.getInstance().isHasSticker(originUrl)
            }
        })
    }

    private fun initGetGroupNoticeProvider() {
        QXIMKit.setGroupNoticeProvider { groupId, callback ->
            callback?.onSuccess(
                QXGroupNotice(
                    groupId!!,
                    "12这是测试群公告，啦啦啦~~~"
                )
            )
        }
    }

    private fun initSelectGroupMemberProvider() {
        QXIMKit.setSelectGroupMemberProvider { context, groupId ->
            // SelectFriendActivity.startActivity(context, SelectFriendActivity.GROUP_AT)
        }
    }

    private fun initSelectTargetProvider() {
        QXIMKit.setSelectTargetProvider { context ->
            // RecentConversationListActivity.startActivity(context)
        }
    }

    private fun initChatBackgroundProvider() {
        QXIMKit.setChatBackgroundProvider { type, targetId, callback ->
            if (containsTestTargetId(targetId)) {
                callback.onSuccess("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimages.china.cn%2Fattachement%2Fjpg%2Fsite1000%2F20150805%2F002564ba9eb8172bf18f1d.jpg&refer=http%3A%2F%2Fimages.china.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1613092388&t=16de40516f356c0bb73ab1f62ce9ab2a")
            }
        }
    }

    private fun containsTestTargetId(targetId: String?): Boolean {
        var ids =
            "plq001,376f0c07b3ac404db8d1dcd3c862c0e1, 4defec83abef4c4b894a797d149a66dd, 197ff8869f894a8db8934aa1fa64f919, shiyiyue1, 6fbc5c56239f4f01abc7dc210533d80d, 7c2a4c8b851940fbbb69c69e70bb9e91"
        return ids.contains(targetId!!)
    }

    private fun initFavoriteProvider() {
        QXIMKit.setFavoriteProvider(object : QXIMKit.QXFavoriteProvider {
            override fun onSave(
                favorites: List<QXFavorite>,
                callback: QXIMKit.QXFavoriteProvider.QXFavoriteCallback?
            ) {
                callback?.onSuccess()
            }

            override fun queryCollection(url: String?): CollectionItem? {
                return DBHelper.getInstance().queryCollection(url, QXIMKit.getInstance().curUserId)
            }

            override fun queryRecord(url: String?): RecordItem? {
                return DBHelper.getInstance().queryRecord(url, QXIMKit.getInstance().curUserId)
            }

            override fun insertCollection(item: CollectionItem?): Long {
                return DBHelper.getInstance().insertCollection(item)
            }

            override fun insertRecord(item: RecordItem?): Long {
                return DBHelper.getInstance().insertRecord(item)
            }
        })
    }

    private fun initFileProvider() {
        setQXUploadProvider { type, filePath, callback ->
            OkhttpUtils.upload(filePath, object : UploadCallback {
                override fun onFailed(errorCode: Int, errorMsg: String?) {
                    callback.onFailed(errorCode, errorMsg)
                }

                override fun onProgress(progress: Int) {
                    callback.onProgress(progress)
                }

                override fun onCompleted(url: String?) {
                    callback.onCompleted(url)
                }

            })
        }
        setQXDownloadProvider { type, length, url, callback ->
            OkhttpUtils.download(url, object : DownloadCallback {
                override fun onFailed(errorCode: Int, errorMsg: String?) {
                    callback.onFailed(errorCode, errorMsg)
                }

                override fun onProgress(progress: Int) {
                    callback.onProgress(progress)
                }

                override fun onCompleted(url: String?) {
                    callback.onCompleted(url)
                }

            })
        }
    }

    private fun initInfoProvider() {
        //设置用户信息提供者
        setQXUserInfoProvider(QXUserInfoProvider { userId -> // 模拟本地直接返回
            val account = getAccount(userId)
            if (account != null) {
                val qxUserInfo = QXUserInfo()
                qxUserInfo.avatarUri = Uri.parse(account!!.icon ?: "")
                qxUserInfo.name = account.name
                qxUserInfo.id = account.userId
                //此处的QXUserInfo对象需要UI层缓存，如果缓存的示例不为空，请直接返回该示例
                if (qxUserInfo != null) {
                    getInstance().refreshUserInfoCache(qxUserInfo)
                    return@QXUserInfoProvider qxUserInfo
                }
            }
            //如果为空，请访问网络获取最新的示例，
            testGetUserInfo(userId)
            null
        }, true)

        //设置群组信息提供者
        setQXGroupProvider({ groupId ->
            OkhttpUtils.getGroupInfo(groupId, object : HttpCallback<GroupInfo> {
                override fun onSuccess(t: GroupInfo) {
                    val qxGroupInfo = QXGroupInfo()
                    qxGroupInfo.name = t.groupName
                    qxGroupInfo.id = groupId
                    QXIMKit.getInstance().refreshGroupInfoCache(qxGroupInfo)
                }

                override fun onError(errorCode: Int, message: String) {

                }

            })
            null
        }, true)

        //设置群组成员信息提供者
        setQXGroupUserInfoProvider({ groupId, userId ->
            OkhttpUtils.getGroupInfo(groupId, object : HttpCallback<GroupInfo> {
                override fun onSuccess(t: GroupInfo) {
                    for (user in t.members) {
                        if (user.userId == userId) {
                            val groupUserInfo = QXGroupUserInfo()
                            groupUserInfo.groupId = groupId
                            groupUserInfo.nickName = user.name
                            groupUserInfo.userId = user.userId
                            QXIMKit.getInstance().refreshGroupUserInfoCache(groupUserInfo)
                        }
                    }
                }

                override fun onError(errorCode: Int, message: String) {

                }

            })
            null
        }, true)
    }

    private fun initExtensionModules(context: Context) {
        val moduleList: List<IExtensionModule> = QXExtensionManager.instance.getExtensionModules()
        var defaultModule: IExtensionModule? = null
        if (moduleList != null) {
            for (module in moduleList) {
                if (module is QXDefaultExtensionModule) {
                    defaultModule = module
                    break
                }
            }
            if (defaultModule != null) {
                QXExtensionManager.instance.unregisterExtensionModule(defaultModule)
            }
        }
        QXExtensionManager.instance.registerExtensionModule(DemoExtensionModules(context))
    }


    /**
     * 判断当前图片消息是否Emoji表情
     * @param message
     * @return
     */
    private fun checkImageIsEmoji(message: Message?): Boolean {
        if (message != null && message.messageContent is ImageMessage) {
            val extra = (message.messageContent as ImageMessage).extra
            val extraJsonObj: JSONObject?
            if (!TextUtils.isEmpty(extra)) {
                try {
                    extraJsonObj = JSONObject(extra)
                    if (null != extraJsonObj) {
                        val type: String = extraJsonObj.optString("type")
                        if (!TextUtils.isEmpty(type) && "emoji".equals(type)) {
                            return true
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    private fun testGetUserInfo(userId: String) {
        // 模拟请求网络
        ThreadPoolUtils.run {
            try {
                // 模拟网络请求返回数据
                val account = getAccount(userId)
                if (account != null) {
                    val qxUserInfo = QXUserInfo()
                    qxUserInfo.name = account!!.name
                    qxUserInfo.id = account.userId
                    val avatarUrl: String = if (userId == "dengweipin") {
                        "http://pic.616pic.com/ys_bnew_img/00/62/63/NrJJDoG2s3.jpg"
                    } else {
                        "http://pic.616pic.com/ys_bnew_img/00/17/39/ICYGSet6v6.jpg"
                    }
                    qxUserInfo.avatarUri = Uri.parse(avatarUrl)
                    getInstance().refreshUserInfoCache(qxUserInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private object Holder {
        val INSTANCE = QXIMManager()
    }

    companion object {
        @JvmStatic
        val instance: QXIMManager by lazy { Holder.INSTANCE }
    }


    interface ConnectionListener {
        fun onChanged(statue: Int)
    }
}