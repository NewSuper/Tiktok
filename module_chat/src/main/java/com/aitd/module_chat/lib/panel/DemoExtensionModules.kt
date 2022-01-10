package com.aitd.module_chat.lib.panel

import android.content.Context
import com.aitd.module_chat.Message
import com.aitd.module_chat.lib.plugin.*
import com.aitd.module_chat.rtc.RTCModuleManager


class DemoExtensionModules(context: Context) : QXDefaultExtensionModule(context) {

    override fun onInit(appKey: String) {
        super.onInit(appKey)
    }

    override fun onConnect(token: String) {
        super.onConnect(token)
    }

    override fun onAttachedToExtension(extension: QXExtension) {
        super.onAttachedToExtension(extension)
    }

    override fun onDetachedFromExtension() {
        super.onDetachedFromExtension()
    }

    override fun onReceivedMessage(message: Message) {
        super.onReceivedMessage(message)
    }

    override fun getPluginModules(conversationType: String): List<IPluginModule> {
        val pluginModuleList = mutableListOf<IPluginModule>()
        pluginModuleList.add(ImagePlugin())
//        pluginModuleList.add(TakePhotoPlugin())
//        pluginModuleList.add(SightPlugin())
        pluginModuleList.add(TakePhotoVideoPlugin())
        pluginModuleList.addAll(RTCModuleManager.INSTANCE.getInternalPlugins(conversationType))
        pluginModuleList.add(LocationPlugin())
        pluginModuleList.add(FilePlugin())
        pluginModuleList.add(RedPacketPlugin())
        return pluginModuleList
    }

    override fun onDisconnect() {
        super.onDisconnect()
    }
}