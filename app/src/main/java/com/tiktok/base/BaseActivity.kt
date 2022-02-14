package com.tiktok.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar

abstract class BaseActivity : AppCompatActivity(), IView {
   // private var loadingProgressDialog: CommonLoadingProgressDialog? = null
    protected open var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getRealPageView(layoutInflater))
        mActivity = this
        init(savedInstanceState)
    }

    open fun getRealPageView(inflater: LayoutInflater): View {
//        if (getLayoutId() == 0) {
//            ToastUtils.showShort("Empty Layout")
//        }
        return inflater.inflate(getLayoutId(), null)
    }
//
//    open fun showLoadingDialog() {
//        if (loadingProgressDialog == null) {
//            loadingProgressDialog = CommonLoadingProgressDialog(this)
//        }
//        loadingProgressDialog?.let {
//            if (!isFinishing && !it.isShowing) {
//                it.showCancele(false)
//            }
//        }
//    }
//
//    open fun hideLoadingDialog() {
//        loadingProgressDialog?.let {
//            if (it.isShowing) {
//                it.dismiss()
//            }
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        hideLoadingDialog()
//    }

    /**
     * 设置状态栏颜色
     */
    protected fun setSystemBarColor(color: Int) {
        ImmersionBar.with(this).statusBarColor(color)
    }

    /**
     * 去除状态栏
     */
    protected fun hideStatusBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init()
    }

    /**
     * 保持不息屏
     */
    protected fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Activity退出动画
     */
    protected fun setExitAnimation(animId: Int) {
        overridePendingTransition(0, animId)
    }

    /**
     * 全屏
     */
    protected fun setFullScreen() {
        ImmersionBar.with(this).init()
    }
}