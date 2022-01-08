package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.GeoMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import kotlinx.android.synthetic.main.imui_layout_record_content_geo.view.*

class RecordGeoMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_geo)
        super.setContentView(itemView, contentLayout, message)

        var geoMessage = message.messageContent as GeoMessage
        contentView!!.tv_geo_title.text = geoMessage.title
        contentView!!.tv_geo_address.text = geoMessage.address
    }
}