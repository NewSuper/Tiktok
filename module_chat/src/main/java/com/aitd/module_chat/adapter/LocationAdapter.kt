package com.aitd.module_chat.adapter

import android.view.View
import com.aitd.module_chat.R
import com.aitd.module_chat.pojo.LocationModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class LocationAdapter(layout:Int) : BaseQuickAdapter<LocationModel, BaseViewHolder>(layout) {

    override fun convert(helper: BaseViewHolder, item: LocationModel) {
        helper.setText(R.id.tvLocationTitle,item.title)
        helper.setText(R.id.tvLocationDescri,item.address)
        helper.getView<View>(R.id.item).isSelected = item.selected
        if (item.selected) {
            helper.setVisible(R.id.ivLocationChecked,true)
        } else {
            helper.setGone(R.id.ivLocationChecked,false)
        }
    }
}