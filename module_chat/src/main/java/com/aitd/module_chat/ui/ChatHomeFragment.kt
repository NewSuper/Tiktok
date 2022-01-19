package com.aitd.module_chat.ui

import android.os.Bundle
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.base.BaseMvvmFragment
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_chat.R
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = ARouterUrl.Chat.ROUTE_CHAT_FRAGMENT)
class ChatHomeFragment:BaseFragment() {
    override fun init(saveInstanceState: Bundle?) {
    }

    override fun getLayoutId(): Int = R.layout.activity_chat_home
}