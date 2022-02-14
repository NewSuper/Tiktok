package com.tiktok

import android.os.Bundle
import com.tiktok.base.BaseActivity
import com.tiktok.fragment.RecommendFragment

class PlayListActivity:BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction().add(R.id.framelayout, RecommendFragment()).commit()
    }

    override fun getLayoutId(): Int {
       return  R.layout.activity_play_list
    }

    companion object {
        @JvmField
        var initPos = 0
    }
}