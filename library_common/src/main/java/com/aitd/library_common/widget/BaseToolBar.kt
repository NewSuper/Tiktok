package com.aitd.library_common.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.Toolbar
import com.aitd.library_common.R
import com.aitd.library_common.alias.viewOnClick

class BaseToolBar : Toolbar {
    private var isTvLight = true
    private var isTvLeft  = false
    private var isTvRight = false
    private var showDiv   = false
    private var title = ""
    lateinit var llRoot: LinearLayout
    lateinit var ivToolbarBack: ImageView
    lateinit var txtToolbarTitle: TextView
    lateinit var mImgToolbarRight: ImageView
    lateinit var mTxtToolbarRight: TextView
    lateinit var mTxtToolbarLeft: TextView
    lateinit var mDiver: View

    //声明变量，类型为一个函数
    private var mOnBackClickListener: viewOnClick? = null
    private var mOnRightClickListener: viewOnClick? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseToolBar)
        isTvLight  = typedArray.getBoolean(R.styleable.BaseToolBar_isTvLight, true)
        isTvRight  = typedArray.getBoolean(R.styleable.BaseToolBar_isTvRight, false)
        isTvLeft   = typedArray.getBoolean(R.styleable.BaseToolBar_isTvLeft, false)
        title = typedArray.getString(R.styleable.BaseToolBar_title) ?: ""
        showDiv = typedArray.getBoolean(R.styleable.BaseToolBar_showBottomDiv, false)
        typedArray.recycle()
        initView(context)
    }

    private fun initView(context: Context) {
        setContentInsetsAbsolute(0, 0)
        val view: View = inflate(context, R.layout.common_base_toolbar, this)
        llRoot = view.findViewById(R.id.ll_root)
        ivToolbarBack = view.findViewById(R.id.iv_toolbar_back)
        txtToolbarTitle = view.findViewById(R.id.txt_toolbar_title)
        mDiver = view.findViewById(R.id.view_toolbar_divider)

        mTxtToolbarLeft  = view.findViewById(R.id.tv_toolbar_left)
        mImgToolbarRight = view.findViewById(R.id.img_toolbar_right)
        mTxtToolbarRight = view.findViewById(R.id.tv_toolbar_right)
        txtToolbarTitle.text = title
        ivToolbarBack.visibility    = showOrHide(!isTvLeft)
        mTxtToolbarLeft.visibility  = showOrHide(isTvLeft)

        mTxtToolbarRight.visibility = showOrHide(isTvRight)
        mImgToolbarRight.visibility = showOrHide(!isTvRight)
        mDiver.visibility           = showOrHide(showDiv)
        bindListener(context)
    }

    /**
     * 事件绑定
     */
    private fun bindListener(context: Context){
        ivToolbarBack.setOnClickListener {
            if (mOnBackClickListener == null && context is Activity) {
                context.finish()
            } else {
                mOnBackClickListener?.invoke(it)
            }
        }
        mTxtToolbarLeft.setOnClickListener {
            if (mOnBackClickListener != null) {
                mOnBackClickListener?.invoke(it)
            }
        }
        mImgToolbarRight.setOnClickListener{
            if (mOnRightClickListener != null){
                mOnRightClickListener?.invoke(it)
            }
        }
        mTxtToolbarRight.setOnClickListener{
            if (mOnRightClickListener != null){
                mOnRightClickListener?.invoke(it)
            }
        }
    }

    /**
     * 显示或隐藏
     */
    private fun showOrHide(isShow: Boolean):Int {
        return if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


    fun setRightImageView(@DrawableRes resId:Int){
        this.mImgToolbarRight.setImageResource(resId)
    }

    fun setRightOnClickListener(listener: viewOnClick?){
        this.mOnRightClickListener = listener
    }

    fun setTitleText(titleString: String) {
        txtToolbarTitle.text = titleString
    }

    fun setLeftText(titleString: String) {
        mTxtToolbarLeft.text = titleString
    }

    fun setRightTextColor(textColor: String) {
        mTxtToolbarRight.setTextColor(Color.parseColor(textColor))
    }

    fun setRightText(titleString: String) {
        mTxtToolbarRight.text = titleString
    }

    fun setRightTextEnabled(isEnabled: Boolean) {
        mTxtToolbarRight.isEnabled = isEnabled
    }

    //添加一个监听方法，参数同样是一个函数，类型与onClickOkListener保持一致
    fun setBackOnClickListener(listener: viewOnClick?) {
        this.mOnBackClickListener = listener
    }
}