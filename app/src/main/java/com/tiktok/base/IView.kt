package com.tiktok.base

import android.os.Bundle

interface IView {
    /**
     * 初始化界面
     */
    fun init(savedInstanceState: Bundle?)

    /**
     * 获取activity布局id
     */
    fun getLayoutId(): Int

}