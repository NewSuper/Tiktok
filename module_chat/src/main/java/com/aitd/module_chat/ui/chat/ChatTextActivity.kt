package com.aitd.module_chat.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.TextMessage
import kotlinx.android.synthetic.main.imui_activity_chat_text.*

/**
 * 查看聊天文字页面
 */
open class ChatTextActivity : BaseActivity() {

    companion object {
//        fun startActivity(context: Context, message: Message) {
//            val intent = Intent(context, ChatTextActivity::class.java)
//            intent.putExtra("message", message)
//            context.startActivity(intent)
//        }
    }

    override fun getLayoutId(): Int = R.layout.imui_activity_chat_text
    private var message: Message? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        //全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        message = intent.getParcelableExtra("message")
        tv_content.text = getMessageContent()

        chat_text_lly.setOnClickListener{
            finish()
        }
    }

    override fun init(saveInstanceState: Bundle?) {

    }

    open fun getMessageContent(): String {
        val textMessage = message!!.messageContent as TextMessage
        return textMessage.content
    }
}