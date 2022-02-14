package com.tiktok

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.tiktok.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun init(savedInstanceState: Bundle?) {
        setFullScreen()
        object : CountDownTimer(500, 500) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }.start()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }
}