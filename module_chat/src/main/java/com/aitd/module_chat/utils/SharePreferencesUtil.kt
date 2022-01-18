package com.aitd.module_chat.utils

import android.content.Context
import android.content.SharedPreferences
import com.aitd.module_chat.pojo.BeanSensitiveWord
import com.aitd.module_chat.pojo.QXGroupNotice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharePreferencesUtil private constructor(){

   companion object{
       private val NAME_QX_IM_SDK = "qx_im_lib"
       private val TAG_TEXT_FILTER = "im_text_filter"
       private val TAG_CHARACTERS = "im_text_filter_special_character"
       private var KEY_USER_INFO_CACHE = "qx.im.sdk.user_info"
       private var mSp: SharedPreferences? = null



       private val TAG_GROUP_NOTICE = "im_group_notice"

       fun getInstance(context: Context):SharePreferencesUtil{
           mSp =context.getSharedPreferences(NAME_QX_IM_SDK,Context.MODE_PRIVATE)
           return Holder.holder
       }
   }

    object Holder{
        val holder:SharePreferencesUtil = SharePreferencesUtil()
    }

    private fun saveString(key: String, content: String) {
        mSp?.edit()?.putString(key, content)?.apply()
    }

    private fun getString(key: String, defaultValue: String): String {
        return mSp?.getString(key, defaultValue)!!
    }

    fun saveSensitiveWord(json: String) {
        saveString(TAG_TEXT_FILTER, json)
    }

    fun loadSensitiveWord(): Array<BeanSensitiveWord>? {
        val json = getString(TAG_TEXT_FILTER, "")
        return Gson().fromJson(json, Array<BeanSensitiveWord>::class.java)
    }

    fun getUserIdFromLocal(): String {
        return getString(KEY_USER_INFO_CACHE, "")
    }

    fun updateLocalUserId(userId: String) {
        saveString(KEY_USER_INFO_CACHE, userId)
    }

    fun saveSpecialCharacters(chars : String) {
        saveString(TAG_CHARACTERS, chars)
    }

    fun loadSpecialCharacters() : String {
        return getString(TAG_CHARACTERS, "")
    }









    fun isGroupNoticeRead(notice: QXGroupNotice): Boolean {
        var notices: Set<QXGroupNotice>? = loadGroupNoticeReadCache()

        if(notices != null) {
            if (!notices.contains(notice)) {
                return false
            } else {
                for (n in notices) {
                    //如果公告内容不一致，则返回未阅读
                    if (n.groupNotice != notice.groupNotice) {
                        return false
                    }
                    return n.isRead
                }
            }
        }
        return false
    }

    /**
     * 读取群
     */
    fun loadGroupNoticeReadCache(): HashSet<QXGroupNotice>? {
        val json = getString(TAG_GROUP_NOTICE, "")
        if (json.isNullOrEmpty()) {
            return null
        }
        var type = object : TypeToken<HashSet<QXGroupNotice>>(){}.type
        return Gson().fromJson(json, type)
    }


    /**
     * 设置群公告已读缓存
     */
    fun addGroupNoticeReadCache(notice: QXGroupNotice) {
        var notices: HashSet<QXGroupNotice>? = loadGroupNoticeReadCache()

        if (notices == null) {
            notices = HashSet<QXGroupNotice>()
        }
        //如果包含
        if(notices.contains(notice)) {
            for (n in notices) {
                if (n.groupNotice != notice.groupNotice) {
                    //覆盖公告内容
                    n.groupNotice = notice.groupNotice
                    n.isRead = notice.isRead
                }
            }
        } else {
            notices.add(notice)
        }
        saveString(TAG_GROUP_NOTICE, Gson().toJson(notices))
    }

}