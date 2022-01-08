package com.aitd.module_chat.ui.chat

import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.imui_record_image_activity.*

class RecordImagePageActivity : BaseActivity() {
    override fun init(saveInstanceState: Bundle?) {
        iv_image.enable()
        var url = intent.getStringExtra("url")
        Glide.with(this).load(url)
            .apply(RequestOptions().placeholder(R.mipmap.default_img).error(R.mipmap.default_img))
            .into(iv_image)
    }

    override fun getLayoutId(): Int = R.layout.imui_record_image_activity
}