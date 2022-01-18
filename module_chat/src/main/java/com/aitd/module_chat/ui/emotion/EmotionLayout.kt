package com.aitd.module_chat.ui.emotion

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.aitd.module_chat.R


class EmotionLayout: LinearLayout {

    private val TAG = "EmotionLayout"

    private var mContext: Context? = null
    private var mTabContainer: LinearLayout? = null
    private var mMeasuredWidth = 0
    private var mMeasuredHeight = 0
    private val mTabViewArray = SparseArray<View>()
    private var vpEmotion: ViewPager? = null
    private var emotionPageAdapter:EmotionPageAdapter? = null

    private var iEmotionClickLisntener: IEmotionClickLisntener? = null

    var inputStatusListener: IInputStatusListener = object : IInputStatusListener {
        override fun inputCallback(inputlength: Int) {
            emotionPageAdapter?.changeInputStatus(inputlength > 0)
        }

    }

    fun setemotionClickListener(lisntener: IEmotionClickLisntener) {
        emotionPageAdapter?.clickLisntener = lisntener
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr:Int = 0) : super(context,attrs,defStyleAttr) {
        mContext = context
        initView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        mContext = context
        initView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        mMeasuredWidth = measureWidth(widthMeasureSpec)
//        mMeasuredHeight = measureHeight(heightMeasureSpec)
//        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight)
//    }
//
//
//    private fun measureWidth(measureSpec: Int): Int {
//        var result = 0
//        val specMode = MeasureSpec.getMode(measureSpec)
//        val specSize = MeasureSpec.getSize(measureSpec)
//        if (specMode == MeasureSpec.EXACTLY) {
//            result = specSize
//        } else {
//            result = dip2px(200f)
//            if (specMode == MeasureSpec.AT_MOST) {
//                result = Math.min(result, specSize)
//            }
//        }
//        return result
//    }

//    private fun measureHeight(measureSpec: Int): Int {
//        var result = 0
//        val specMode = MeasureSpec.getMode(measureSpec)
//        val specSize = MeasureSpec.getSize(measureSpec)
//        if (specMode == MeasureSpec.EXACTLY) {
//            result = specSize
//        } else {
//            result = dip2px(200f)
//            if (specMode == MeasureSpec.AT_MOST) {
//                result = Math.min(result, specSize)
//            }
//        }
//        return result
//    }

    private fun initView() {
        LayoutInflater.from(mContext).inflate(R.layout.imui_chat_emotion_layout,this)
        mTabContainer = findViewById(R.id.llTabContainer)
        vpEmotion = findViewById(R.id.vpEmotion)
        initViewPager()
        initTabs()
        initClickListener()
        StickerManager.instance.updateSticker = object : IUpdateSticker {
            override fun addSticker(stickerItem: StickerItem) {
                emotionPageAdapter?.refreshAddStickerAdater(stickerItem)
            }

            override fun removeSticker(stickerItem: StickerItem) {
                emotionPageAdapter?.refreshDelStickerAdater(stickerItem)
            }

            override fun removeSticker(stickerItemList: List<StickerItem>) {
                emotionPageAdapter?.refreshStickerAdater(stickerItemList)
            }

            override fun notifyStickerPosition(from: Int,to:Int) {
                emotionPageAdapter?.notifyPosition(from,to)
            }
        }
        vpEmotion?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(postion: Int) {
                selectTab(postion)
            }
        })
        vpEmotion?.offscreenPageLimit = mTabContainer?.childCount!!
    }

    private fun initTabs() {
        addTab(0,R.drawable.vector_emoji_normal)
        addTab(1,R.drawable.vector_sticker_fav)
        addTab(2,R.drawable.vector_sticker_system)
        selectTab(0)
    }

    private fun addTab(posi:Int,resId:Int) {
        val view = EmotionTab(mContext!!,resId)
        mTabContainer?.addView(view)
        mTabViewArray.put(posi,view)
        view.setOnClickListener {
            selectTab(posi)
        }
    }

    private fun initClickListener() {

    }

    private fun selectTab(tabPosi: Int) {
        if (tabPosi == mTabViewArray.size() ) return
        for (i in 0 until mTabViewArray.size() ) {
            val tab = mTabViewArray[i]
            if (i == tabPosi) {
                tab.setBackgroundResource(R.drawable.shape_white_2dp)
            } else {
                tab.background = null
            }
        }
        vpEmotion?.setCurrentItem(tabPosi,true)

    }

    private fun initViewPager() {
        emotionPageAdapter = EmotionPageAdapter(mContext!!)
        vpEmotion?.adapter = emotionPageAdapter
    }

    fun dip2px(dipValue: Float): Int {
        val dm = context.applicationContext.resources.displayMetrics
        return (dipValue *  dm.density + 0.5f).toInt()
    }
}