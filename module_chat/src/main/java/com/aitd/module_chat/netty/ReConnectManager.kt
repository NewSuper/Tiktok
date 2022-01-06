package com.aitd.module_chat.netty

import com.aitd.module_chat.pojo.event.ReconnectEvent
import com.aitd.module_chat.utils.EventBusUtil
import com.aitd.module_chat.utils.qlog.QLog
import java.util.*

/**
 * 重连定时任务
 */
class ReConnectManager private constructor() {

    private var timer: Timer? = null
    private var task: TimerTask? = null

    @Volatile
    private  var reconnectCount = 0

    companion object {

        private const val RECOONECT_MAX = 5
        private const val RECOONECT_TIME_DELAY = 500L
        private const val RECOONECT_TIME_PERIOD = 5 * 1000L

        @JvmStatic
        val instance: ReConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ReConnectManager()
        }
    }

    @Synchronized
    fun startReconnect() {
        if (timer == null) {
            timer = Timer()
            task = object : TimerTask() {
                override fun run() {
                    reconnectCount++
                    if (reconnectCount > RECOONECT_MAX) {
                        stopReconnect()
                        return
                    }
                    QLog.i("ReConnectManager","ReConnectManager 进行重连")
                    EventBusUtil.post(ReconnectEvent.EVENT_RECONNECT)
                }
            }
            timer?.schedule(task, RECOONECT_TIME_DELAY, RECOONECT_TIME_PERIOD)
        }
    }

    @Synchronized
    fun stopReconnect() {
        timer?.cancel()
        timer = null
        reconnectCount = 0
    }
}