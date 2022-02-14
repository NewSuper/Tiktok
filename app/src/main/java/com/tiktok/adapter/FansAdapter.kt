package com.tiktok.adapter

import android.widget.ImageView
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.VideoBean

class FansAdapter : BaseQuickAdapter<VideoBean.UserBean, BaseViewHolder>(R.layout.item_fans) {

    override fun convert(holder: BaseViewHolder, item: VideoBean.UserBean) {
        holder.apply {
            val iv_head = getView<ImageView>(R.id.iv_head)
            val tv_focus = getView<TextView>(R.id.tv_focus)
            setText(R.id.tv_nickname, item.nickName)
            if (item.isFocused){
                setText(R.id.tv_focus, "已关注")
            }else{
                setText(R.id.tv_focus, "关注")
            }

            iv_head.setBackgroundColor(item.head)
            tv_focus.setOnClickListener {
                if (!item.isFocused) {
                    tv_focus.text = "已关注"
                    tv_focus  .setBackgroundResource(R.drawable.shape_round_halfwhite)
                } else {
                    tv_focus.text = "关注"
                    tv_focus  .setBackgroundResource(R.drawable.shape_round_red)
                }
                item.isFocused = !item.isFocused
            }
        }
    }
}