package com.aitd.module_login.adapter

import com.aitd.library_common.language.LanguageType
import com.aitd.module_login.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Author : palmer
 * Date   : 2021/7/6
 * E-Mail : lxlfpeng@163.com
 * Desc   :
 */
class LanguangAdapter :
    BaseQuickAdapter<LanguageType, BaseViewHolder>(R.layout.login_item_languange) {
    var selectedIndex = 0
    override fun convert(holder: BaseViewHolder, item: LanguageType) {
        item.apply {
            holder.let {
                it.setText(R.id.txt_languang, context.getString(displayName))
                it.setVisible(R.id.iv_languang_checed, selectedIndex == it.adapterPosition)
            }
        }
    }
}