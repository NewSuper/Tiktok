package com.aitd.module_discover

import android.os.Bundle
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Discover.ROUTE_DISCOVER_FRAGMENT)
class DiscoverHomeFragment : BaseFragment() {
    override fun init(savedInstanceState: Bundle?) {

    }

    override fun getLayoutId(): Int {
        return R.layout.discover_activity_home
    }
}