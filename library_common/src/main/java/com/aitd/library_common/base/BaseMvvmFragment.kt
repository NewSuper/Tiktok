package com.aitd.library_common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseMvvmFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseFragment() {
    protected open lateinit var mBinding: DB
    protected open lateinit var mViewModel: VM
    override fun getRealPageView(inflater: LayoutInflater, container: ViewGroup?): View {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false)
        mBinding.lifecycleOwner = this
        var tClass: Class<VM>? = null
        try {
            val type = javaClass.genericSuperclass as ParameterizedType
            val actualTypeArguments: Array<Type> = type.actualTypeArguments
            tClass = actualTypeArguments[0] as Class<VM>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (tClass != null) {
            mViewModel = createViewModel(tClass)
            mViewModel.showDialog.observe(this, Observer {
                if (it) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            })
        }
        return mBinding.root
    }
    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel){
        viewModels.forEach {viewModel ->
            viewModel.showDialog.observe(this, Observer {
                if (it) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            })
        }
    }
    /**
     * 创建ViewModel
     */
    private fun createViewModel(clazz: Class<VM>): VM = ViewModelProvider(this).get(clazz)
}