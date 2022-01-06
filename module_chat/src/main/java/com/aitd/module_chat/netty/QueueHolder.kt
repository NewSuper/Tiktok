package com.aitd.module_chat.netty

import com.aitd.module_chat.lib.handler.BaseCmdHandler
import com.aitd.module_chat.lib.handler.ReceiveGroupMessageHandler
import com.aitd.module_chat.utils.qlog.QLog
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QueueHolder {

    @Volatile
    var isStart = false

    private val max = 100
    private val duration = 100L
    var handler : BaseCmdHandler? = null

    var mMessageQueue: MessageQueue<MessageTask>? = null
    var mPool : ExecutorService? = null

    constructor() {
        mMessageQueue = MessageQueue(max)
    }

    fun take(task: MessageTask) {
        QLog.d("QueueHolder","并发测试 take 取任务放入队列")
        mMessageQueue!!.add(task)

        //如果消费者线程池空或者已终止，则重新启动
        if (mPool == null || mPool!!.isShutdown || mPool!!.isTerminated) {
            QLog.d("QueueHolder", "并发测试 启动线程池")
            handler = ReceiveGroupMessageHandler()
            mPool = Executors.newSingleThreadExecutor()
            isStart = true
        }
        isStart = true
        mPool!!.execute(mRunnable)
    }

    private var mRunnable = Runnable {
        try {
            QLog.d("QueueHolder",  "启动消费者线程 threadId="+Thread.currentThread().id)

            while (isStart) {
                QLog.d("QueueHolder",  "任务轮询 threadId="+Thread.currentThread().id)
                if(mMessageQueue!!.size < 1) {
                    QLog.d("QueueHolder",  "消息队列为空，停止轮询")
                    isStart = false
                    break
                }
                //消费任务
                var task = mMessageQueue!!.poll()
                task.handle(handler!!)
                if (mPool!!.isShutdown || mPool!!.isTerminated) {
                    isStart = false
                    break
                }
                Thread.sleep(duration)
            }
            QLog.d("QueueHolder",  "终止消费者线程")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}