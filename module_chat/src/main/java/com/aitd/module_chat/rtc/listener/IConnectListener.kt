package com.aitd.module_chat.rtc.listener

import com.aitd.module_chat.rtc.QXCallState

interface IConnectListener {
    fun join()
    fun joined()
    fun connecting()
    fun connected()
    fun disconnect(callState: QXCallState)
    fun error(callState: QXCallState)
}