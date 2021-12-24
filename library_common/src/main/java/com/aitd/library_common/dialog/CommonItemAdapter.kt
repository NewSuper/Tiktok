package com.aitd.library_common.dialog

import com.aitd.library_common.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class CommonItemAdapter:BaseQuickAdapter<String,BaseViewHolder>(R.layout.common_item_dialog) {
    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.txt_item,item)
    }
}