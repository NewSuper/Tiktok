package com.aitd.module_mine

import android.os.Bundle
import android.view.View
import com.aitd.library_common.base.BaseFragment
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Author : palmer
 * Date   : 2021/6/11
 * E-Mail : lxlfpeng@163.com
 * Desc   :
 */
@Route(path = ARouterUrl.Mine.ROUTE_MINE_FRAGMENT)
class MineHomeFragment : BaseFragment() {
    override fun init(savedInstanceState: Bundle?) {
//        mViewContent?.findViewById<View>(R.id.chat_textview)?.setOnClickListener {
//            showLoadingDialog()
//        }
    }

    override fun getLayoutId(): Int {
        return R.layout.mine_activity_home
    }
}