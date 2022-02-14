package com.tiktok.adapter

import android.widget.ImageView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.VideoBean

class PrivateLetterAdapter :
    BaseQuickAdapter<VideoBean.UserBean, BaseViewHolder>(R.layout.item_private_letter) {
    override fun convert(holder: BaseViewHolder, item: VideoBean.UserBean) {
        holder.apply {
            val iv_head = getView<ImageView>(R.id.iv_head)
            setText(R.id.tv_nickname, item.nickName)
            iv_head.setImageResource(item.head)
        }
    }
}