package com.aitd.module_login.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.library_common.data.UserResponse
import com.aitd.library_common.dialog.SelectImgeFragment
import com.aitd.library_common.imageload.ImageLoader
import com.aitd.library_common.language.LanguageSpUtil
import com.aitd.library_common.language.LanguageType
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.utils.FilterUtil
import com.aitd.library_common.utils.MaxTextLengthFilter
import com.aitd.library_common.utils.SimpleTextWatcher
import com.aitd.module_login.R
import com.aitd.module_login.databinding.LoginActivityAddUserInfoBinding
import com.aitd.module_login.utils.CountryData
import com.aitd.module_login.vm.LoginViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.PermissionUtils
import kotlinx.coroutines.flow.*
import setTextString
import java.util.*

/**
 * Author : palmer
 * Date   : 2021/7/16
 * E-Mail : lxlfpeng@163.com
 * Desc   : 添加个人资料
 */
@Route(path = ARouterUrl.Login.ROUTER_SETTING_USER_ACTIVITY)
class AddUserInfoActivity : BaseMvvmActivity<LoginViewModel, LoginActivityAddUserInfoBinding>() {

    private var userData: UserResponse = BaseApplication.getUserBean()
    private var mSelectImgeFragment: SelectImgeFragment? = null
    val REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 1
    override fun init(savedInstanceState: Bundle?) {
        mBinding.etNickname.filters = arrayOf(
            FilterUtil.getEmojiInputFilter(
                this@AddUserInfoActivity,
                getString(R.string.filter_default)
            ), MaxTextLengthFilter(30)
        )
        mBinding.etNickname.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s.toString())) {
                    mBinding.txtNext.isEnabled = false
                } else {
                    mBinding.txtNext.isEnabled = true
                    userData.nickname = s.toString()
                }
            }
        })
        displayUserInfoData()
        mSelectImgeFragment = SelectImgeFragment()
        mSelectImgeFragment?.setPicterSelectListener {
            ImageLoader.Builder(it, mBinding.ivAvator).showAsCircle()
        }
//        mCommonBottomDialog = CommonBottomDialog(
//            this, mutableListOf(
//                getString(R.string.takephoto),
//                getString(R.string.seal_select_chat_bg_album)
//            )
//        )
//        mCommonBottomDialog?.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
//            mCommonBottomDialog?.dismiss()
//            when (position) {
//                0 -> {
//
//                }
//                1 -> {
//                    Intent(Intent.ACTION_GET_CONTENT).run {
//                        addCategory(Intent.CATEGORY_OPENABLE)
//                        type = "image/*"
//                        startActivityForResult(
//                            this,
//                            REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY
//                        )
//                    }
//                }
//            }
//        })
    }

    private fun displayUserInfoData() {
        userData.apply {
            ImageLoader.Builder(img, mBinding.ivAvator)
                .setErrorRes(R.mipmap.login_head_default)
                .setPlaceHolderRes(R.mipmap.login_head_default)
                .show()

            mBinding.etNickname.setTextString(userData.nickname)
            if (TextUtils.isEmpty(userData.countryCode)) {
                LanguageSpUtil.getLanguageType().let {
                    if (it.equals(LanguageType.SIMPLIFIED_CHINESE) || it.equals(
                            LanguageType.TRADITIONAL_CHINESE
                        )
                    ) {
                        userData.nationality = "65,新加坡"
                    } else {
                        userData.nationality = "65,Singapore"
                    }
                }
            } else {
                CountryData.getCountry().find { userData.countryCode == it.tel }?.let {
                    val nationality = LanguageSpUtil.getLanguageType().let { languageType ->
                        if (languageType.equals(LanguageType.SIMPLIFIED_CHINESE) || languageType.equals(
                                LanguageType.TRADITIONAL_CHINESE
                            )
                        ) {
                            it.tel + "," + it.name
                        } else {
                            it.tel + "," + it.en
                        }
                    }
                    userData.nationality = nationality
                }
            }
            userData.nationality.split(",").let {
                if (it.size > 1) {
                    mBinding.txtRegion.text = it[1]
                }
            }
            setGenderSelected()
        }
    }

    override fun getLayoutId(): Int = R.layout.login_activity_add_user_info

    fun viewOnclick(view: View) {
        when (view.id) {
            R.id.rl_avator -> {
                PermissionUtils.permission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                    .callback(object : PermissionUtils.FullCallback {
                        override fun onGranted(granted: MutableList<String>) {
                            // mCommonBottomDialog?.show()
                            mSelectImgeFragment?.show(supportFragmentManager, "")
                        }

                        override fun onDenied(
                            deniedForever: MutableList<String>,
                            denied: MutableList<String>
                        ) {
                        }
                    }).request()
            }
            R.id.txt_sex_man -> {
                userData.gender = "1"
                setGenderSelected()
            }
            R.id.txt_sex_woman -> {
                userData.gender = "0"
                setGenderSelected()
            }
            R.id.ll_region -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTER_SELECT_COUNTRY_ACTIVITY)
                    .navigation()
            }
            R.id.txt_next -> {
                val nickName: String = mBinding.etNickname.text.toString().trim { it <= ' ' }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
//            cropImage(data.data)
//
//
//            val uri = data.data
//            val cr = contentResolver
//            try {
//                if (uri != null) {
//                    val mBitmap = MediaStore.Images.Media.getBitmap(cr, uri) //显得到bitmap图片
//                    mBitmap?.let {
//                        launch {
//                            flow<String> {
//                                emit(QRCodeDecoder.syncDecodeQRCode(mBitmap))
//                            }.flowOn(Dispatchers.IO).onStart {
//                                showLoadingDialog()
//                            }.onCompletion {
//                                hideLoadingDialog()
//                            }.catch {
//                                ToastUtils.showShort(getString(com.huke.library_qrcode.R.string.choice_album_agin_tips))
//                            }.collect {
//                                Intent().run {
//                                    putExtra("result", it)
//                                    setResult(Activity.RESULT_OK, this)
//                                    finish()
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun cropImage(uri: Uri) {
        Intent("com.android.camera.action.CROP").apply {
            setDataAndType(uri, "image/*")

        }
    }

    private fun setGenderSelected() {
        if (TextUtils.isEmpty(userData.gender)) {
            userData.gender = "1"
        }
        mBinding.txtSexMan.setTextColor(
            if (userData.gender.equals("1")) ContextCompat.getColor(
                this@AddUserInfoActivity,
                R.color.common_white
            ) else ContextCompat.getColor(this@AddUserInfoActivity, R.color.common_text_gray_66)
        )
        mBinding.txtSexMan.setBackgroundResource(
            if (userData.gender.equals("1")) R.drawable.shape_5083fc_14dp else 0
        )
        mBinding.txtSexWoman.setTextColor(
            if (userData.gender.equals("1")) ContextCompat.getColor(
                this@AddUserInfoActivity,
                R.color.common_text_gray_66
            ) else ContextCompat.getColor(this@AddUserInfoActivity, R.color.common_white)
        )
        mBinding.txtSexWoman.setBackgroundResource(
            if (userData.gender.equals("1")) 0 else R.drawable.shape_5083fc_14dp
        )
    }
}