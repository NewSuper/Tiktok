package com.aitd.module_chat.pojo

import android.content.Context
import com.google.gson.Gson

class UserCache(private val context: Context) {
    private val soname = "User_cache"
    private val spcachename = "last_login_user"
    private val sp by lazy { context.getSharedPreferences(soname, Context.MODE_PRIVATE) }

    fun saveUserCache(userInfo: UserInfo) {
        val json = Gson().toJson(userInfo)
        sp.edit().putString(spcachename, json).commit()
    }

    fun getUserCache(): UserInfo? {
        try {
            val userJson = sp.getString(spcachename, "")
            return Gson().fromJson(userJson, UserInfo::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

}