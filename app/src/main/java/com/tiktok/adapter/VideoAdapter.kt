package com.tiktok.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.VideoBean
import com.tiktok.view.ControllerView
import com.tiktok.view.LikeView

class VideoAdapter : BaseQuickAdapter<VideoBean, BaseViewHolder>(R.layout.item_video) {
    override fun convert(holder: BaseViewHolder, item: VideoBean) {
        holder.apply {
            val controller = getView<ControllerView>(R.id.controller)
            val iv_cover = getView<ImageView>(R.id.iv_cover)
            val likeview = getView<LikeView>(R.id.likeview)

            controller.setVideoData(item)
            iv_cover.setBackgroundResource(item.coverRes)
            likeview.setOnLikeListener(object :LikeView.OnLikeListener{
                override fun onLikeListener() {
                    if (!item.isLiked){//未点赞，会有点赞效果，否则无
                        controller.like()
                    }
                }
            })
        }
    }
}