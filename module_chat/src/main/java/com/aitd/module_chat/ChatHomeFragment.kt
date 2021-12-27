package com.aitd.module_chat

import android.os.Bundle
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Chat.ROUTE_CHAT_FRAGMENT)
class ChatHomeFragment : BaseFragment() {
    override fun init(savedInstanceState: Bundle?) {

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat_home
    }
}