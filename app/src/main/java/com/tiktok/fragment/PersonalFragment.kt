package com.tiktok.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.tiktok.FocusActivity
import com.tiktok.R
import com.tiktok.ShowImageActivity
import com.tiktok.base.BaseFragment
import com.tiktok.base.CommPagerAdapter
import com.tiktok.pojo.CurUserBean
import com.tiktok.pojo.MainPageChangeEvent
import com.tiktok.pojo.VideoBean
import com.tiktok.utils.NumUtils
import com.tiktok.utils.RxBus
import kotlinx.android.synthetic.main.fragment_personal.*
import kotlinx.android.synthetic.main.personal_home_header.*
import rx.Subscription
import rx.functions.Action1

class PersonalFragment : BaseFragment() {
    private val fragments = ArrayList<Fragment>()
    private var pagerAdapter: CommPagerAdapter? = null
    private var curUserBean: VideoBean.UserBean? = null
    private var subscription: Subscription? = null
    override fun init(savedInstanceState: Bundle?) {
        //解决toolbar左边距问题
        toolbar!!.setContentInsetsAbsolute(0, 0)
        setappbarlayoutPercent()
        setUserInfo()

        ivReturn!!.setOnClickListener {
            RxBus.getInstance().post(MainPageChangeEvent(0))
        }
        ivHead!!.setOnClickListener {
            transitionAnim(ivHead, curUserBean!!.head)
        }

        llFocus!!.setOnClickListener {
            startActivity(Intent(activity, FocusActivity::class.java))
        }
        llFans!!.setOnClickListener {
            startActivity(Intent(activity, FocusActivity::class.java))
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_personal
    }


    /**
     * 过渡动画跳转页面
     *
     * @param view
     */
    fun transitionAnim(view: View?, res: Int) {
        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            requireView(),
            getString(R.string.trans)
        )
        val intent = Intent(activity, ShowImageActivity::class.java)
        intent.putExtra("res", res)
        ActivityCompat.startActivity(requireActivity(), intent, compat.toBundle())
    }

    fun setUserInfo() {
        subscription = RxBus.getInstance().toObservable(CurUserBean::class.java)
            .subscribe(Action1 { curUserBean: CurUserBean ->
                coordinatorLayoutBackTop()
                this.curUserBean = curUserBean.userBean
                ivBg!!.setImageResource(curUserBean.userBean.head)
                ivHead!!.setImageResource(curUserBean.userBean.head)
                tvNickname!!.text = curUserBean.userBean.nickName
                tvSign!!.text = curUserBean.userBean.sign
                tvTitle!!.text = curUserBean.userBean.nickName
                val subCount = NumUtils.numberFilter(curUserBean.userBean.subCount)
                val focusCount = NumUtils.numberFilter(curUserBean.userBean.focusCount)
                val fansCount = NumUtils.numberFilter(curUserBean.userBean.fansCount)

                //获赞 关注 粉丝
                tvGetLikeCount!!.text = subCount
                tvFocusCount!!.text = focusCount
                tvFansCount!!.text = fansCount

                //关注状态
                if (curUserBean.userBean.isFocused) {
                    tvAddfocus!!.text = "已关注"
                    tvAddfocus!!.setBackgroundResource(R.drawable.shape_round_halfwhite)
                } else {
                    tvAddfocus!!.text = "关注"
                    tvAddfocus!!.setBackgroundResource(R.drawable.shape_round_red)
                }
                setTabLayout()
            } as Action1<CurUserBean>)
    }

    private fun setTabLayout() {
        val titles = arrayOf(
            "作品 " + curUserBean!!.workCount,
            "动态 " + curUserBean!!.dynamicCount,
            "喜欢 " + curUserBean!!.likeCount
        )
        fragments.clear()
        for (i in titles.indices) {
            fragments.add(WorkFragment())
            tabLayout!!.addTab(tabLayout!!.newTab().setText(titles[i]))
        }
        pagerAdapter = CommPagerAdapter(childFragmentManager, fragments, titles)
        viewPager!!.adapter = pagerAdapter
        tabLayout!!.setupWithViewPager(viewPager)
    }

    /**
     * 根据滚动比例渐变view
     */
    private fun setappbarlayoutPercent() {
        appbarlayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appbarlayout, verticalOffset ->
            val percent = Math.abs(verticalOffset * 1.0f) / appbarlayout.totalScrollRange //滑动比例
            if (percent > 0.8) {
                tvTitle!!.visibility = View.VISIBLE
                tvFocus!!.visibility = View.VISIBLE
                val alpha = 1 - (1 - percent) * 5 //渐变变换
                tvTitle!!.alpha = alpha
                tvFocus!!.alpha = alpha
            } else {
                tvTitle!!.visibility = View.GONE
                tvFocus!!.visibility = View.GONE
            }
        })
    }

    /**
     * 自动回顶部
     */
    private fun coordinatorLayoutBackTop() {
        val behavior = (appbarlayout!!.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is AppBarLayout.Behavior) {
            val appbarlayoutBehavior = behavior
            val topAndBottomOffset = appbarlayoutBehavior.topAndBottomOffset
            if (topAndBottomOffset != 0) {
                appbarlayoutBehavior.topAndBottomOffset = 0
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscription != null) {
            subscription!!.unsubscribe()
        }
    }

}