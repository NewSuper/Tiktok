package com.aitd.module_chat.http

interface HttpCallback<T> {

    fun onSuccess(t: T)
    fun onError(errorCode: Int, message: String)
}