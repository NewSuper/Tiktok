package com.aitd.library_common.base

import android.os.Bundle

interface IView {
    fun init(saveInstanceState: Bundle?)
    fun getLayoutId():Int
}