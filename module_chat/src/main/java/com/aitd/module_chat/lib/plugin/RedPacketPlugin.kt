package com.aitd.module_chat.lib.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.panel.IPluginModule
import com.aitd.module_chat.lib.panel.QXExtension
import com.aitd.module_chat.pojo.ConversationType
import com.aitd.module_chat.ui.redpacket.RedPacketPrivateActivity

class RedPacketPlugin : IPluginModule {

    override fun obtainDrawable(context: Context): Drawable {
        return context.resources.getDrawable(R.drawable.vector_red_paper)
    }

    override fun obtainTitle(context: Context): String {
        return context.resources.getString(R.string.qx_chat_add_panel_red_paper)
    }

    override fun onClick(context: Activity, extension: QXExtension) {
        if (extension.conversationType == ConversationType.TYPE_PRIVATE){
             RedPacketPrivateActivity.goToActivity(context, extension.targetId)
        }else{
           // RedPacketGroupActivity.goToActivity(context, extension.targetId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}