package com.tiktok.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.VideoBean
import com.tiktok.view.IconFontTextView

class GridVideoAdapter : BaseQuickAdapter<VideoBean, BaseViewHolder>(R.layout.item_gridvideo) {
    override fun convert(holder: BaseViewHolder, item: VideoBean) {
        holder.apply {
            val tv_distance = getView<IconFontTextView>(R.id.tv_distance)
            val iv_cover = getView<ImageView>(R.id.iv_cover)
            val iv_head = getView<ImageView>(R.id.iv_head)
            setText(R.id.tv_content, item.content)
            tv_distance.text = item.distance.toString() + " km"

            iv_cover.setBackgroundResource(item.coverRes)
            iv_head.setImageResource(item.userBean!!.head)
        }
    }
}