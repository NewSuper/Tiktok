package com.aitd.module_login.ui

import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_login.R
import com.alibaba.android.arouter.facade.annotation.Route


@Route(path = ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY)
class LoginHomeActivity : BaseActivity() {


    override fun init(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int = R.layout.login_activity_login
}