package com.tiktok.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.tiktok.R
import com.tiktok.adapter.FansAdapter
import com.tiktok.base.BaseFragment
import com.tiktok.pojo.DataCreate
import com.tiktok.pojo.VideoBean
import kotlinx.android.synthetic.main.fragment_fans.*

class FansFragment : BaseFragment() {
    private var listData = mutableListOf<VideoBean.UserBean>()
    private val fansAdapter by lazy {
        FansAdapter()
    }

    override fun init(savedInstanceState: Bundle?) {
        DataCreate().initData()
        mActivity?.let {
            listData.clear()
            listData.addAll(DataCreate.userList)
            fansAdapter.setNewInstance(listData)
            recyclerview!!.layoutManager = GridLayoutManager(activity, 3)
            recyclerview!!.adapter = fansAdapter
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_fans
    }
}