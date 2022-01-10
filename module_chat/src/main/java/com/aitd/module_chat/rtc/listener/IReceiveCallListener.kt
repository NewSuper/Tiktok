package com.aitd.module_chat.rtc.listener

import com.aitd.module_chat.rtc.QXCallSession

interface IReceiveCallListener {
    fun onReceivedCall(session: QXCallSession)
}