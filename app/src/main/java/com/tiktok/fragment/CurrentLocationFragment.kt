package com.tiktok.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tiktok.PlayListActivity
import com.tiktok.R
import com.tiktok.adapter.GridVideoAdapter
import com.tiktok.base.BaseFragment
import com.tiktok.pojo.DataCreate
import com.tiktok.pojo.VideoBean
import kotlinx.android.synthetic.main.fragment_current_location.*

class CurrentLocationFragment : BaseFragment() {
    private var listData = mutableListOf<VideoBean>()

    private val gridVideoAdapter by lazy {
        GridVideoAdapter()
    }


    override fun init(savedInstanceState: Bundle?) {
        DataCreate().initData()

        mActivity?.let {
            listData.clear()
            listData.addAll(DataCreate.datas)

            gridVideoAdapter.setNewInstance(listData)
            recyclerView!!.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            recyclerView!!.adapter = gridVideoAdapter

            refreshLayout!!.setColorSchemeResources(R.color.color_link)
            refreshLayout!!.setOnRefreshListener {
                object : CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        refreshLayout!!.isRefreshing = false
                    }
                }.start()
            }
            gridVideoAdapter.setOnItemClickListener { adapter, view, position ->
                PlayListActivity.initPos = position;
                requireActivity().startActivity(Intent(activity, PlayListActivity::class.java))
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_current_location
    }
}