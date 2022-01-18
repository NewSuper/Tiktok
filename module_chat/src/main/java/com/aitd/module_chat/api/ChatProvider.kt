package com.aitd.module_chat.api

import com.aitd.library_common.network.RetrofitManager

object ChatProvider {
    val requestService:ChatApi get() = RetrofitManager.getInstance().getApiService(ChatApi::class.java)
}