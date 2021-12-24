package com.aitd.library_common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.aitd.library_common.R
import com.chad.library.adapter.base.listener.OnItemClickListener

class CommonBottomDialog:Dialog {

    private var mData = mutableListOf<String>()
    constructor(context: Context?,data:MutableList<String>):this(context, R.style.common_bottom_dialog,data)
    constructor(context: Context?,themeResId:Int,data: MutableList<String>):super(context!!,themeResId){
        this.mData = data
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    private var mCommonItemAdapter: CommonItemAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_bottom_dialog)
        findViewById<RecyclerView>(R.id.rlv_root).apply {
            mCommonItemAdapter = CommonItemAdapter().let {
                it.setNewInstance(mData)
                if (mOnItemClickListener != null){
                    it.setOnItemClickListener(mOnItemClickListener)
                }
                it
            }
            adapter = mCommonItemAdapter
        }
        window?.run {
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomAnimation)
            val params = attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes = params
        }
        findViewById<View>(R.id.tvCancel).setOnClickListener { dismiss() }
    }
    fun setOnItemClickListener(onItemClickListener:OnItemClickListener){
        mOnItemClickListener = onItemClickListener
    }
}