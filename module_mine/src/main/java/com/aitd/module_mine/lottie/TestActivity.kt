package com.aitd.module_mine.lottie

import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_mine.R

class TestActivity :BaseActivity() {
    override fun init(saveInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int {
       return  R.layout.fragment_web
    }
}