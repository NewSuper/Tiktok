package com.aitd.module_chat.lib

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.RemoteException
import android.text.TextUtils
import com.aitd.library_common.utils.network.INetWorkMonitor
import com.aitd.library_common.utils.network.NetWorkMonitorManager
import com.aitd.library_common.utils.network.NetWorkState
import com.aitd.module_chat.*
import com.aitd.module_chat.http.BeanPubKey
import com.aitd.module_chat.http.HttpUtil
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.lib.db.entity.MessageEntity
import com.aitd.module_chat.lib.jobqueue.JobManagerUtil
import com.aitd.module_chat.listener.ConnectionStatusListener
import com.aitd.module_chat.listener.ResultCallback
import com.aitd.module_chat.netty.*
import com.aitd.module_chat.pojo.*
import com.aitd.module_chat.pojo.UserInfoCache.setToken
import com.aitd.module_chat.pojo.UserInfoCache.setUserId
import com.aitd.module_chat.pojo.event.*
import com.aitd.module_chat.push.PushType
import com.aitd.module_chat.utils.ConversationUtil
import com.aitd.module_chat.utils.SensitiveWordsUtils
import com.aitd.module_chat.utils.SharePreferencesUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.google.gson.Gson
import com.google.protobuf.GeneratedMessageV3
import com.qx.it.protos.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.jvm.Throws

class LibHandlerStub constructor(private val mContext: Context, private val mAppkey: String) :
    IHandler.Stub() {
    private val mWorkHandler: Handler
    private var mConnectStringCallback: IConnectStringCallback? = null
    private var mOnReceiveMessageListener: IOnReceiveMessageListener? = null
    private var mConversationListener: IConversationListener? = null
    private var mMessageReceiptListener: IMessageReceiptListener? = null
    private var mOnChatRoomMessageReceiveListener: IOnChatRoomMessageReceiveListener? = null
    private var mOnChatNoticeReceivedListenerList = arrayListOf<IOnChatNoticeReceivedListener>()
    private var connectionStatus: ConnectionStatusListener.Status? = null
    private var mConnectionStatusListener: IConnectionStatusListener? = null
    private var mNettyClientModel: NettyClientModel? = null
    private var isNetworkAvailable = false
    private var mCallReceiveMessageListener: ICallReceiveMessageListener? = null
    private var mRTCReceiveMessageListener: IRTCMessageListener? = null
    private var rtcConfig: RTCServerConfig? = null

    companion object {
        private val TAG = LibHandlerStub::class.java.simpleName
    }

    init {
        val handlerThread = HandlerThread("IPC_SERVICE")
        handlerThread.start()
        mWorkHandler = Handler(handlerThread.looper)
        mNettyClientModel = NettyClientModel(mContext)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        if (mContext.applicationContext is Application) {
            NetWorkMonitorManager.getInstance().init(mContext.applicationContext as Application)
            NetWorkMonitorManager.getInstance().register(this)
        }
    }

    @Throws(RemoteException::class)
    override fun connectServer(
        token: String,
        imServerUrl: String,
        callback: IConnectStringCallback?) {
        UserInfoCache.appKey = mAppkey
        HttpUtil.SERVER_URL = imServerUrl
        UserInfoCache.setToken(token)
        mConnectStringCallback = callback
        getHttpPubKey()
    }

    private fun getHttpPubKey() {
        HttpUtil.getHttpPubKey(object : HttpUtil.HttpResponseListener {
            override fun onProcess() {

            }

            override fun onSuccess(obj: Any?) {
                var result = obj as BeanPubKey
                Key.HTTP_SERVER_PUB_KEY = result.data.p
                getServerIp()
            }

            override fun onFailed(code: Int, message: String?) {
                mConnectStringCallback?.onFailure(code, message)
            }
        })
    }

    private fun getServerIp() {
        HttpUtil.getServerIp(
            mAppkey, UserInfoCache.getToken(),
            object : HttpUtil.HttpResponseListener {
                override fun onProcess() {

                }

                override fun onSuccess(obj: Any?) {
                    val result = obj as BeanResponse
                    if (obj.data != null) {
                        saveServerIp(result.data as String)
                        getTextFilter()
                        connect()
                    } else {
                        mConnectStringCallback?.onFailure(result.code, result.message)
                    }
                }

                override fun onFailed(code: Int, message: String?) {
                    mConnectStringCallback?.onFailure(
                        code,
                        if (message.isNullOrBlank()) "get server ip error" else message
                    )
                }
            })
    }

    private fun getTextFilter() {
        HttpUtil.getSensitiveWord(mAppkey, object : HttpUtil.HttpResponseListener {
            override fun onProcess() {

            }

            override fun onSuccess(obj: Any?) {
                if (obj != null) {
                    val data = obj as BeanGetSensitiveWord
                    SharePreferencesUtil.getInstance(mContext)
                        .saveSensitiveWord(Gson().toJson(data.data))
                }
            }

            override fun onFailed(code: Int, message: String?) {

            }
        })
    }

    private fun saveServerIp(serverList: String) {
        val result = serverList.split(":".toRegex()).toTypedArray()
        TcpServer.host = result[0]
        TcpServer.port = result[1].toInt()
    }

    private fun connect() {
        mNettyClientModel!!.initNettyClient(UserInfoCache.getToken())
    }











    /*******************************/


    @Throws(RemoteException::class)
    override fun disconnect() {
    }

    override fun openDebugLog() {
        QLog.openDebug()
    }

    @Throws(RemoteException::class)
    override fun setConversationNoDisturbing(conversationId: String?, isNoDisturbing: Boolean, callback: IResultCallback?) {
        var type = "cancel"
        if (isNoDisturbing) {
            type = "set"
        }
        var noDisturbing = 0
        if (isNoDisturbing) {
            noDisturbing = 1
        }
        if (IMDatabaseRepository.instance.updateConversationNoDisturbing(noDisturbing, conversationId!!) > 0) {
            val conversationEntity = IMDatabaseRepository.instance.getConversationById(conversationId)
            if (conversationEntity != null) {
                val body = S2CSpecialOperation.SpecialOperation.newBuilder().setSendType(conversationEntity.conversationType).setTargetId(conversationEntity.targetId)
                    .setUserId(UserInfoCache.getUserId()).setType(type).build()
                postOperation(SystemCmd.C2S_MESSAGE_MUTED, body, callback!!)
                return
            }
        }
        callback?.onFailed(QXError.DB_NO_ROW_FOUND.ordinal)
    }

    private fun postOperation(cmd: Short, body: GeneratedMessageV3?, callback: IResultCallback, taskId: String = "") {
        val msg = S2CSndMessage()
        msg.cmd = cmd
        if (body != null) {
            msg.body = body
        }
        post(msg, callback)
    }

    private fun post(msg: S2CSndMessage, callback: IResultCallback, taskId: String = "") {
        JobManagerUtil.instance.postMessage(msg, object : ResultCallback {

            override fun onSuccess() {
                try {
                    if (callback != null) {
                        callback?.onSuccess()
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(error: QXError) {
                try {
                    if (callback != null) {
                        callback?.onFailed(error.ordinal)
                    }
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }, taskId)
    }

    @Throws(RemoteException::class)
    override fun startCall(conversationType: String,
                           targetId: String,
                           roomId: String,
                           callType: String,
                           userIds: List<String>,
                           callback: IResultCallback) {
        val body = C2SVideoLaunch.VideoLaunch.newBuilder().setRoomId(roomId).setSendType(conversationType).setTargetId(targetId)
            .addAllUserIds(userIds).setType(callType).build()
        postOperation(SystemCmd.C2S_VIDEO_LAUNCH, body, callback)
    }

    @Throws(RemoteException::class)
    override fun acceptCall(roomId: String, callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).build()
        postOperation(SystemCmd.C2S_VIDEO_ANSWER, body, callback)
    }

    @Throws(RemoteException::class)
    override fun sendCallError(roomId: String?, targetId: String,callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).setUserId(targetId).build()
        postOperation(SystemCmd.C2S_VIDEO_ERROR, body, callback)
    }

    @Throws(RemoteException::class)
    override fun searchConversations(keyWord: String?, conversationTypes: Array<String>?, messageTypes: Array<String>?): MutableList<SearchConversationResult>? {
        return IMDatabaseRepository.instance.searchConversations(keyWord!!, conversationTypes!!, messageTypes!!).toMutableList()
    }

    @Throws(RemoteException::class)
    override fun getAllUnReadCount(region: List<String>): Int {
        var count = IMDatabaseRepository.instance.getAllUnReadCount(region)
        if (count == null) {
            count = 0
        }
        return count
    }

    @Throws(RemoteException::class)
    override fun getConversationUnReadCount(conversationId: String, isIgnoreNoDisturbing: Boolean): Int {
        var count = IMDatabaseRepository.instance.getConversationUnReadCount(conversationId, isIgnoreNoDisturbing)
        if (count == null) {
            count = 0
        }
        return count
    }

    @Throws(RemoteException::class)
    override fun checkSensitiveWord(text: String?): SensitiveWordResult? {
        return SensitiveWordsUtils.checkSensitiveWord(text, mContext)
    }

    @Throws(RemoteException::class)
    override fun registerCustomEventProvider(provider: ICustomEventProvider?): Boolean {
        if (provider != null) {
            return CustomEventManager.registerCustomEventProvider(provider)
        }
        return false
    }

    @Throws(RemoteException::class)
    override fun isMessageExist(messageId: String?): Boolean {
        if (TextUtils.isEmpty(messageId)) {
            return false;
        }
        return IMDatabaseRepository.instance.isMessageExist(messageId!!)
    }

    @Throws(RemoteException::class)
    override fun cancelCall(roomId: String, callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).build()
        postOperation(SystemCmd.C2S_VIDEO_CANCEL, body, callback)
    }

    @Throws(RemoteException::class)
    override fun getCurUserId(): String {
        return UserInfoCache.getUserId()
    }

    @Throws(RemoteException::class)
    override fun refuseCall(roomId: String, callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).build()
        postOperation(SystemCmd.C2S_VIDEO_REFUSE, body, callback)
    }

    @Throws(RemoteException::class)
    override fun hangUp(roomId: String,userId:String, callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).setUserId(userId).build()
        postOperation(SystemCmd.C2S_VIDEO_RING_OFF, body, callback)
    }

    @Throws(RemoteException::class)
    override fun setChatRoom(chatRoomId: String, name: String, value: String, autDel: Int, callback: IResultCallback) {
        val body = C2SChatroomProperty.SetProperty.newBuilder().setChatroomId(chatRoomId).setPropName(name).setPropValue(value)
            .setAutoDelete(autDel).build()
        postOperation(SystemCmd.C2S_CHATROOM_SET_PROP, body, callback)
    }

    @Throws(RemoteException::class)
    override fun delChatRoom(chatRoomId: String, name: String, callback: IResultCallback) {
        val body = C2SChatroomProperty.SetProperty.newBuilder().setChatroomId(chatRoomId).setPropName(name).build()
        postOperation(SystemCmd.C2S_CHATROOM_DEL_PROP, body, callback)
    }

    @Throws(RemoteException::class)
    override fun getChatRoom(chatRoomId: String, name: String, callback: IResultCallback) {
        val body = C2SChatroomProperty.SetProperty.newBuilder().setChatroomId(chatRoomId).setPropName(name).build()
        postOperation(SystemCmd.C2S_CHATROOM_GET_PROP, body, callback)
    }

    @Throws(RemoteException::class)
    override fun joinChatRoom(chatRoomId: String, callback: IResultCallback) {
        val body = S2CChatroomJoin.ChatroomJoin.newBuilder().setChatroomId(chatRoomId).build()
        postOperation(SystemCmd.C2S_JOIN_CHATROOM, body, callback)
    }

    @Throws(RemoteException::class)
    override fun exitChatRoom(chatRoomId: String, callback: IResultCallback) {
        val body = S2CChatroomJoin.ChatroomJoin.newBuilder().setChatroomId(chatRoomId).build()
        postOperation(SystemCmd.C2S_EXIT_CHATROOM, body, callback)
    }

    @Throws(RemoteException::class)
    override fun sendOnly(message: Message, callback: ISendMessageCallback) {
        val s2CSndMessage = MessageConvertUtil.instance.messageToS2CSndMsg(message)
        when (message.messageType) {
           MessageType.TYPE_AUDIO, MessageType.TYPE_IMAGE, MessageType.TYPE_VIDEO, MessageType.TYPE_FILE, MessageType.TYPE_GEO -> {
                updateOriginPath(message)
            }
        }
        JobManagerUtil.instance.postMessage(s2CSndMessage, object : ResultCallback {
            override fun onSuccess() {
                callback.onSuccess()
            }

            override fun onFailed(error: QXError) {
                //发送失败处理：修改消息状态为失败
                message.state = Message.State.STATE_FAILED
                message.failedReason = error.code.toString() + "-" + error.extra
                //更新数据库消息状态
                if (IMDatabaseRepository.instance.updateMessageState(message.messageId,
                        MessageEntity.State.STATE_FAILED, message.failedReason) > 0) {
                    callback.onError(error.ordinal, message)
                }
            }
        }, message.messageId)
    }

    @Throws(RemoteException::class)
    override fun saveOnly(message: Message, callback: ISendMessageCallback) {

        //过滤会话类型，只有单聊和群聊能将消息保存到数据库
        if (message.conversationType == ConversationType.TYPE_PRIVATE || message.conversationType == ConversationType.TYPE_GROUP) {
            //如果为输入状态消息，则不保存
            if (message.messageType !== MessageType.TYPE_STATUS) {
                val messageEntity = MessageConvertUtil.instance.messageToMessageEntity(message)
                saveToDB(messageEntity, callback)
            }
        } else {
            callback.onAttached(message)
        }
    }

    @Throws(RemoteException::class)
    override fun updateMessageState(messageId: String, state: Int): Int {
        return IMDatabaseRepository.instance.updateMessageState(messageId, state, "")
    }

    @Throws(RemoteException::class)
    override fun updateMessageStateAndTime(messageId: String?, timestamp: Long, state: Int): Int {
        return IMDatabaseRepository.instance.updateMessageStateAndTime(state, timestamp, messageId!!)
    }

    private fun saveToDB(messageEntity: MessageEntity, callback: ISendMessageCallback) {
        //获取消息体的json str
        GlobalScope.launch {

            var conversationEntity = ConversationModel.instance.generateConversation(messageEntity)
            //插入会话
            if (IMDatabaseRepository.instance.insertConversation(conversationEntity) > 0) {
                //更新会话草稿为空
                IMDatabaseRepository.instance.updateDraft("",
                    conversationEntity.conversationId
                )
                messageEntity.conversationId = conversationEntity.conversationId
                //重置消息发送状态为发送中
                messageEntity.state = MessageEntity.State.STATE_SENDING
                // 插入消息
                var result = IMDatabaseRepository.instance.insertMessage(messageEntity)
                if (result.messages.isNotEmpty()) {
                    //更新会话的TimeIndicator
                    IMDatabaseRepository.instance!!.updateTimeIndicator(
                        conversationEntity.conversationId, messageEntity.timestamp
                    )
                    for (msg in result.messages) {
                        var message = MessageConvertUtil.instance.convertToMessage(msg)!!
                        if (message != null) {
                            callback.onAttached(message)
                        } else {
                            callback.onError(QXError.PARAMS_INCORRECT.ordinal, message)
                        }
                    }
                }
                if (conversationEntity.isNew) {
                    ConversationModel.instance.getConversationProperty(conversationEntity!!)
                }
            }
        }
    }

    @Throws(RemoteException::class)
    override fun sendHeartBeat() {
        QLog.i(TAG, "发送心跳包")
        if (HeartBeatHolder.getInstance().isNeedSendHeatBeat) {
            HeartBeatTimeCheck.getInstance().startTimer()
        }
        var msg = S2CSndMessage()
        msg.cmd = SystemCmd.C2S_HEARTBEAT
        JobManagerUtil.instance.postHeatBeat(msg)
    }

    @Throws(RemoteException::class)
    override fun sendLogout(pushName: String, callback: IResultCallback) {
        setUserId("")
        setToken("")
        val body = C2SLogOut.LogOut.newBuilder().setManufacturer(PushType.getType(pushName).type.toString()).build()
        postOperation(SystemCmd.C2S_LOGOUT, body, callback)
    }

    @Throws(RemoteException::class)
    override fun sendMessageReadReceipt(conversationType: String, targetId: String, callback: IResultCallback) {
        QLog.i(TAG, "sendMessageReceipt 发送已读回执")

        var body =
            C2SMessageRead.MessageReadConfirm.newBuilder().setSendType(conversationType).setTargetId(targetId).build()

        GlobalScope.launch {

            //获取会话ID
            var conversationEntity = IMDatabaseRepository.instance.getConversation(
                conversationType, targetId
            )
            //根据会话id，设置当前会话下所有消息为已读状态，MessageEntity.State.STATE_READ
            if (conversationEntity != null) {
                IMDatabaseRepository.instance.updateMessageStateByConversationId(
                    conversationEntity.conversationId, MessageEntity.State.STATE_READ
                )
                //清空内存中会话的未读数量
                IMDatabaseRepository.instance.updateConversationUnReadCount(
                    conversationType, targetId, 0
                ,0)

                //如果是群组，则清空会话@信息
                if (conversationType == ConversationType.TYPE_GROUP) {
//                    IMDatabaseRepository.instance.updateConversationAtTO(targetId, "")
                }
            }
        }
        postOperation(SystemCmd.C2S_MESSAGE_READ, body, callback)

    }

    @Throws(RemoteException::class)
    override fun sendRecall(message: Message, callback: IResultCallback) {
        var messageEntity = MessageConvertUtil.instance.messageToMessageEntity(message)
        var body = C2SMessageRecall.MessageRecall.newBuilder().setSendType(messageEntity.sendType).setMessageId(
            message.messageId
        ).setTargetId(messageEntity.to).build()

        postOperation(SystemCmd.C2S_MESSAGE_RECALL, body, callback)
    }

    @Throws(RemoteException::class)
    override fun deleteLocalMessageById(messageIds: Array<String>): Int {
        return IMDatabaseRepository.instance.markMessageDelete(messageIds)
    }

    @Throws(RemoteException::class)
    override fun setConversationTop(conversationId: String?, isTop: Boolean, callback: IResultCallback?) {
        var type = if (isTop) {
            "set"
        } else {
            "cancel"
        }
        var top = if (isTop) {
            1
        } else {
            0
        }
        if (IMDatabaseRepository.instance.updateConversationTop(top, conversationId!!) > 0) {

            val conversationEntity = IMDatabaseRepository.instance.getConversationById(conversationId)
            if (conversationEntity != null) {
                //发送到服务器
                var body =
                    S2CSpecialOperation.SpecialOperation.newBuilder().setSendType(conversationEntity.conversationType)
                        .setTargetId(conversationEntity.targetId).setUserId(UserInfoCache.getUserId()).setType(type).build()
                postOperation(SystemCmd.C2S_SESSION_TOP, body, callback!!)
                return
            }
        }
        callback?.onFailed(QXError.DB_NO_ROW_FOUND.ordinal)

    }

    @Throws(RemoteException::class)
    override fun deleteRemoteMessageByMessageId(conversationType: String,
                                                targetId: String,
                                                messageIds: List<String>,
                                                callback: IResultCallback) {
        var body = C2SMessageDelete.MessageDelete.newBuilder().setSendType(conversationType).setTargetId(targetId)
            .addAllMessageIds(messageIds).build()
        postOperation(SystemCmd.C2S_MESSAGE_DEL, body, callback)
    }

    @Throws(RemoteException::class)
    override fun deleteRemoteMessageByTimestamp(conversationType: String,
                                                targetId: String,
                                                timestamp: Long,
                                                callback: IResultCallback) {
        var body = C2SMessageDelete.MessageDelete.newBuilder().setSendType(conversationType).setTargetId(targetId)
            .setTimestamp(timestamp).build()

        postOperation(SystemCmd.C2S_MESSAGE_DEL, body, callback)
    }

    @Throws(RemoteException::class)
    override fun deleteLocalMessageByTimestamp(conversationType: String, targetId: String, timestamp: Long): Int {
        return IMDatabaseRepository.instance.deleteLocalMessageByTimestamp(
            conversationType, targetId, timestamp
        )
    }

    @Throws(RemoteException::class)
    override fun getConversationProperty(conversation: Conversation, callback: IResultCallback) {
        var conversationEntity = ConversationUtil.toConversationEntity(conversation)
        ConversationModel.instance.getConversationProperty(conversationEntity)
    }

    @Throws(RemoteException::class)
    override fun updateOriginPath(message: Message): Int {
        return IMDatabaseRepository.instance.updateOriginPath(MessageConvertUtil.instance.messageToMessageEntity(message))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun rtcServerEvent(rtcEvent: RTCConfigEvent) {
        rtcConfig = rtcEvent.config
    }

    @Throws(RemoteException::class)
    override fun getRTCConfig(): RTCServerConfig? {
        return rtcConfig
    }

    @Throws(RemoteException::class)
    override fun updateAtMessageReadState(messageId: String, conversationId: String, read: Int): Int {
        return IMDatabaseRepository.instance.updateAtMessageReadState(messageId, conversationId, read)
    }

    @Throws(RemoteException::class)
    override fun clearAtMessage(conversationId: String): Int {
        return IMDatabaseRepository.instance.clearAtMessage(conversationId)
    }

    @Throws(RemoteException::class)
    override fun updateCustomMessage(conversationId: String, messageId: String, content: String, extra: String): Int {
        return IMDatabaseRepository.instance.updateCustomMessage(conversationId, messageId, content, extra)
    }

    @Throws(RemoteException::class)
    override fun updateLocalPath(message: Message): Int {
        return IMDatabaseRepository.instance.updateLocalPath(MessageConvertUtil.instance.messageToMessageEntity(message))
    }

    override fun updateHearUrl(messageId : String,  headUrl : String): Int {
        return IMDatabaseRepository.instance.updateVideoHeadUrl(messageId!!, headUrl!!)
    }

    @Throws(RemoteException::class)
    override fun deleteConversation(conversationId: String): Int {
        return IMDatabaseRepository.instance.deleteConversation(conversationId)
    }

    @Throws(RemoteException::class)
    override fun deleteConversationByTargetId(type: String, targetId: String): Int {
        return IMDatabaseRepository.instance.deleteConversation(type, targetId)
    }

    @Throws(RemoteException::class)
    override fun deleteAllConversation(): Int {
        return IMDatabaseRepository.instance.deleteAllConversation()
    }

    @Throws(RemoteException::class)
    override fun searchTextMessage(content: String): List<Message> {

        var result = IMDatabaseRepository.instance.searchTextMessageByContent(content, null)
        var list = arrayListOf<Message>()
        if (result != null) {
            for (msg in result) {
                var m = MessageConvertUtil.instance.convertToMessage(msg)
                if (m != null) {
                    list.add(m)
                }
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getHttpHost(): String {
        return HttpUtil.SERVER_URL
    }

    @Throws(RemoteException::class)
    override fun getRSAKey(): String {
        return Key.HTTP_SERVER_PUB_KEY
    }

    /**
     * 按回话搜索文本消息
     * @param content 文本内容
     * @param conversationId 会话id
     */
    @Throws(RemoteException::class)
    override fun searchTextMessageByConversationId(content: String, conversationId: String): List<Message> {
        var result = IMDatabaseRepository.instance.searchTextMessageByContent(content, conversationId)
        var list = arrayListOf<Message>()
        for (msg in result) {
            var m = MessageConvertUtil.instance.convertToMessage(msg)
            if (m != null) {
                list.add(m)
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getMessages(conversationType: String?, targetId: String?, offset: Int, pageSize: Int): MutableList<Message> {
        QLog.d(TAG, "获取本地消息： 从第$offset 条 - " + (offset + pageSize - 1) + " 记录开始"
        )

        var result = IMDatabaseRepository.instance.getMessages(
            conversationType!!, targetId!!, offset, pageSize
        )
        var list = arrayListOf<Message>()
        if (result != null) {
            for (msg in result) {
                var m = MessageConvertUtil.instance.convertToMessage(msg)
                if (m != null) {
                    list.add(m)
                }
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getMessagesByTimestamp(conversationType: String, targetId: String,
                                        timestamp: Long, searchType: Int, pageSzie: Int): List<Message> {

        var result = IMDatabaseRepository.instance.getMessagesByTimestamp(conversationType, targetId,
            timestamp, searchType, pageSzie)
        var list = arrayListOf<Message>()
        if (result != null) {
            for (msg in result) {
                var m = MessageConvertUtil.instance.convertToMessage(msg)
                if (m != null) {
                    list.add(m)
                }
            }
        }
        return list

    }

    @Throws(RemoteException::class)
    override fun getMessagesByType(conversationId: String?, types: MutableList<String>?, offset: Int, pageSize: Int, isAll: Boolean, isDesc: Boolean): MutableList<Message> {
        var result = IMDatabaseRepository.instance.getMessagesByType(
            conversationId!!, types!!, offset, pageSize, isAll, isDesc
        )
        var list = arrayListOf<Message>()
        if (result != null) {
            for (msg in result) {
                var m = MessageConvertUtil.instance.convertToMessage(msg)
                if (m != null) {
                    list.add(m)
                }
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getUnReadAtMessages(conversationId: String?): MutableList<Message>? {
        if (TextUtils.isEmpty(conversationId)) {
            return null
        }

        var result = IMDatabaseRepository.instance.getUnReadAtToMessage(conversationId!!)
        var list = arrayListOf<Message>()
        if (result != null) {
            for (msg in result) {
                if (msg != null) {
                    list.add(MessageConvertUtil.instance.convertToMessage(msg)!!)
                }
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getFirstUnReadMessage(conversationId: String): Message? {
        var result = IMDatabaseRepository.instance.getFirstUnReadMessage(conversationId)
        if (result != null) {
            return MessageConvertUtil.instance.convertToMessage(result)
        }
        return null
    }

    @Throws(RemoteException::class)
    override fun getConversation(conversationType: String, targetId: String): Conversation? {
        var result = IMDatabaseRepository.instance.getConversation(conversationType, targetId)
        return ConversationUtil.toConversation(result)
    }

    @Throws(RemoteException::class)
    override fun getAllConversation(): List<Conversation> {
        QLog.d(TAG, "getAllConversation currentThreadId"+Thread.currentThread().id)

        var result = IMDatabaseRepository.instance.getAllConversation()
        var list = arrayListOf<Conversation>()
        if (result != null) {
            for (conv in result) {
                list.add(ConversationUtil.toConversation(conv)!!)
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun getConversationInRegion(region: MutableList<String>?): MutableList<Conversation> {
        var result = IMDatabaseRepository.instance.getConversationInRegion(region!!)
        var list = arrayListOf<Conversation>()
        if (result != null) {
            for (conv in result) {
                list.add(ConversationUtil.toConversation(conv)!!)
            }
        }
        return list
    }

    @Throws(RemoteException::class)
    override fun clearMessages(conversationId: String): Int {
        return IMDatabaseRepository.instance.deleteMessageByConversationId(conversationId)
    }

    @Throws(RemoteException::class)
    override fun setReceiveMessageListener(listener: IOnReceiveMessageListener?) {
        mOnReceiveMessageListener = listener
    }

    @Throws(RemoteException::class)
    override fun setMessageReceiptListener(listener: IMessageReceiptListener?) {
        mMessageReceiptListener = listener
    }

    @Throws(RemoteException::class)
    override fun addOnChatNoticeReceivedListener(listener: IOnChatNoticeReceivedListener?) {
        mOnChatNoticeReceivedListenerList.add(listener!!)
    }

    @Throws(RemoteException::class)
    override fun setOnChatRoomMessageReceiveListener(listener: IOnChatRoomMessageReceiveListener?) {
        mOnChatRoomMessageReceiveListener = listener
    }

    @Throws(RemoteException::class)
    override fun setConversationListener(listener: IConversationListener?) {
        mConversationListener = listener
    }

    @Throws(RemoteException::class)
    override fun setConnectionStatusListener(listener: IConnectionStatusListener?) {
        mConnectionStatusListener = listener
    }

    /**
     * 更新草稿
     * @param conversationId 会话id
     * @param draft 草稿内容，当没有草稿时，请设置为空字符串：""
     */
    @Throws(RemoteException::class)
    override fun updateConversationDraft(conversationId: String, draft: String): Int {
        return IMDatabaseRepository.instance.updateDraft(draft, conversationId)
    }

    @Throws(RemoteException::class)
    override fun updateConversationTitle(type: String?, targetId: String?, title: String?): Int {
        return IMDatabaseRepository.instance.updateConversationTitle(type!!, targetId!!, title!!)
    }

    @Throws(RemoteException::class)
    override fun getServerHost(): String {
        return TcpServer.host
    }

    @Throws(RemoteException::class)
    override fun setUserProperty(property: UserProperty, callback: IResultCallback) {
        val builder = C2SUserProperty.UserProperty.newBuilder()
        if (!property.language.isNullOrEmpty()) {
            builder.language = property.language
        }
        val body = builder.build()
        postOperation(SystemCmd.C2S_USERPROPERTY, body, callback)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun handleCallMessageEvent(event: CallEvent) {
        QLog.d(TAG, " handleCallMessageEvent")
        val callMessage = CallReceiveMessage(event.cmd, event.roomId,
            event.sendType, event.targetId,
            event.type, event.userId, event.memebers)
        callMessage.param = event.data
        this.mCallReceiveMessageListener?.onReceive(callMessage)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun handleRTCSignalMessage(event: RTCEvent) {
        val signalData = RTCSignalData()
        signalData.cmd = event.cmd
        signalData.data = Gson().toJson(event.data)
        QLog.d(TAG, " handleRTCMessage $signalData")
        this.mRTCReceiveMessageListener?.onReceive(signalData)
    }

    @Throws(RemoteException::class)
    override fun setRTCSignalMessageListener(lisntener: IRTCMessageListener?) {
        this.mRTCReceiveMessageListener = lisntener
    }

    @Throws(RemoteException::class)
    override fun setCallReceiveMessageListener(listener: ICallReceiveMessageListener?) {
        this.mCallReceiveMessageListener = listener
    }

    @Throws(RemoteException::class)
    override fun switchAudioCall(roomId: String?, callback: IResultCallback) {
        val body = C2SVideoAnswer.VideoAnswer.newBuilder().setRoomId(roomId).build()
        postOperation(SystemCmd.C2S_VIDEO_SWITCH, body, callback)
    }


    /**
     * 处理消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleMessage(event: BaseEvent) {

        when (event.eventName) {
            //收到新消息
            BaseEvent.EventName.EVENT_NAME_NEW_MESSAGE -> {
                var list = MessageConvertUtil.instance.convertToMessageList((event.eventBody as List<MessageEntity>))
                mOnReceiveMessageListener?.onReceiveNewMessage(list)
            }

            //收到聊天室消息
            BaseEvent.EventName.EVENT_NAME_NEW_CHAT_ROOM_MESSAGE -> {
                var message = MessageConvertUtil.instance.convertToMessage(
                    event.eventBody as MessageEntity
                )
                mOnChatRoomMessageReceiveListener?.onReceiveNewChatRoomMessage(message)
            }

            //收到撤回消息
            BaseEvent.EventName.EVENT_NAME_RECALL_MESSAGE -> {
                var message = MessageConvertUtil.instance.convertToMessage(
                    event.eventBody as MessageEntity
                )
                mOnReceiveMessageListener?.onReceiveRecallMessage(message)
            }

            //收到正在输入状态
            BaseEvent.EventName.EVENT_NAME_INPUT_STATUS_MESSAGE -> {
                mOnReceiveMessageListener?.onReceiveInputStatusMessage(event.eventBody as String)
            }

            //消息状态改变：已送达
            BaseEvent.EventName.EVENT_NAME_MESSAGE_STATE_CHANGED -> {
                var message = MessageConvertUtil.instance.convertToMessage(
                    event.eventBody as MessageEntity
                )
                mMessageReceiptListener?.onMessageReceiptReceived(message)
            }
            //消息状态改变:已读
            BaseEvent.EventName.EVENT_NAME_MESSAGE_READ -> {
                mMessageReceiptListener?.onMessageReceiptRead()
            }
            //获取到历史消息
            BaseEvent.EventName.EVENT_NAME_HISTORY_MESSAGE -> {
                var list = MessageConvertUtil.instance.convertToMessageList((event.eventBody as List<MessageEntity>))

                mOnReceiveMessageListener?.onReceiveHistoryMessage(list)
            }
            //收到单聊离线消息
            BaseEvent.EventName.EVENT_NAME_P2P_OFFLINE_MESSAGE -> {
                var list = MessageConvertUtil.instance.convertToMessageList((event.eventBody as List<MessageEntity>))

                mOnReceiveMessageListener?.onReceiveP2POfflineMessage(list)
            }
            //收到群组离线消息
            BaseEvent.EventName.EVENT_NAME_GROUP_OFFLINE_MESSAGE -> {
                var list = MessageConvertUtil.instance.convertToMessageList((event.eventBody as List<MessageEntity>))

                mOnReceiveMessageListener?.onReceiveGroupOfflineMessage(list)
            }
            //收到系统离线消息
            BaseEvent.EventName.EVENT_NAME_SYSTEM_OFFLINE_MESSAGE -> {
                var list = MessageConvertUtil.instance.convertToMessageList((event.eventBody as List<MessageEntity>))

                mOnReceiveMessageListener?.onReceiveSystemOfflineMessage(list)
            }
            //聊天室属性获取
            BaseEvent.EventName.EVENT_NAME_CHATROOM_ATTRIBUTE -> {
                //mOnChatRoomMessageReceiveListener?.onReceiveGetAttribute(event.eventBody as HashMap<String, String>)
            }

            //聊天室销毁
            BaseEvent.EventName.EVENT_NAME_CHAT_ROOM_DESTROY -> {
                for (listener in mOnChatNoticeReceivedListenerList) {
                    listener?.onChatRoomDestroy()
                }
            }

            //聊天禁言、封禁
            BaseEvent.EventName.EVENT_NAME_CHAT_NOTICE -> {
                var notice = event.eventBody as ChatNotice
                //是否为全局操作
                if (notice.isGlobal) {
                    if (notice.sendType == ConversationType.TYPE_GROUP) {
                        //全局群组禁言
                        for (listener in mOnChatNoticeReceivedListenerList) {
                            listener?.onGroupGlobalMute(notice.isEnable())
                        }
                    } else {
                        //全局聊天室禁言
                        for (listener in mOnChatNoticeReceivedListenerList) {
                            listener?.onChatRoomGlobalMute(notice.isEnable())
                        }
                    }
                } else {
                    if (notice.isBan) {
                        //封禁
                        for (listener in mOnChatNoticeReceivedListenerList) {
                            listener?.onChatRoomBan(notice.targetId, notice.isEnable())
                        }
                    } else {
                        //禁言
                        if (notice.sendType == ConversationType.TYPE_GROUP) {
                            if (notice.isAll) {
                                //群整体禁言
                                for (listener in mOnChatNoticeReceivedListenerList) {
                                    listener?.onGroupAllMute(
                                        notice.targetId, notice.isEnable()
                                    )
                                }
                            } else {
                                //群组成员禁言
                                for (listener in mOnChatNoticeReceivedListenerList) {
                                    listener?.onGroupMute(
                                        notice.targetId, notice.isEnable()
                                    )
                                }
                            }
                        } else {
                            //聊天室禁言
                            for (listener in mOnChatNoticeReceivedListenerList) {
                                listener?.onChatRoomMute(notice.targetId, notice.isEnable())
                            }
                        }
                    }
                }
            }

            //置顶、免打扰、会话未读数
            BaseEvent.EventName.EVENT_NAME_CONVERSATION_UPDATE -> {
                var result = event.eventBody as List<Conversation>
                if (mConversationListener != null) {
                    mConversationListener?.onChanged(result)
                }
            }
        }
    }

    //TODO 待优化
    //不加注解默认监听所有的状态，方法名随意，只需要参数是一个NetWorkState即可
    @INetWorkMonitor()
    fun handleNetWorkStateChange(netWorkState: NetWorkState) {
        isNetworkAvailable = when (netWorkState) {
            NetWorkState.GPRS, NetWorkState.WIFI -> {
                true
            }
            else -> {
                false
            }
        }
        QLog.i(TAG, "onNetWorkStateChange isNetworkAvailable >>> : $isNetworkAvailable ,${netWorkState.name}")
        reconnect()// 尝试进行重连
    }

    /**
     * 处理netty lib发送过来的网络连接事件
     * priority 提高优先级 最先接收
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 1)
    fun handleConnectionStatus(event: ConnectionStatusListener.Status) {
        QLog.d(TAG, " netty 连接状态发生变更 ${event.message}")
        connectionStatus = event
        if (mConnectionStatusListener != null) {
            mConnectionStatusListener?.onChanged(event!!.value)
        }
        when (event) {
            ConnectionStatusListener.Status.CONNECTED -> {
                // 连接成功
                ReConnectManager.instance.stopReconnect()
                mConnectStringCallback?.onComplete()
            }
            ConnectionStatusListener.Status.LOGOUT, ConnectionStatusListener.Status.KICKED -> {
                closeHeartBeat()//JobManagerUtil.instance.cancelAllJob()
                disconnect()
            }
            ConnectionStatusListener.Status.NETWORK_ERROR, ConnectionStatusListener.Status.TIMEOUT, ConnectionStatusListener.Status.DISCONNECTED -> {
                closeHeartBeat()
                reconnect()
            }
        }
    }

    fun closeHeartBeat() {
        //关闭心跳检测服务
        HeartBeatHolder.getInstance().stopHeartBeatService()
        //取消心跳超时检测
        HeartBeatTimeCheck.getInstance().cancelTimer()
    }

    /**
     * 接收心跳检测请求
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleHeartBeatCheck(event: HeartBeatEvent) {
        if (event == HeartBeatEvent.EVENT_CHECK_NORMALLY) {
            if (HeartBeatHolder.getInstance().isNeedSendHeatBeat) {
                QLog.i(TAG, "tcp超过${HeartBeatHolder.getInstance().EXP_TIME}毫秒无通信，发送心跳包"
                )
                sendHeartBeat()
            }
        } else if (event == HeartBeatEvent.EVENT_CHECK_IMMEDIATELY) {
            sendHeartBeat()
        }
    }

    /**
     * 检查数据库中正在发送的消息
     */
    override fun checkSendingMessage(): List<Message>? {
        QLog.d(TAG, "checkSendingMessage currentThreadId"+Thread.currentThread().id)
        var messages = ArrayList<Message>()

        var result = IMDatabaseRepository.instance.checkSendingMessage()
        if (result.isNullOrEmpty()) {
            return messages
        }
        for (messageEntity in result) {
            var message = MessageConvertUtil.instance.convertToMessage(messageEntity)
            if (message != null) {
                //如果该消息不在消息队列中，则入列
                var isExist = JobManagerUtil.instance.isExist(message.messageId)
                if (!isExist) {
                    messages.add(message)
                }
            }
        }
        return messages
    }

    @Throws(RemoteException::class)
    override fun rtcCandidate(candidate: RTCCandidate, callback: IResultCallback) {
        val sdp = C2SVideoRtcCandidate.Candidate.newBuilder()
            .setSdp(candidate.candidate!!.sdp)
            .setSdpMid(candidate.candidate!!.sdpMid)
            .setSdpMLineIndex(candidate.candidate!!.sdpMLineIndex)
            .build()
        val builder = C2SVideoRtcCandidate.VideoRtcCandidate.newBuilder()
            .setRoomId(candidate.roomId)
            .setFrom(candidate.from)
            .setTo(candidate.to)
        builder.candidate = sdp
        val body = builder.build()
        postOperation(SystemCmd.C2S_RTC_SIGNAL_CANDIDATE, body, callback)
    }

    @Throws(RemoteException::class)
    override fun rtcJoin(join: RTCJoin, callback: IResultCallback) {
        val body = C2SVideoRtcJoin.VideoRtcJoin.newBuilder().setRoomId(join.roomId)
            .setRoomType(join.roomType)
            .build()
        postOperation(SystemCmd.C2S_RTC_SIGNAL_JOIN, body, callback)
    }

    @Throws(RemoteException::class)
    override fun rtcJoined(joined: RTCJoined, callback: IResultCallback) {
//        val body = C2SVideoRtcJoined.VideoRtcJoined.newBuilder().setRoomId(joined.roomId)
//                .addAllPeers(joined.peers)
//                .build()
//        postOperation(SystemCmd.C2S_RTC_SIGNAL_JOIN, body, callback)
    }

    @Throws(RemoteException::class)
    override fun rtcOffer(offer: RTCOffer, callback: IResultCallback) {
        val body = C2SVideoRtcOffer.VideoRtcOffer.newBuilder()
            .setRoomId(offer.roomId)
            .setFrom(offer.from)
            .setTo(offer.to)
            .setSdp(offer.sdp)
            .build()
        postOperation(SystemCmd.C2S_RTC_SIGNAL_OFFER, body, callback)
    }

    @Throws(RemoteException::class)
    override fun rtcAnswer(offer: RTCOffer, callback: IResultCallback) {
        val body = C2SVideoRtcOffer.VideoRtcOffer.newBuilder()
            .setRoomId(offer.roomId)
            .setFrom(offer.from)
            .setTo(offer.to)
            .setSdp(offer.sdp)
            .build()
        postOperation(SystemCmd.C2S_RTC_SIGNAL_ANSWER, body, callback)
    }

    @Throws(RemoteException::class)
    override fun updateConversationBackground(url: String, conversationId: String): Int {
        return IMDatabaseRepository.instance.updateConversationBackground(url, conversationId)
    }

    private fun send(message: Message) {
        sendOnly(message, object : ISendMessageCallback.Stub() {
            override fun onAttached(message: Message?) {
            }

            override fun onSuccess() {
            }

            override fun onError(errorOrdinal: Int, message: Message?) {
            }

        })
    }

    override fun rtcVideoParam(data: RTCVideoParam?, callback: IResultCallback) {
        val body = C2SVideoParam.VideoParam.newBuilder()
            .setRoomId(data?.roomId)
            .setUserId(data?.userId)
            .setParam(data?.param)
            .build()
        postOperation(SystemCmd.C2S_VIDEO_PARAM, body, callback)
    }

    private fun reconnect() {
        // 当网络切换到NONE时，会调用两次
        // 第一次netty会首先发出网络错误发起重连请求
        // 第二次系统的网络变化广播
        // 重连请求任务稍微延迟后
        // 当系统网络广播到来时会再次判断网络是否可用，不可用则会把重连事件关闭
        // 当网络切换到可用时会进行重连
        QLog.d(TAG, " reconnect 是否进行重连 userToken:${UserInfoCache.getToken()},网络状态：$isNetworkAvailable"
        )
        if (!TextUtils.isEmpty(UserInfoCache.getToken()) && isNetworkAvailable) {
            ReConnectManager.instance.startReconnect()
        } else {
            ReConnectManager.instance.stopReconnect()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleReconnect(event: ReconnectEvent) {
        mWorkHandler.post {
            getHttpPubKey()
        }
    }


}