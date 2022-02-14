package com.tiktok.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.tiktok.PlayListActivity
import com.tiktok.R
import com.tiktok.adapter.WorkAdapter
import com.tiktok.base.BaseFragment
import com.tiktok.pojo.DataCreate
import com.tiktok.pojo.VideoBean
import kotlinx.android.synthetic.main.fragment_fans.*

class WorkFragment : BaseFragment() {
    private var listData = mutableListOf<VideoBean>()
    private val workAdapter by lazy {
        WorkAdapter()
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivity?.let {
            listData.clear()
            listData.addAll(DataCreate.datas)
            workAdapter.setNewInstance(listData)
            recyclerview!!.layoutManager = GridLayoutManager(activity, 3)
            recyclerview!!.adapter = workAdapter
            workAdapter.setOnItemClickListener { adapter, view, position ->
                PlayListActivity.initPos = position;
                requireActivity().startActivity(Intent(activity, PlayListActivity::class.java))
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_fans
    }
}