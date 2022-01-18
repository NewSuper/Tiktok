package com.aitd.module_chat.http

import okhttp3.logging.HttpLoggingInterceptor

class HttpLogger:HttpLoggingInterceptor.Logger {
    override fun log(message: String) {

    }
}