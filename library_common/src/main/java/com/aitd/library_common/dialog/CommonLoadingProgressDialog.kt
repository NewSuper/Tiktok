package com.aitd.library_common.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.aitd.library_common.R

class CommonLoadingProgressDialog :Dialog {

    private val loading_guan_img:ImageView by lazy {
        findViewById(R.id.loading_quan_img)
    }
    private var animator:ValueAnimator? = null

    constructor(context: Context):this(context,R.style.commont_loading_dialog)
    constructor(context: Context,themeResId:Int):super(context,themeResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_loading_dialog)
        animator = ObjectAnimator.ofFloat(loading_guan_img,"rotation",0f,-360f).apply {
            repeatCount =ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            duration = 500
        }
    }

    override fun show() {
        super.show()
        animator?.start()
    }
    fun showCancel(cancel:Boolean){
        setCanceledOnTouchOutside(cancel)
        show()
    }

    override fun hide() {
        super.hide()
        animator?.end()
    }

    override fun dismiss() {
        super.dismiss()
        animator?.end()
    }
}