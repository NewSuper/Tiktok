package com.aitd.module_chat.lib.panel

import android.content.Context
import com.aitd.module_chat.Message
import com.aitd.module_chat.lib.plugin.FilePlugin
import com.aitd.module_chat.lib.plugin.ImagePlugin
import com.aitd.module_chat.lib.plugin.LocationPlugin
import com.aitd.module_chat.lib.plugin.TakePhotoPlugin
import com.aitd.module_chat.rtc.RTCModuleManager

open class QXDefaultExtensionModule(context: Context) : IExtensionModule {

    override fun onInit(appKey: String) {

    }

    override fun onConnect(token: String) {

    }

    override fun onAttachedToExtension(extension: QXExtension) {
    }

    override fun onDetachedFromExtension() {
    }

    override fun onReceivedMessage(message: Message) {
    }

    override fun getPluginModules(conversationType: String): List<IPluginModule> {
        val pluginModuleList = mutableListOf<IPluginModule>()
        pluginModuleList.add(ImagePlugin())
        pluginModuleList.add(TakePhotoPlugin())
        pluginModuleList.addAll(RTCModuleManager.INSTANCE.getInternalPlugins(conversationType))
        pluginModuleList.add(LocationPlugin())
        pluginModuleList.add(FilePlugin())
        return pluginModuleList
    }

    override fun onDisconnect() {
    }
}