package com.aitd.module_main

import android.os.Bundle
import android.os.CountDownTimer
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.utils.PreferenceUtils
import com.alibaba.android.arouter.launcher.ARouter

class SplashActivity : BaseActivity() {
    private var countDownTimer: CountDownTimer? = null

    override fun init(savedInstanceState: Bundle?) {
        countDownTimer = object : CountDownTimer(1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) = Unit
            override fun onFinish() {
                val isLogin = false
                if (isLogin) {
                    ARouter.getInstance().build(ARouterUrl.Main.ROUTE_MAIN_ACTIVITY).navigation()
                } else {
                    val isFirstGuide: Boolean = PreferenceUtils.getBoolean(this@SplashActivity, "firstGuide")
                    if (isFirstGuide) {
                        ARouter.getInstance().build(ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY).navigation()
                    } else {
                        ARouter.getInstance().build(ARouterUrl.Main.ROUTE_GUIDE_ACTIVITY).navigation()
                    }
                }
                finish()
            }
        }.start()
    }

    override fun getLayoutId(): Int = R.layout.main_activity_splash

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}