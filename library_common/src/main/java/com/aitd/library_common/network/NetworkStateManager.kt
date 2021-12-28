package com.aitd.library_common.network

import com.kunminx.architecture.ui.callback.UnPeekLiveData

/**
 * Author : palmer
 * Date   : 2021/6/25
 * E-Mail : lxlfpeng@163.com
 * Desc   : 网络变化管理者
 */
class NetworkStateManager private constructor() {

    val mNetworkStateCallback = UnPeekLiveData<NetState>()

    companion object {
        val instance: NetworkStateManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkStateManager()
        }
    }

}