package com.aitd.library_common.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

fun <T : TextView> T.setDrawableDircetion(resId:Int, direction: DrawableDircetion) {
    setDrawableDircetion(resId,direction,0,0)
}

fun <T : TextView> T.setDrawableDircetion(resId:Int, direction: DrawableDircetion, width:Int = 0, height:Int = 0) {
    val drawable =  ContextCompat.getDrawable(this.context,resId)
    drawable!!.setBounds(0, 0, if (width == 0) drawable.minimumWidth else width,if (height == 0) drawable.minimumHeight else height)
    when(direction) {
        DrawableDircetion.LEFT ->  this.setCompoundDrawables(drawable, null, null, null)
        DrawableDircetion.TOP ->  this.setCompoundDrawables(null, drawable, null, null)
        DrawableDircetion.RIGHT ->  this.setCompoundDrawables(null, null, drawable, null)
        DrawableDircetion.BOTTOM->  this.setCompoundDrawables(null, null, null, drawable)
    }
}

enum class DrawableDircetion {
    LEFT,RIGHT,TOP,BOTTOM
}

fun dp2px(context: Context, dp:Int):Int{
    return context.resources.displayMetrics.density.let { (dp * it + 0.5).toInt()}
}

fun dp2pxF(context: Context, dp:Int):Float{
    return context.resources.displayMetrics.density.let { (dp * it + 0.5).toFloat()}
}

fun px2dp(context: Context, px:Int):Int{
    return context.resources.displayMetrics.density?.let { (px.toDouble() / it + 0.5).toInt()}
}

fun <T : View> T.click(block: (T) -> Unit) {
    setOnClickListener {
        block(this)
    }
}



val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this, Resources.getSystem().displayMetrics)

val Float.dp2
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this, Resources.getSystem().displayMetrics).toInt()