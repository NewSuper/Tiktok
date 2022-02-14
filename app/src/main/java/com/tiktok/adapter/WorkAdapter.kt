package com.tiktok.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.VideoBean
import com.tiktok.utils.NumUtils
import com.tiktok.view.ControllerView

class WorkAdapter : BaseQuickAdapter<VideoBean, BaseViewHolder>(R.layout.item_work) {
    override fun convert(holder: BaseViewHolder, item: VideoBean) {
        holder.apply {
            val iv_cover = getView<ImageView>(R.id.iv_cover)

            iv_cover.setBackgroundResource(item.coverRes)
            setText(R.id.tv_likecount, NumUtils.numberFilter(item.likeCount))
        }
    }
}