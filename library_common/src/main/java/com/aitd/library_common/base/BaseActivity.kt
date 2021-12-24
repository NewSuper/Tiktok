package com.aitd.library_common.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aitd.library_common.data.KickedEvent
import com.aitd.library_common.dialog.CommonLoadingProgressDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseActivity : AppCompatActivity(), IView {

    private var loadingProgressDialog: CommonLoadingProgressDialog? = null
    protected open var mActivity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getRealPageView(layoutInflater))
        mActivity = this
        ARouter.init(application)
        init(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    open fun getRealPageView(inflater: LayoutInflater): View {
        if (getLayoutId() == 0) {
            ToastUtils.showShort("Empty Layout")
        }
        return inflater.inflate(getLayoutId(), null)
    }

    open fun showLoadingDialog() {
        if (loadingProgressDialog == null) {
            loadingProgressDialog = CommonLoadingProgressDialog(this)
        }
        loadingProgressDialog?.let {
            if (!isFinishing && !it.isShowing) {
                it.showCancel(false)
            }
        }
    }

    open fun hideLoadingDialog() {
        loadingProgressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        hideLoadingDialog()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogout(event: KickedEvent) {
        finish()
    }
}