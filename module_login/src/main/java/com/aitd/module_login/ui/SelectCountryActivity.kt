package com.aitd.module_login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.imageload.SpaceItemDecorations
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_login.R
import com.aitd.module_login.adapter.CountryCodeAdpater
import com.aitd.module_login.adapter.CountryCodeSearchAdapter
import com.aitd.module_login.bean.CountryCodeBean
import com.aitd.module_login.databinding.LoginActivitySelectCountryBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import me.yokeyword.indexablerv.IndexableLayout

@Route(path = ARouterUrl.Login.ROUTER_SELECT_COUNTRY_ACTIVITY)
class SelectCountryActivity : BaseActivity() {
    lateinit var mBing: LoginActivitySelectCountryBinding
    private var isSearch = false
    private val type = 0 //1  为选择地址
    val mRcContainer by lazy {
        mBing.rcCountryContainer
    }
    val mLayoutRc by lazy {
        mBing.layoutRc
    }
    private lateinit var mCodeSearchAdapter: CountryCodeSearchAdapter
    lateinit var mCountryCodeAdpater: CountryCodeAdpater


    override fun getLayoutId(): Int = R.layout.login_activity_select_country

    override fun getRealPageView(inflater: LayoutInflater): View {
        val realPageView = super.getRealPageView(inflater)
        mBing = DataBindingUtil.bind(realPageView)!!
        return realPageView
    }
    override fun init(saveInstanceState: Bundle?) {
        initView()
        analysis()
    }
    fun viewOnclick(view: View) {
        when (view.id) {
            R.id.tv_country_code_search -> {
                isSearch = true
                showOrHide(true)
                KeyboardUtils.showSoftInput(mBing.edtCountrySearch)
            }
            R.id.img_search_country_name_area_code_close -> {
                isSearch = false
                showOrHide(false)
                KeyboardUtils.hideSoftInput(mBing.edtCountrySearch)
            }
        }
    }
    private fun initView(){
        mCodeSearchAdapter = CountryCodeSearchAdapter(this)
        mCodeSearchAdapter.setShowMobCountryCode(type == 0)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRcContainer.layoutManager = linearLayoutManager
        mRcContainer.adapter = mCodeSearchAdapter
        mCodeSearchAdapter.setOnItemClickListen { bean -> goBack(bean) }
        mRcContainer.addItemDecoration(SpaceItemDecorations(0, 0, 0, 0))
        mCountryCodeAdpater = CountryCodeAdpater(this)
        mCountryCodeAdpater.setShowMobCountryCode(type == 0)
        mLayoutRc.setLayoutManager(LinearLayoutManager(this))
        //分隔线
        mLayoutRc.recyclerView.addItemDecoration(SpaceItemDecorations(0, 0, 0, 0))
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)))
        mLayoutRc.setCompareMode(IndexableLayout.MODE_FAST)
        mLayoutRc.setAdapter(mCountryCodeAdpater)
        mCountryCodeAdpater.setOnItemContentClickListener { _, _, _, entity ->
            goBack(entity)
        }
    }
    private fun goBack(entity: CountryCodeBean?) {
        val it = Intent()
        it.putExtra("nationality", entity)
        setResult(2, it)
        finish()
    }
    /**
     * 解析国际JSON数据
     */
    private fun analysis() {
//        val lang: String =
//            SpUtil.getInstance(BaseApplication.getInstance()).getString(SpUtil.LANGUAGE)
//        if ("ch_pan" == lang || "ch_jian" == lang) { //繁体
//            lag = 0
//        } else {
//            lag = 1
//        }
//        Runnable {
//            try {
//                val jsonArray = JSONArray(json)
//                for (i in 0 until jsonArray.length()) {
//                    val `object`: JSONObject = jsonArray.getJSONObject(i)
//                    val mItemBean = CountryCodeBean()
//                    mItemBean.setMobCountryCode(`object`.optString("tel"))
//                    mItemBean.setCountryCode(`object`.optString("short"))
//                    if (lag == 0) {
//                        mItemBean.setCountry(`object`.optString("name"))
//                    } else {
//                        mItemBean.setCountry(`object`.optString("en"))
//                    }
//                    if (mItemBean.getCountryCode().equals("HK") || mItemBean.getCountryCode()
//                            .equals("CN") || mItemBean.getCountryCode().equals("SG")
//                    ) {
//                        mThreeData.add(mItemBean)
//                    } else {
//                        mData.add(mItemBean)
//                    }
//                    mCommonData.add(mItemBean)
//                }
//                setData()
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//        }.run()
    }

    private fun showOrHide(isSearch: Boolean) {
        val layoutParams = mBing.vSearchShowLine.layoutParams
        layoutParams.height = ConvertUtils.dp2px(if (isSearch) 1f else 5f)
        mBing.vSearchShowLine.layoutParams = layoutParams

        mBing.btbSelected.visibility = if (isSearch) View.GONE else View.VISIBLE

        mBing.tvCountryCodeSearch.visibility = if (isSearch) View.GONE else View.VISIBLE
        mBing.layoutRc.setVisibility(if (isSearch) View.GONE else View.VISIBLE)
        mBing.lyCountryCodeSearch.visibility = if (isSearch) View.VISIBLE else View.GONE
        // mBing.mVSearchShowLine.setVisibility(if (isSearch) View.VISIBLE else View.GONE)
        // mBing.mVLineSearch.setVisibility(if (isSearch) View.VISIBLE else View.GONE)
    }

}