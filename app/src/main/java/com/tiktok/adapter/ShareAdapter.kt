package com.tiktok.adapter

import android.view.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.ShareBean

class ShareAdapter: BaseQuickAdapter<ShareBean, BaseViewHolder>(R.layout.item_share) {
    override fun convert(holder: BaseViewHolder, item: ShareBean) {
        holder.apply {
            val view_bg = getView<View>(R.id.view_bg)
            setText(R.id.tv_icon, item.iconRes)
            setText(R.id.tv_text, item.text)
            view_bg.setBackgroundResource(item.bgRes)
        }
    }
}