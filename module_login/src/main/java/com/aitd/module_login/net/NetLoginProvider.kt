package com.aitd.module_login.net

import com.aitd.library_common.network.RetrofitManager

object NetLoginProvider {
    val requestService: LoginApi get() = RetrofitManager.getInstance().getApiService(LoginApi::class.java)
}