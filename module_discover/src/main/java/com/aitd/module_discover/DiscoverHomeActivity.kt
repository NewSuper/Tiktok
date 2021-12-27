package com.aitd.module_discover

import android.os.Bundle
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Discover.ROUTE_DISCOVER_ACTIVITY)
class DiscoverHomeActivity : BaseActivity() {


    override fun init(savedInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int =R.layout.discover_activity_home
}