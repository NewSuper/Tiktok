package com.aitd.module_chat.rtc

import android.content.Context
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.utils.qlog.QLog

class RTCModuleManager {

    fun getInternalPlugins(conversationType: String): List<IPluginModule> {
        val pluginModules = mutableListOf<IPluginModule>()
        if (callModule != null && (conversationType == ConversationType.TYPE_PRIVATE )) {
            pluginModules.addAll(callModule!!.getPlugins(conversationType))
        }
        return pluginModules
    }

    fun onConnected(token: String,host:String) {
        QLog.d(TAG,"InternalModuleManager onConnected token:$token,host:$host")
        callModule?.onConnected(token,host)
    }

    fun onClick(context: Context, conversationType:String, target:String, mediayType:Int) {
        callModule?.onClick(context,conversationType,target,mediayType)
    }

    fun onInitialized(appKey: String?) {
        if (callModule != null) {
            callModule!!.onInitialized(appKey!!)
        }
    }

    companion object {

        private val TAG = "InternalModuleManager"

        private var callModule: IExternalModule? = null

        @JvmStatic
        fun init(context: Context, appKey:String) {

            try {
                val moduleName = "com.aitd.module_chat.rtc.QXCallClient"
                val cls = Class.forName(moduleName)
                val constructor = cls.getConstructor(Context::class.java)
                val client = constructor.newInstance(context)
                QLog.d(TAG,"InternalModuleManager init getDeclaredConstructor2 QXCallClient")
            }catch (ex:Exception) {
                ex.printStackTrace()
                QLog.e(TAG,"InternalModuleManager init getDeclaredConstructor2 QXCallClient exception:${ex}")
            }

            try {
                val moduleName = "com.aitd.module_chat.rtc.QXCallModule"
                val cls = Class.forName(moduleName)
                val constructor = cls.getDeclaredConstructor()
                callModule = constructor.newInstance() as IExternalModule
                callModule?.onCreate(context)
                QLog.d(TAG,"InternalModuleManager init getDeclaredConstructor2 QXCallModule")
            }catch (ex:Exception) {
                ex.printStackTrace()
                QLog.e(TAG,"InternalModuleManager init getDeclaredConstructor2 QXCallModule exception:${ex}")
            }
        }

        @JvmStatic
        val INSTANCE: RTCModuleManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RTCModuleManager()
        }

    }


}