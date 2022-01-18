package com.aitd.module_chat.ui.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.*
import com.aitd.module_chat.lib.AudioPlayManager
import com.aitd.module_chat.lib.UserInfoUtil
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.viewholder.ChatVoiceMessageHandler
import com.aitd.module_chat.viewholder.record.ChatRecordAdapter
import kotlinx.android.synthetic.main.imui_activity_chat_record.*
import kotlinx.android.synthetic.main.imui_common_title_bar.*


class ChatRecordActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.imui_activity_chat_record

    private lateinit var mRecordAdapter: ChatRecordAdapter
    private var mMessageList: List<Message> = arrayListOf()
    private lateinit var mMessageViewManager: LinearLayoutManager
    var mMessage: Message? = null


    override fun init(saveInstanceState: Bundle?) {
        mMessage = intent.getParcelableExtra("message")
        if (mMessage?.messageType == MessageType.TYPE_RECORD && mMessage?.messageContent is RecordMessage) {
            var record = mMessage!!.messageContent as RecordMessage
            mMessageList = record.messages
        }
        iv_back.setOnClickListener {
            onBackPressed()
        }
        iv_menu.visibility = View.GONE

        if (mMessage != null) {
            tv_title_bar_name.text =
                UserInfoUtil.getRecordTitle(this@ChatRecordActivity, mMessage!!)
        }
        initRecordRecyclerView()
    }


    private fun initRecordRecyclerView() {
        mMessageViewManager = LinearLayoutManager(this)
        mRecordAdapter = ChatRecordAdapter(mMessageList)

        recycler_view_record.apply {
            setHasFixedSize(true)
            layoutManager = mMessageViewManager
            adapter = mRecordAdapter
        }

        mRecordAdapter.setItemClickListener(object : ChatRecordAdapter.OnItemClickListener {
            override fun onClick(position: Int, v: View) {
                var message = mMessageList[position]
                when (message.messageType) {
                    MessageType.TYPE_RECORD -> {
                        var intent = Intent(this@ChatRecordActivity, ChatRecordActivity::class.java)
                        intent.putExtra("message", message)
                        startActivity(intent)
                    }
                    MessageType.TYPE_GEO -> {
                        LocationDetailActivity.startActivity(this@ChatRecordActivity, message)
                    }
                    MessageType.TYPE_IMAGE -> {
                        val imageMsg = message.messageContent as ImageMessage
//                        if (imageMsg.originUrl.contains(".gif") || imageMsg.localPath.contains(".gif")) {
//                            ChatGifActivity.startActivity(this@ChatRecordActivity, message)
//                        } else {
//                            ImagePageActivity.startActivity(this@ChatRecordActivity, message)
//                        }
                        var intent =
                            Intent(this@ChatRecordActivity, RecordImagePageActivity::class.java)
                        intent.putExtra("url", imageMsg.originUrl)
                        startActivity(intent)
                    }
                    MessageType.TYPE_IMAGE_AND_TEXT -> {
                        var uri = Uri.parse(
                            (message.messageContent as ImageTextMessage)!!.redirectUrl
                        )
                        var intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.data = uri
                        startActivity(intent)
                    }
                    MessageType.TYPE_FILE -> {
                        var intent = Intent(this@ChatRecordActivity, RecordFileActivity::class.java)
                        intent.putExtra("message", message)
                        startActivity(intent)
                    }
                    MessageType.TYPE_AUDIO -> {
                        var voiceMessageHandler: ChatVoiceMessageHandler.ViewHolder? = null
                        if (v.tag is ChatVoiceMessageHandler.ViewHolder) {
                            voiceMessageHandler = v.tag as ChatVoiceMessageHandler.ViewHolder
                        }
                        val voiceMessage = message.messageContent as AudioMessage
                        if (AudioPlayManager.getInstance().isPlaying) {
                            if (AudioPlayManager.getInstance().playingUri == Uri.parse(voiceMessage.localPath)) {
                                AudioPlayManager.getInstance().stopPlay()
                                return
                            }
                            AudioPlayManager.getInstance().stopPlay()
                        }
                        if (!AudioPlayManager.getInstance()
                                .isInNormalMode(this@ChatRecordActivity) && AudioPlayManager.getInstance()
                                .isInVOIPMode(this@ChatRecordActivity)
                        ) {
                            ToastUtil.toast(
                                this@ChatRecordActivity,
                                getString(R.string.qx_voip_occupying)
                            )
                        } else {
                            if (voiceMessageHandler != null) {
                                AudioPlayManager.getInstance().startPlay(
                                    this@ChatRecordActivity, Uri.parse(voiceMessage.localPath),
                                    ChatVoiceMessageHandler().VoiceMessagePlayListener(
                                        this@ChatRecordActivity,
                                        message,
                                        voiceMessageHandler!!
                                    )
                                )
                            }
                        }
                    }
                    MessageType.TYPE_VIDEO -> {
                        var intent =
                            Intent(this@ChatRecordActivity, RecordVideoPlayActivity::class.java)
                        intent.putExtra("message", message)
                        startActivity(intent)
                    }
                }
            }
        })
    }
}