package com.aitd.module_login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.language.LanguageType
import com.aitd.library_common.language.MultiLanguageUtil
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_login.R
import com.aitd.module_login.adapter.LanguangAdapter
import com.aitd.module_login.databinding.LoginActivityLanguangBinding
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import java.util.*

/**
 * Author : palmer
 * Date   : 2021/7/5
 * E-Mail : lxlfpeng@163.com
 * Desc   : 多语言切换
 */
@Route(path = ARouterUrl.Login.ROUTER_LANGUANG_ACTIVITY)
class LanguangActivity : BaseActivity() {
    lateinit var mBinding: LoginActivityLanguangBinding

    @Autowired
    @JvmField
    var url: String = ""

    override fun init(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        mBinding.toolbarLanguang.setBackOnClickListener {
            finish()
        }
        val mLanguangData = mutableListOf(
            LanguageType.TRADITIONAL_CHINESE,
            LanguageType.SIMPLIFIED_CHINESE,
            LanguageType.US,
            LanguageType.KOREA,
            LanguageType.JAPAN
        )
        val madater = LanguangAdapter()
        MultiLanguageUtil.getAppSettingLocal(this).apply {
            LanguageType.TRADITIONAL_CHINESE.name
            val index = when (this) {
                Locale.TRADITIONAL_CHINESE -> 0
                Locale.SIMPLIFIED_CHINESE -> 1
                Locale.US -> 2
                Locale.KOREA -> 3
                Locale.JAPAN -> 4
                else -> 0
            }
            madater.selectedIndex = index
        }
        madater.setNewInstance(mLanguangData)
        madater.setOnItemClickListener { adapter, _, position ->
            madater.selectedIndex = position
            adapter.notifyDataSetChanged()
            MultiLanguageUtil.setAppLanguage(this, madater.data[position].locale)
            ARouter.getInstance().build(url)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation()
            overridePendingTransition(R.anim.activity_alpha_in, R.anim.activity_alpha_out)
            finish()
        }
        mBinding.rlvLanguang.adapter = madater
    }

    override fun getRealPageView(inflater: LayoutInflater): View {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun getLayoutId(): Int = R.layout.login_activity_languang
}