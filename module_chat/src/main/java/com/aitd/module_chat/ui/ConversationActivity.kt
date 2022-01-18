package com.aitd.module_chat.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.aitd.module_chat.push.PushNotificationMessage
import com.aitd.module_chat.push.QXPushClient
import com.aitd.module_chat.utils.qlog.QLog
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * 推送拉起界面
 * 获取参数发送广播给第三方界面处理
 */
class ConversationActivity : AppCompatActivity() {

    private val TAG = "ConversationActivity"

    //聊天对象id，等价于to字段
    var targetId: String? = ""

    //聊天对象名称
    var targetName: String? = ""
    var conversationType: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // xiaomi vivo meizu
        targetId = intent.data?.getQueryParameter("targetId")
        targetName = intent.data?.getQueryParameter("targetName")
        var pushType = intent.data?.getQueryParameter("pushType")
        conversationType = intent.data?.lastPathSegment?.toUpperCase(Locale.US)

        // fcm huawei oppo
        if (targetId.isNullOrEmpty() && targetName.isNullOrEmpty()) {
            try {
                val json = intent.extras?.getString("qx")
                if (!TextUtils.isEmpty(json)) {
                    val jsonObject = JSONObject(json)
                    targetId = jsonObject.optString("senderId")
                    targetName = jsonObject.optString("senderId")
                    conversationType = jsonObject.optString("sendType")
                    pushType = jsonObject.optString("pushType")
                }
                QLog.e(TAG,  "push extras json:$json")
            } catch (json: JSONException) {
                json.printStackTrace()
            }
        }

        QLog.e(TAG,  "push targetId:$targetId,targetName:$targetName,pushType:$pushType, conversationType:$conversationType")
        if (targetId.isNullOrEmpty() || targetName.isNullOrEmpty() || conversationType.isNullOrEmpty()) {
            finish()
            return
        }
        val countDownTimer = object: CountDownTimer(1500,500) {
            override fun onFinish() {
                val pushNotificationMessage = PushNotificationMessage()
                pushNotificationMessage.senderId = targetId
                pushNotificationMessage.targetUserName = targetName
                pushNotificationMessage.conversationType = QXPushClient.ConversationType.setName(conversationType)
                val intent = Intent()
                intent.action = "qxim.push.intent.MESSAGE_CLICKED"
                intent.putExtra("pushType", pushType)
                intent.putExtra("message", pushNotificationMessage)
                intent.setPackage(packageName)
                QLog.e(TAG,  "push targetId:$targetId,targetName:$targetName,pushType:$pushType, conversationType:$conversationType message:$pushNotificationMessage")
                sendBroadcast(intent)
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }
        countDownTimer.start()
    }
}