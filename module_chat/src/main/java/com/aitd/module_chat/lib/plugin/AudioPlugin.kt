package com.aitd.module_chat.lib.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.lib.panel.QXExtension
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.rtc.QXCallKit

class AudioPlugin: IPluginModule {
    override fun obtainDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.vector_audio_call)
    }

    override fun obtainTitle(context: Context): String {
        return context.resources.getString(R.string.qx_chat_add_panel_audio_call)
    }

    override fun onClick(context: Activity, extension: QXExtension) {
        if (extension.conversationType == ConversationType.TYPE_GROUP) {
            val memebers = mutableListOf("dengweipin1","yipeng","yipeng1","zhoululu","zhoululu1") as ArrayList
            QXCallKit.startMultiCall(context,extension.conversationType,extension.targetId,memebers,QXCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO)
        } else {
            QXCallKit.startSignleCall(context,extension.conversationType,extension.targetId,QXCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}