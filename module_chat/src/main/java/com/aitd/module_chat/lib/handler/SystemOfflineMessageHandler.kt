package com.aitd.module_chat.lib.handler

import android.util.Log
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.pojo.InsertMessageResult
import com.aitd.module_chat.utils.EventBusUtil

/**
 * 系统离线消息
 */
class SystemOfflineMessageHandler : HistoryMessageUtilHandler() {
    private val TAG = "SystemOfflineMessageHan"

    override fun notifyUiUpdate(result: InsertMessageResult) {
        Log.e(TAG, "notifyUiUpdate: 更新UI")
        if (result.messages.size > 0) {
            //在此叠加消息未读数
            IMDatabaseRepository.instance.addConversationUnReadCount(
                ConversationType.TYPE_SYSTEM, result.messages[0].from, result.messages[0].to, result.newMessageCount,0)
            EventBusUtil.postSystemOfflineMessage(result.messages)
        }
    }


    override fun isNeedCheckDeleteTime(): Boolean {
        return false
    }
}