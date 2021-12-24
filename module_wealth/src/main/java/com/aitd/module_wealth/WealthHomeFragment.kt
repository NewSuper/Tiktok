package com.aitd.module_wealth

import android.os.Bundle
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Wealth.ROUTE_WEALTH_FRAGMENT)
class WealthHomeFragment : BaseFragment() {
    override fun init(savedInstanceState: Bundle?) {

    }

    override fun getLayoutId(): Int {
        return R.layout.wealth_home_activity
    }
}