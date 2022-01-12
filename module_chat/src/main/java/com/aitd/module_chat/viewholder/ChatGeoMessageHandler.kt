package com.aitd.module_chat.viewholder

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.aitd.module_chat.GeoMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.MessageContent
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.utils.file.CornerTransform
import com.aitd.module_chat.utils.file.DensityUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ChatGeoMessageHandler : ChatBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_msg_content_geo)
        super.setContentView(itemView, contentLayout, message)
        var geoMessage: MessageContent? = getMessageContent(message) ?: return
        geoMessage = geoMessage as GeoMessage

        var rootView = (contentView!!.getChildAt(0)) as ConstraintLayout
        val titleView = rootView.findViewById<TextView>(R.id.tv_geo_msg_title)
        val addressView = rootView.findViewById<TextView>(R.id.tv_geo_msg_address)
        val previewMap = rootView.findViewById<ImageView>(R.id.iv_image)
        titleView.text = geoMessage.title
        addressView.text = geoMessage.address

        var url = geoMessage.localPath
        if (TextUtils.isEmpty(url)) {
            url = geoMessage.previewUrl
        }

        //圆角处理
        val transformation = CornerTransform(itemView.context, DensityUtil.dip2px(itemView.context, 5f).toFloat())
        transformation.setExceptCorner(true, true, false, false)
        Glide.with(itemView.context)
            .load(QXIMKit.getInstance().getRealUrl(url))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.qx_ic_location_item_default)
            .transform(transformation)
            .into(previewMap);
    }
}