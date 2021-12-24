package com.aitd.library_common.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils

abstract class BaseFragment : Fragment(), IView {
    protected open var mViewContent: View? = null
    protected open var TAG: String = this.javaClass.simpleName
    protected open var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mViewContent == null) {
            mViewContent = getRealPageView(inflater, container).apply {
                if (parent != null && parent is ViewGroup) {
                    (parent as ViewGroup).removeView(mViewContent)
                }
            }
        }
        return mViewContent
    }

    open fun getRealPageView(inflater: LayoutInflater, container: ViewGroup?): View {
        if (getLayoutId() == 0) {
            ToastUtils.showShort("Empty Layout")
        }
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity = null
    }

    open fun showLoadingDialog() {
        activity?.let {
            if (it is BaseActivity) {
                it.showLoadingDialog()
            }
        }
    }

    open fun hideLoadingDialog() {
        activity?.let {
            if (it is BaseActivity) {
                it.hideLoadingDialog()
            }
        }
    }
}