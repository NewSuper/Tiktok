package com.aitd.module_chat.rtc

import android.content.Context
import com.aitd.module_chat.lib.panel.IPluginModule

interface IExternalModule {

    fun onCreate(context: Context)

    fun onInitialized(token: String)

    fun onConnected(token: String,host:String)

    fun onViewCreated()

    fun onDisconnected()

    /**
     * 单聊，群聊部分插件功能不一样
     */
    fun getPlugins(conversationType: String): List<IPluginModule>

    fun onClick(context: Context, conversationType:String, target:String, mediaType:Int)
}