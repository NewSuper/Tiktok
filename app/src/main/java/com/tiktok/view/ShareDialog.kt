package com.tiktok.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiktok.R
import com.tiktok.adapter.PrivateLetterAdapter
import com.tiktok.adapter.ShareAdapter
import com.tiktok.pojo.DataCreate
import com.tiktok.pojo.ShareBean
import kotlinx.android.synthetic.main.dialog_share.*

class ShareDialog : BaseBottomSheetDialog() {

    private var privateLetterAdapter: PrivateLetterAdapter? = null
    private var shareAdapter: ShareAdapter? = null
    private val shareBeans = ArrayList<ShareBean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.dialog_share, container)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        rvPrivateLetter!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        privateLetterAdapter = PrivateLetterAdapter()
        privateLetterAdapter!!.setNewInstance(DataCreate.userList)
        rvPrivateLetter!!.adapter = privateLetterAdapter
        rvShare!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        shareAdapter = ShareAdapter()
        shareAdapter!!.setNewInstance(shareBeans)
        rvShare!!.adapter = shareAdapter
        setShareDatas()
    }

    private fun setShareDatas() {
        shareBeans.add(ShareBean(R.string.icon_friends, "朋友圈", R.color.color_wechat_iconbg))
        shareBeans.add(ShareBean(R.string.icon_wechat, "微信", R.color.color_wechat_iconbg))
        shareBeans.add(ShareBean(R.string.icon_qq, "QQ", R.color.color_qq_iconbg))
        shareBeans.add(ShareBean(R.string.icon_qq_space, "QQ空间", R.color.color_qqzone_iconbg))
        shareBeans.add(ShareBean(R.string.icon_weibo, "微博", R.color.color_weibo_iconbg))
        shareBeans.add(ShareBean(R.string.icon_comment, "私信好友", R.color.color_FF0041))
        shareAdapter!!.notifyDataSetChanged()
    }

    protected override val height: Int
        protected get() = dp2px(requireContext(), 355f)
}