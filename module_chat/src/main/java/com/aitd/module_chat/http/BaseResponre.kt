package com.aitd.module_chat.http

class BaseResponre<T> {
    var code: Int = 0
    var data: T? = null
    var message: String = ""
}