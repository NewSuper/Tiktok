package com.aitd.module_login.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.library_common.base.Constans
import com.aitd.library_common.dialog.CommonDialog
import com.aitd.library_common.encrypt.EncryptHelper
import com.aitd.library_common.extend.gone
import com.aitd.library_common.extend.visible
import com.aitd.library_common.language.MultiLanguageUtil
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.utils.SimpleTextWatcher
import com.aitd.library_common.utils.StringUtil
import com.aitd.library_common.utils.UniversalID
import com.aitd.module_login.R
import com.aitd.module_login.bean.VerificationInviteBean
import com.aitd.module_login.databinding.LoginActivityRegisterBinding
import com.aitd.module_login.utils.LoginRegexUtils
import com.aitd.module_login.utils.NoSenseCaptchaUtils
import com.aitd.module_login.vm.LoginViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.netease.nis.captcha.Captcha.CloseType
import com.netease.nis.captcha.CaptchaListener
import java.util.*

/**
 * Author : palmer
 * Date   : 2021/6/22
 * E-Mail : lxlfpeng@163.com
 * Desc   :注册界面
 */
@Route(path = ARouterUrl.Login.ROUTE_REGISTER_ACTIVITY)
class RegisterActivity : BaseMvvmActivity<LoginViewModel, LoginActivityRegisterBinding>(),
    View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {
    private var isMobile = false
    private var mCode = ""
    private var mEmial = ""
    private var mMobile = ""
    private var mPwd = ""
    var isCheckOk = true
    private var inviteBean: VerificationInviteBean? = null

    private var mCountDownTimer: CountDownTimer? = null

    private var mInvitationCode = ""
    private var tipsDialog: CommonDialog? = null
    private val REQUESTCODEA = 1
    var inviteTipsDialog: CommonDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        mBinding.edtRegEmail.onFocusChangeListener = this
        mBinding.edtRegEmail.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mEmial = s.toString().trim()
                check()
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    mBinding.imgRegMobileOrEmailClose.visible()
                    if (TextUtils.isEmpty(mEmial) || !RegexUtils.isEmail(mEmial.trim())) {
                        mBinding.lyRegEmialShow.visible()
                    } else {
                        mBinding.lyRegEmialShow.gone()
                    }
                } else {
                    mBinding.imgRegMobileOrEmailClose.gone()
                    mBinding.lyRegEmialShow.gone()
                }
            }
        })
        mBinding.edtRegSetPwd.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mPwd = s.toString().trim()
                check()
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    mBinding.imgRegSetPwdClose.visible()
                    checksShow(mPwd)
                    if (LoginRegexUtils.isCheckPwd(mPwd)) {
                        mBinding.lyRegPwdShow.gone()
                    } else {
                        mBinding.lyRegPwdShow.visible()
                    }
                } else {
                    mBinding.imgRegSetPwdClose.gone()
                    mBinding.lyRegPwdShow.gone()
                }
            }
        })
        mBinding.edtRegCode.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mCode = s.toString().trim()
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgRegCodeClose.visible()
                } else {
                    mBinding.imgRegCodeClose.gone()
                }
            }
        })

        mBinding.edtRegCode.onFocusChangeListener = this
        mBinding.edtRegSetPwd.onFocusChangeListener = this
        mBinding.cbRegSetPwdHideShow.setOnCheckedChangeListener(this)
        mBinding.edtRegSetPwd.transformationMethod = PasswordTransformationMethod.getInstance()
        mBinding.tvRegSendCode.isEnabled = false
        mCountDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.tvRegSendCode.apply {
                    isClickable = false
                    text =
                        getString(R.string.reg_send_verification_code, millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                mBinding.tvRegSendCode.apply {
                    text = getString(R.string.reg_get_verification_code)
                    isClickable = true
                }
            }
        }

        initObserve()
    }

    private fun initObserve() {
        mViewModel.sendSmsSuccess.observe(this, androidx.lifecycle.Observer {
            mCountDownTimer?.start()
        })
        mViewModel.showLoginDialog.observe(this, androidx.lifecycle.Observer {
            if (!it) {
                return@Observer
            }
            CommonDialog(this).run {
                setOnLeftClickListener {
                    this.dismiss()
                    finish()
                }
                setContentText(
                    if (isMobile) getString(R.string.reg_phone_number_registered) else getString(
                        R.string.reg_email_registered
                    )
                )
                setRightButtonText(getString(R.string.common_go_login))
                setLeftButtonText(getString(R.string.cancel))
                show()
            }
        })
        mViewModel.registSuccess.observe(this, androidx.lifecycle.Observer {
            if (it) {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation()
                finish()
            }
        })
        mViewModel.invatationCodeInfo.observe(this, androidx.lifecycle.Observer {
            inviteBean = it
            displayInvitationInfo()
        })
    }

    /**
     * 判断是否为空
     */
    private fun check() {
        mBinding.tvRegSendCode.isEnabled = false
        if (isMobile) {
            if (TextUtils.isEmpty(mMobile) || mMobile.trim().length < 5) {
                mBinding.tvRegistSubmit.isEnabled = false
                return
            }
        } else {
            if (TextUtils.isEmpty(mEmial) || !RegexUtils.isEmail(mEmial.trim())) {
                mBinding.tvRegistSubmit.isEnabled = false
                return
            }
        }
        mBinding.tvRegSendCode.isEnabled = true
        if (TextUtils.isEmpty(mCode) || mCode.length < 6) {
            mBinding.tvRegistSubmit.isEnabled = false
            return
        }
        if (TextUtils.isEmpty(mPwd) || !LoginRegexUtils.isCheckPwd(mPwd)) {
            mBinding.tvRegistSubmit.isEnabled = false
            return
        }
        //        if (TextUtils.isEmpty(mNiname) || mNiname.length() < 1) {
//            mTvRegistSubmit.setEnabled(false);
//            return;
//        }
        if (!isCheckOk) {
            mBinding.tvRegistSubmit.isEnabled = false
            return
        }
        mBinding.tvRegistSubmit.isEnabled = true
    }

    /**
     * 检查是否正常
     *
     * @param text
     */
    private fun checksShow(text: String) {
        if (LoginRegexUtils.isDigitBig(text)) {
            setHitShow(mBinding.imgMatcherContainsOne, mBinding.tvMatcherContainsOne, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsOne, mBinding.tvMatcherContainsOne, 0)
        }
        if (LoginRegexUtils.isDigitSmall(text)) {
            setHitShow(mBinding.imgMatcherContainsTwo, mBinding.tvMatcherContainsTwo, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsTwo, mBinding.tvMatcherContainsTwo, 0)
        }
        if (LoginRegexUtils.isDigit(text)) {
            setHitShow(mBinding.imgMatcherContainsThree, mBinding.tvMatcherContainsThree, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsThree, mBinding.tvMatcherContainsThree, 0)
        }
        if (text.length in 8..12) {
            setHitShow(mBinding.imgMatcherContainsFour, mBinding.tvMatcherContainsFour, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsFour, mBinding.tvMatcherContainsFour, 0)
        }
    }

    /**
     * 根据状态显示
     *
     * @param imageView
     * @param textView
     * @param tag       1.正常 2.错误
     */
    private fun setHitShow(imageView: ImageView, textView: TextView, tag: Int) {
        imageView.setImageResource(if (tag == 1) R.mipmap.login_ic_check_ok else R.mipmap.login_ic_check_fail)
        textView.setTextColor(if (tag == 1) Color.parseColor("#5083FC") else Color.parseColor("#666666"))
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v!!.id) {
            R.id.edt_reg_email -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineMobileOrEmail,
                        Color.parseColor("#5083FC")
                    )
                } else {
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineMobileOrEmail,
                        Color.parseColor("#E9E9E9")
                    )
                }
            }
            R.id.edt_reg_code -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(mBinding.vUnderLineCode, Color.parseColor("#5083FC"))
                } else {
                    setViewLineBackgroundColor(mBinding.vUnderLineCode, Color.parseColor("#E9E9E9"))
                }
            }
            R.id.edt_reg_set_pwd -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineSetPwd,
                        Color.parseColor("#5083FC")
                    )
                } else {
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineSetPwd,
                        Color.parseColor("#E9E9E9")
                    )
                }
            }
        }
    }

    /**
     * 公用
     *
     * @param viewLineBackgroundColor
     * @param paresColore
     */
    private fun setViewLineBackgroundColor(viewLineBackgroundColor: View, paresColore: Int) {
        viewLineBackgroundColor.setBackgroundColor(paresColore)
    }

    override fun getLayoutId(): Int = R.layout.login_activity_register

    fun viewClick(view: View) {
        when (view.id) {
            R.id.img_reg_mobile_or_email_close -> if (isMobile) {
                mBinding.edtRegMobile.setText("")
            } else {
                mBinding.edtRegEmail.setText("")
            }
            R.id.img_reg_code_close -> mBinding.edtRegCode.setText("")
            R.id.img_reg_set_pwd_close -> mBinding.edtRegSetPwd.setText("")
            R.id.img_reg_set_niname_close -> {
            }
            R.id.img_reg_invitation_code_close -> mBinding.edtRegInvitationCode.setText("")
            R.id.img_fp_back -> finish()
//            R.id.tv_select_mobile -> if (!DoubleClickUtil.isFastClick()) {
//                SelectCountryCodeActivity.goToActivity(mActivity)
//            }
            R.id.tv_reg_send_code -> {
                sendMessage()
            }
            R.id.tv_regist_submit -> {
                checkEmailOrMobile()
            }
            R.id.tv_reg_agreement -> {
                val appSettingLocal = MultiLanguageUtil.getAppSettingLocal(this)
                var file = "tradeagreement_jian.pdf"
                if (appSettingLocal.equals(Locale.TRADITIONAL_CHINESE)) {
                    file = "tradeagreement.pdf"
                }
                ARouter.getInstance().build(ARouterUrl.PDFViewer.ROUTE_PDF_ACTIVITY)
                    .withString("file", file)
                    .withString("title", getString(R.string.yonghuxieyi)).navigation()
            }
            R.id.rl_input_invatation -> {
                RegisterInvitionCodeDialog(this, null).apply {
                    setOnclick {
                        inviteBean = it
                        displayInvitationInfo()
                    }
                }.show()
            }
            R.id.iv_sao_yi_sao -> {
                ARouter.getInstance().build(ARouterUrl.QRCode.ROUTE_SCAN_ACTIVITY)
                    .navigation(this, REQUESTCODEA)
            }
            R.id.iv_change_invitation -> {
                if (inviteBean != null) {
                    RegisterInvitionCodeDialog(this, inviteBean).apply {
                        setOnclick {
                            inviteBean = it
                            displayInvitationInfo()
                        }
                    }.show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUESTCODEA && resultCode == Activity.RESULT_OK && data != null) {
            val resultCode = data.getStringExtra("result")
            resultCode?.let {
                if (it.contains("inviteCode=")) {
                    val inviteCode = StringUtil.getOneParameter(it, "inviteCode")
                    loadInvatationCodeInfo(inviteCode)
                } else {
                    if (inviteTipsDialog == null) {
                        inviteTipsDialog = CommonDialog(this).apply {
                            setContentText(getString(R.string.choice_pic_agin_tips))
                            setOnLeftClickListener {
                                ARouter.getInstance().build(ARouterUrl.QRCode.ROUTE_SCAN_ACTIVITY)
                                    .navigation(this@RegisterActivity, REQUESTCODEA)
                            }
                        }
                    }
                    inviteTipsDialog?.show()
                }
            }
        }
    }

    private fun sendMessage() {
        mViewModel.sendSmsCode(mEmial)
    }

    /**
     * 判断是否为空
     */
    private fun checkEmailOrMobile() {
        if (inviteBean != null) {
            mInvitationCode = inviteBean!!.inviteCode!!
        }
        if (isMobile) {
            if (mMobile.isEmpty()) {
                ToastUtils.showShort(getString(R.string.phone_not_null1))
                return
            }
            if (LoginRegexUtils.judgeContainsStr(mMobile)) {
                ToastUtils.showShort(getString(R.string.phone_format_error))
                return
            }
        } else {
            if (mEmial.isEmpty()) {
                ToastUtils.showShort(getString(R.string.email_not_null))
                return
            }
            if (!LoginRegexUtils.isEmail(mEmial)) {
                ToastUtils.showShort(getString(R.string.email_format_error))
                return
            }
        }
        if (mInvitationCode.isEmpty()) {
            if (tipsDialog == null) {
                tipsDialog = CommonDialog(this)
            }
            tipsDialog?.apply {

                setCancelClickListener(View.OnClickListener {
                    tipsDialog?.dismiss()
                    //图形验证码
                    initNeteaseCaptcha()
                })
                setOnLeftClickListener(View.OnClickListener {
                    tipsDialog?.dismiss()
                    RegisterInvitionCodeDialog(this@RegisterActivity, null).run {
                        setOnclick {
                            inviteBean = it
                            displayInvitationInfo()
                        }
                        show()
                    }
                })
                banCancelable()
                setTitleText(getString(R.string.tip))
                setContentText(getString(R.string.invate_code_tip))
                setLeftButtonText(getString(R.string.continue_to_register))
                setRightButtonText(getString(R.string.invitation_input_code))
                show()
            }
        } else {
            if (mInvitationCode.isNotEmpty()) {
                val lastchar: String = mInvitationCode.trim { it <= ' ' }
                    .substring(
                        mInvitationCode.trim { it <= ' ' }.length - 1,
                        mInvitationCode.trim { it <= ' ' }.length
                    )
                if (lastchar.equals("a", ignoreCase = true) || lastchar.equals(
                        "b",
                        ignoreCase = true
                    ) || lastchar.equals("c", ignoreCase = true) || lastchar.equals(
                        "d",
                        ignoreCase = true
                    )
                ) {
                    //图形验证码
                    initNeteaseCaptcha()
                } else {
                    ToastUtils.showShort(getString(R.string.valid_invate_code))
                }
            }
        }
    }

    private fun initNeteaseCaptcha() {
        //图形验证码
        NoSenseCaptchaUtils.getCaptcha(this, object : CaptchaListener {
            override fun onReady() {}
            override fun onValidate(s: String, s1: String, s2: String) {
                if (!TextUtils.isEmpty(s1)) {
                    //验证成功
                    runOnUiThread { newRegister(s1) }
                } else {
                    //验证失败
                }
            }

            override fun onError(i: Int, s: String) {}
            override fun onClose(closeType: CloseType) {}
        }).validate()

    }

    /**
     * 新用户注册
     */
    private fun newRegister(neCaptchaValidate: String) {
        val map = HashMap<String, String>()
        var type = ""
        if (isMobile) {
            type = "2" // 2手机注册
            map["account"] = mMobile
            // map["countryCode"] = countryCode
        } else {
            type = "3" //3邮箱注册
            map["account"] = mEmial
        }
        map["code"] = mCode
        val bean: EncryptHelper.EncryptBean? = EncryptHelper.encryptData(mPwd)
        var encry = ""
        var password = ""
        if (null != bean) {
            password = bean.aesParamContent
            encry = bean.rsaEncry
        }
        map["password"] = password
        if (!TextUtils.isEmpty(mInvitationCode)) {
            val lastchar = mInvitationCode.trim { it <= ' ' }
                .substring(
                    mInvitationCode.trim { it <= ' ' }.length - 1,
                    mInvitationCode.trim { it <= ' ' }.length
                )
            Log.e("proxyUserSn= ", "lastchar= $lastchar")
            if (lastchar.equals("a", ignoreCase = true)) {
                map["proxyUserSn"] = mInvitationCode.trim { it <= ' ' }
                    .substring(0, mInvitationCode.trim { it <= ' ' }.length - 1)
                map["shareType"] = "1"
                Log.e("proxyUserSn= ", mInvitationCode.trim { it <= ' ' }
                    .substring(0, mInvitationCode.trim { it <= ' ' }.length - 1))
            } else if (lastchar.equals("b", ignoreCase = true)) {
                map["proxyUserSn"] = mInvitationCode.trim { it <= ' ' }
                    .substring(0, mInvitationCode.trim { it <= ' ' }.length - 1)
                map["shareType"] = "2"
            } else if (lastchar.equals("c", ignoreCase = true)) {
                map["proxyUserSn"] = mInvitationCode.trim { it <= ' ' }
                    .substring(0, mInvitationCode.trim { it <= ' ' }.length - 1)
                map["shareType"] = "3"
            } else if (lastchar.equals("d", ignoreCase = true)) {
                map["proxyUserSn"] = mInvitationCode.trim { it <= ' ' }
                    .substring(0, mInvitationCode.trim { it <= ' ' }.length - 1)
                map["shareType"] = "4"
            }
        }
        //v2新增参数
        map["type"] = type
        map["regDevice"] = Build.MANUFACTURER
        map["regDeviceId"] = UniversalID.getUniversalID(this)
        map["operatingSystem"] = java.lang.String.valueOf(Constans.Key.DEVICE_OS)
        map["neCaptchaValidate"] = neCaptchaValidate
        val queryString = Gson().toJson(map)
        mViewModel.register(encry, queryString)
    }

    private fun displayInvitationInfo() {
        mBinding.rlInputInvatation.gone()
        mBinding.llInvatationInfo.visible()
        Glide.with(this).load(inviteBean!!.img)
            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .placeholder(R.mipmap.login_head_default)
            .error(R.mipmap.login_head_default).into(mBinding.ivInviteesHeader)
        mBinding.ivInviteesName.text = inviteBean!!.nickname
        mBinding.txtInvitationCode.text = inviteBean!!.inviteCode
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.cb_reg_set_pwd_hide_show -> {
                if (isChecked) {
                    // 显示密码
                    mBinding.edtRegSetPwd.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    // 隐藏密码
                    mBinding.edtRegSetPwd.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
                mBinding.edtRegSetPwd.setSelection(mBinding.edtRegSetPwd.length())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDownTimer?.onFinish()
        mCountDownTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        this.window.decorView.post { //把获取到的内容打印出来
            val copyStr: String = ClipboardUtils.getText().toString()
            if (!TextUtils.isEmpty(copyStr) && copyStr.contains("aitd=")) {
                val copyData = copyStr.substring(5, copyStr.length)
                loadInvatationCodeInfo(copyData)
                ClipboardUtils.clear()
            }
        }
    }

    private fun loadInvatationCodeInfo(invatationCode: String) {
        mViewModel.loadInvatationCodeInfo(invatationCode)
    }
}