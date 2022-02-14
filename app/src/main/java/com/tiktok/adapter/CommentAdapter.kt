package com.tiktok.adapter

import android.widget.ImageView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tiktok.R
import com.tiktok.pojo.CommentBean
import com.tiktok.utils.NumUtils

class CommentAdapter : BaseQuickAdapter<CommentBean, BaseViewHolder>(R.layout.item_comment) {
    override fun convert(holder: BaseViewHolder, item: CommentBean) {
        holder.apply {
            val iv_head = getView<ImageView>(R.id.iv_head)
            setText(R.id.tv_nickname, item.userBean?.nickName)
            setText(R.id.tv_content, item.content)
            setText(R.id.tv_likecount, NumUtils.numberFilter(item.likeCount))
            iv_head.setBackgroundResource(item.userBean!!.head)
        }
    }
}