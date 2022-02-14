package com.tiktok.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

/**
 * 跑马灯textview
 */
class MarqueeTextView : TextView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun isFocused(): Boolean {
        return true
    }
}