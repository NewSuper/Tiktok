package com.aitd.library_common.utils

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import com.aitd.library_common.utils.GlobalContextManager.Companion.instance
import java.net.URLDecoder
import java.util.regex.Pattern

object StringUtil {
    /**
     * 获取url里面的参数
     */
    fun getOneParameter(url: String, keyWord: String): String? {
        var url = url
        var retValue: String? = ""
        try {
            val charset = "utf-8"
            url = URLDecoder.decode(url, charset)
            if (url.indexOf('?') != -1) {
                val contents = url.substring(url.indexOf('?') + 1)
                val keyValues = contents.split("&".toRegex()).toTypedArray()
                for (i in keyValues.indices) {
                    val key = keyValues[i].substring(0, keyValues[i].indexOf("="))
                    val value = keyValues[i].substring(keyValues[i].indexOf("=") + 1)
                    if (key == keyWord) {
                        if (value != null || "" != value.trim { it <= ' ' }) {
                            retValue = value
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return retValue
    }

    /**
     * 是否为null或空字符串
     */
    fun isEmpty(str: String?): Boolean {
        return TextUtils.isEmpty(str)
    }

    /**
     * 判断是字符串是否没有（空或者"")
     *
     * @param aData
     * @return
     */
    fun stringEmpty(aData: String?): Boolean {
        return if (null == aData || "" == aData || "" == aData.trim { it <= ' ' }) {
            true
        } else false
    }

    /**
     * 取指定资源字符串对像值
     *
     * @param context
     * @return
     */
    @JvmStatic
    fun getResourceStr(context: Context, StringID: Int): String {
        if (StringID == -1) return ""
        try {
            return context.getString(StringID)
        } catch (mx: Resources.NotFoundException) {
        }
        return ""
    }

    /**
     * 取指定资源字符串对像值
     *
     * @param StringID
     * @return
     */
    fun getResourceStr(StringID: Int): String {
        if (StringID == -1) return ""
        if (instance.context != null) {
            try {
                return instance.context!!.resources.getString(StringID)
            } catch (mx: Resources.NotFoundException) {
            }
        }
        return ""
    }

    /**
     * 取指定资源字符串对像值(例如 Hi,%1$s你好吗?)
     *
     * @param StringID
     * @param obj      数据填充列表
     * @return
     */
    @JvmStatic
    fun getResourceStr(context: Context, StringID: Int, vararg obj: Any?): String {
        return String.format(getResourceStr(context, StringID), *obj)
    }

    /**
     * 返回需要的资源颜色
     *
     * @param colorID
     * @return
     */
    @JvmStatic
    fun getResourceColor(colorID: Int): Int {
        return instance.context!!.resources.getColor(colorID)
    }

    /**
     * 判断当前内容是否为正确的网址
     * @param urls
     * @return
     */
    fun isHttpUrl(urls: String): Boolean {
        var isurl = false
        val regex = ("(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)") //设置正则表达式
        val pat = Pattern.compile(regex.trim { it <= ' ' }) //比对
        val mat = pat.matcher(urls.trim { it <= ' ' })
        isurl = mat.matches() //判断是否匹配
        if (isurl) {
            isurl = true
        }
        return isurl
    }
}