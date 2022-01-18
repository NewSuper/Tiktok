package com.aitd.module_chat.pojo

import android.os.Build
import android.util.Base64
import com.aitd.library_common.utils.GlobalContextManager
import com.aitd.module_chat.utils.SharePreferencesUtil
import java.nio.charset.Charset

object UserInfoCache {
    private var userId:String=""
    private var token:String=""
    var appKey=""

    fun getUserId():String{
        if (userId.isNullOrEmpty()){
            var data=SharePreferencesUtil.getInstance(GlobalContextManager.instance.context!!).getUserIdFromLocal()
            userId = decrypt(data)
        }
        return userId
    }

    fun setUserId(userId: String){
        this.userId = userId
        SharePreferencesUtil.getInstance(GlobalContextManager.instance.context!!).updateLocalUserId(
            encrypt(userId))
    }

    private fun decrypt(data: String): String {
        var bytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getDecoder().decode(data)
        } else {
            Base64.decode(data, Base64.NO_WRAP)
        }
        return String(bytes)
    }
    private fun encrypt(userId: String): String {
        var bytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.util.Base64.getEncoder().encode(userId.toByteArray())
        } else {
            Base64.encode(userId.toByteArray(), Base64.NO_WRAP)
        }
        return String(bytes, Charset.defaultCharset())
    }

    fun getToken(): String {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

}