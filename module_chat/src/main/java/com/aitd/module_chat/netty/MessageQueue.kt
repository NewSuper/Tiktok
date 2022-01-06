package com.aitd.module_chat.netty

import com.aitd.module_chat.utils.qlog.QLog
import java.util.concurrent.ConcurrentLinkedQueue


//定长消息队列，用于处理高并发消息的接收和当任务满时，挤掉头结点，插入尾结点
class MessageQueue <E>(private var max : Int): ConcurrentLinkedQueue<E>() {
    override fun add(element: E): Boolean {
        super.add(element)
        QLog.d("MessageQueue","并发测试 新任务入列")
        while (size > max) {
            QLog.d("MessageQueue", "并发测试 队列已满，挤掉最旧的任务")
            remove()
        }
        return true
    }
}