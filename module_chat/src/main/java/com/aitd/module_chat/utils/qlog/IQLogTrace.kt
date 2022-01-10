package com.aitd.module_chat.utils.qlog

interface IQLogTrace {
    fun upload()
    fun log(content:String)
}