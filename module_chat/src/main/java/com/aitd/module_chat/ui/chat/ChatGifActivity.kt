package com.aitd.module_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.ImageMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMKit
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.imui_activity_chat_gif.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*

class ChatGifActivity:BaseActivity() {
    companion object {
        fun startActivity(context: Context, message: Message) {
            val intent = Intent(context,ChatGifActivity::class.java)
            intent.putExtra("message",message)
            context.startActivity(intent)

        }
    }
    private var gifMessage:Message ?= null
    override fun init(saveInstanceState: Bundle?) {
        gifMessage = intent.getParcelableExtra("message")
        gifMessage?.apply {
            val imageMessage = messageContent as ImageMessage
            if (!imageMessage.originUrl.isNullOrEmpty()) {
                Glide.with(this@ChatGifActivity).setDefaultRequestOptions(RequestOptions().centerInside()).load(
                    QXIMKit.getInstance().getRealUrl(imageMessage.originUrl)).into(iv_gif)
            } else if (!imageMessage.localPath.isNullOrEmpty()) {
                Glide.with(this@ChatGifActivity).setDefaultRequestOptions(RequestOptions().centerInside()).load(imageMessage.localPath).into(iv_gif)
            }
        }
        iv_gif.setOnClickListener {
            finish()
        }

        iv_back.setOnClickListener {
            finish()
        }
        iv_menu.visibility = View.GONE
    }

    override fun getLayoutId(): Int = R.layout.imui_activity_chat_gif
}