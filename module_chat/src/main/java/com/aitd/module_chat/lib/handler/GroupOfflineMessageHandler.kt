package com.aitd.module_chat.lib.handler

import android.util.Log
import com.aitd.module_chat.lib.db.IMDatabaseRepository
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.pojo.InsertMessageResult
import com.aitd.module_chat.utils.EventBusUtil

 open class GroupOfflineMessageHandler : HistoryMessageUtilHandler() {
    private val TAG = "GroupOfflineMessageHand"

    override fun notifyUiUpdate(result: InsertMessageResult) {
        Log.e(TAG, "notifyUiUpdate: 更新UI")
        if (result.messages.size > 0) {
            //在此叠加消息未读数
            IMDatabaseRepository.instance.addConversationUnReadCount(
                ConversationType.TYPE_GROUP, result.messages[0].from, result.messages[0].to, result.newMessageCount,0
            )
            EventBusUtil.postGroupOfflineMessage(result.messages)
        }
    }

    override fun isNeedCheckDeleteTime(): Boolean {
        return false
    }
}