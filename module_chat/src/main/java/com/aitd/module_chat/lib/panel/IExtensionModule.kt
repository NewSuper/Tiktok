package com.aitd.module_chat.lib.panel

import com.aitd.module_chat.Message

interface IExtensionModule {
    fun onInit(appKey: String)

    fun onConnect(token: String)

    fun onAttachedToExtension(extension: QXExtension)

    fun onDetachedFromExtension()

    fun onReceivedMessage(message: Message)

    /**
     * 单聊，群聊部分插件功能不一样
     */
    fun getPluginModules(conversationType: String): List<IPluginModule>

    fun onDisconnect()
}