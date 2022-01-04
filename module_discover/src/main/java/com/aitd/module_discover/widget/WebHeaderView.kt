package com.aitd.module_discover.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.aitd.library_common.alias.viewOnClick
import com.aitd.library_common.base.BASE_URL_WEB
import com.aitd.library_common.base.WEB_LANGUAGE
import com.aitd.library_common.language.LanguageSpUtil
import com.aitd.module_discover.R

class WebHeaderView:FrameLayout {
    private lateinit var mHeaderView: View
    private lateinit var mRecommendDetailsWeb: WebView
    private var mOnWriteMessageClickListener: viewOnClick? = null
    private var mOnLikeClickListener: viewOnClick? = null
    private var mOnShareClickListener: viewOnClick? = null

    constructor(context: Context) : this(context, null){
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0){
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }
    private fun init(){
        mHeaderView = LayoutInflater.from(context).inflate(R.layout.discover_recommendation_details_head,this,true)
        mRecommendDetailsWeb = mHeaderView?.findViewById(R.id.web)
        mRecommendDetailsWeb?.settings?.javaScriptEnabled = true
        mRecommendDetailsWeb?.settings?.domStorageEnabled = true
        bindListenerView()
    }
    private fun bindListenerView(){
        mHeaderView.findViewById<TextView>(R.id.tv_Write_message).setOnClickListener {
            if (mOnWriteMessageClickListener != null){
                mOnWriteMessageClickListener?.invoke(it)
            }
        }

        mHeaderView.findViewById<LinearLayout>(R.id.ly_recommend_share).setOnClickListener {
            if (mOnShareClickListener !=null){
                mOnShareClickListener?.invoke(it)
            }
        }

        mHeaderView.findViewById<LinearLayout>(R.id.ly_recommend_like).setOnClickListener {
            if (mOnLikeClickListener !=null){
                mOnLikeClickListener?.invoke(it)
            }
        }
    }

    fun setLoadUrl(articleId:Int){
        mRecommendDetailsWeb?.loadUrl(BASE_URL_WEB + articleId + WEB_LANGUAGE + LanguageSpUtil.getLanguageType().code)
    }

    /**
     * 写留言
     */
    fun setWriteMessageOnClickListener(listener: viewOnClick?){
        this.mOnWriteMessageClickListener = listener
    }

    /**
     * 点赞
     */
    fun setLikeOnClickListener(listener: viewOnClick?){
        this.mOnLikeClickListener = listener
    }

    /**
     * 分享
     */
    fun setShareOnClickListener(listener: viewOnClick?){
        this.mOnShareClickListener = listener
    }
}