package com.tiktok

import android.os.Bundle
import com.tiktok.base.BaseActivity
import kotlinx.android.synthetic.main.activity_show_image.*

class ShowImageActivity:BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        ivHead!!.setOnClickListener { finish() }
        val headRes = intent.getIntExtra("res", 0)
        ivHead!!.setImageResource(headRes)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_show_image
    }
}