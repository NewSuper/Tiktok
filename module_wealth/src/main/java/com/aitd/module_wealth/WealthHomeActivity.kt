package com.aitd.module_wealth

import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Wealth.ROUTE_WEALTH_ACTIVITY)
class WealthHomeActivity : BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {

    }
    override fun getLayoutId(): Int = R.layout.wealth_home_activity
}