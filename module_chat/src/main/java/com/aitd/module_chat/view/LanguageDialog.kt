package com.aitd.module_chat.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.aitd.module_chat.R
import kotlinx.android.synthetic.main.imui_dialog_language.*


class LanguageDialog(context: Context) : Dialog(context) {

    var mContentView: View? = null
    var mListener: OnButtonClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        mContentView = LayoutInflater.from(context).inflate(R.layout.imui_dialog_language, null, false)
        setContentView(mContentView!!)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.setGravity(Gravity.BOTTOM)

        initView()
    }

    private fun initView() {
        btnChinese.setOnClickListener {
            mListener?.chinese()
            dismiss()
        }
        btnEnglisn.setOnClickListener {
            mListener?.english()
            dismiss()
        }
        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        mListener = listener
    }

    interface OnButtonClickListener {

        fun chinese()

        fun english()
    }

}