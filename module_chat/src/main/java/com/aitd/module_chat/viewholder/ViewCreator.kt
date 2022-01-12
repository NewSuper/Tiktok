package com.aitd.module_chat.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aitd.module_chat.R

object ViewCreator {

    fun create(context: Context, viewType: Int, parent: ViewGroup): BaseViewHolder {
        var view: View?
        when (viewType) {
            //系统通知
            ViewTypeUtil.ViewType.type_notice -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_msg_notice, parent, false)
                return NoticeViewHolder(context,view)
            }
            //撤回
            ViewTypeUtil.ViewType.type_recall -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_recall_msg, parent, false)
                return RecallViewHolder(context,view)
            }

            ViewTypeUtil.ViewType.type_left -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_msg_common_left, parent, false)
                return CommonViewHolder(context,view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_msg_common_right, parent, false)
                return CommonViewHolder(context,view)
            }
        }
    }

}