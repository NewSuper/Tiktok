package com.aitd.library_common.utils

import android.content.Context

/**
 * 全局上下文单例
 */
class GlobalContextManager {
    var context: Context? = null
        private set

    internal object Holder {
        var instance = GlobalContextManager()
    }

    fun cacheApplicationContext(context: Context?) {
        this.context = context
    }

    companion object {
        @JvmStatic
        val instance: GlobalContextManager
            get() = Holder.instance
    }
}