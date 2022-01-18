package com.aitd.module_chat.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aitd.module_chat.ImageTextMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.MessageContent
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMKit
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class ChatImageTextMessageHandler : ChatBaseMessageHandler() {
    var mIconMap = HashMap<String, Int>()
    var mTypeMap = HashMap<String, Int>()

    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_msg_content_image_text)
        super.setContentView(itemView, contentLayout, message)

        var content: MessageContent? = getMessageContent(message) ?: return
        content = content as ImageTextMessage

        var rootView = contentView!!.findViewById(R.id.layout_message_content) as ConstraintLayout

        var tv_title = rootView.findViewById<TextView>(R.id.tv_title)
        var tv_content = rootView.findViewById<TextView>(R.id.tv_content)
        var iv_icon = rootView.findViewById<ImageView>(R.id.iv_image)
        var iv_type_icon = rootView.findViewById<ImageView>(R.id.iv_type_icon)
        var tv_type = rootView.findViewById<TextView>(R.id.tv_type)

        tv_title.text = content.title
        tv_content.text = content.content

        Glide.with(itemView.context).load(QXIMKit.getInstance().getRealUrl(content.imageUrl)).apply(
            RequestOptions
                .bitmapTransform(RoundedCorners(20))
                .placeholder(R.mipmap.default_img)
        ).into(iv_icon)

        iv_type_icon.setImageResource(getResource(content.tag))
        tv_type.text = itemView.context.resources.getString(getTypeName(content.tag))
    }

    private fun getResource(tag :String): Int {
        var resId = mIconMap[tag.toLowerCase()]
        return resId ?: R.drawable.vector_file_unkown
    }

    private fun getTypeName(tag :String): Int {
        var resId = mTypeMap[tag.toLowerCase()]
        return resId ?: R.string.qx_image_text_type_unknown
    }

    init {
        //社区
        mIconMap["0"] = R.drawable.vector_shequ
        //动态
        mIconMap["1"] = R.drawable.vector_shequ
        //直播
        mIconMap["2"] = R.drawable.vector_live
        //推荐
        mIconMap["3"] = R.drawable.vector_recommend

        mTypeMap["0"] = R.string.qx_image_text_type_social
        mTypeMap["1"] = R.string.qx_image_text_type_activity
        mTypeMap["2"] = R.string.qx_image_text_type_live
        mTypeMap["3"] = R.string.qx_image_text_type_recommend

    }
}
