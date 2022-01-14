package com.aitd.module_login.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.library_common.data.UserResponse
import com.aitd.library_common.dialog.CommonDialog
import com.aitd.library_common.extend.gone
import com.aitd.library_common.extend.visible
import com.aitd.library_common.imageload.ImageLoader
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.statistics.EventConstant
import com.aitd.library_common.statistics.MobclickAgent
import com.aitd.library_common.utils.PreferenceUtils
import com.aitd.library_common.utils.UniversalID
import com.aitd.module_login.R
import com.aitd.module_login.bean.LoginFaceRequest
import com.aitd.module_login.databinding.LoginActivityLoginBinding
import com.aitd.library_common.utils.RegexCheckUtils
import com.aitd.module_login.utils.LoginResultHelper
import com.aitd.module_login.utils.NoSenseCaptchaUtils
import com.aitd.library_common.utils.SimpleTextWatcher
import com.aitd.module_login.vm.LoginViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.netease.nis.captcha.Captcha
import com.netease.nis.captcha.Captcha.CloseType
import com.netease.nis.captcha.CaptchaListener

@Route(path = ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY)
class LoginHomeActivity : BaseMvvmActivity<LoginViewModel, LoginActivityLoginBinding>(),
    View.OnFocusChangeListener {

    private var isCute = false //默认为fale 没有切换

    private var isFaceLogin = false // false 默认刷脸显示 切换密码切换

    private var mMobileOrEmail = ""

    private var mPwd = ""

    private var tipsDialog: CommonDialog? = null

    override fun init(savedInstanceState: Bundle?) {
        mBinding.edtDlPwd.transformationMethod = PasswordTransformationMethod.getInstance()
        mBinding.etMobileOrEmail.onFocusChangeListener = this
        mBinding.edtDlPwd.onFocusChangeListener = this
        mBinding.dlCbPasswordEye.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 显示密码
                mBinding.edtDlPwd.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                // 隐藏密码
                mBinding.edtDlPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            mBinding.edtDlPwd.setSelection(mBinding.edtDlPwd.length())
        }
        mBinding.edtDlPwd.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mPwd = s.toString().trim { it <= ' ' }
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgDlPwdClose.visible()
                } else {
                    mBinding.imgDlPwdClose.gone()
                }
            }
        })
        mBinding.etMobileOrEmail.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mMobileOrEmail = s.toString().trim { it <= ' ' }
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgDlMobileOrEmailClose.visible()
                } else {
                    mBinding.imgDlMobileOrEmailClose.gone()
                }
            }
        })
        initObser()
        showHeadPicCut()
    }

    private fun initObser() {
        mViewModel.checkEmailBind.observe(this, Observer {
            if (it.smsFlag === 0 && TextUtils.isEmpty(it.email)) {
                ToastUtils.showShort(R.string.user_no_email_error)
            } else {
                if (TextUtils.isEmpty(it.email)) {
                    it.email = mMobileOrEmail
                }
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_FINFPWD_ACTIVITY)
                    .withString("email", it.email).withInt("emailBindFlag", it.emailBindFlag)
                    .navigation()
            }
        })
        mViewModel.loginResult.observe(this, Observer {
            when (it.code) {
                "STEB2003" -> {
                    showPasswordErrorDialog(Gson().toJson(it.data))
                }
                "200" -> {
                    val userResponse = Gson().fromJson(
                        Gson().toJson(it.data),
                        UserResponse::class.java
                    )
                    //没有人脸，有实名认证
                    if (null != userResponse && !userResponse.isFaceFlag && "1" == userResponse.authState && userResponse.isImgFlag) {
                        //  realNameFaceLoginUserResponse = userResponse
                        showNoFaceHasAuthDialog(userResponse, mMobileOrEmail, mPwd)
                    } else {
                        MobclickAgent.getInstance().eventAction(
                            this,
                            EventConstant.LOGIN,
                            "登录",
                            "成功登录",
                            EventConstant.LOGIN_PAGE
                        )
                        LoginResultHelper.login(this, userResponse, mMobileOrEmail, mPwd)
                    }

                }
                else -> {
                    ToastUtils.showShort(it.msg)
                }
            }
        })
        mViewModel.checkAccount.observe(this, Observer {
            if (!it.accountIsExist) {
                ToastUtils.showShort(getString(R.string.login_check_account_no_exist))
            } else if (!it.faceExist) {
                ToastUtils.showShort(getString(R.string.login_check_account_no_face))
            } else {
                //TODO 人脸识别
                //  FaceRecognitionActivity.startFaceLogin(this, mMobileOrEmail)
            }
        })
    }

    private var noFaceHasAuthDialog: CommonDialog? = null

    private fun showNoFaceHasAuthDialog(
        userResponse: UserResponse,
        username: String,
        password: String
    ) {
        if (noFaceHasAuthDialog == null) {
            noFaceHasAuthDialog = CommonDialog(this).apply {
                setTitleText(getString(R.string.tip))
                setLeftButtonText(resources.getString(R.string.login_redirect))
                setRightButtonText(resources.getString(R.string.login_face))
                setContentText(resources.getString(R.string.login_auth_state_dialog_tip))
                setCancelClickListener(View.OnClickListener {
                    dismiss()
                    //  MobclickAgent.getInstance()
                    //      .eventAction(mActvity, EventConstant.LOGIN, "登录", "成功登录", EventConstant.LOGIN_PAGE)
                    //  LoginResultHelper.login(mActvity, userResponse, username, password)
                })
                setOnLeftClickListener(View.OnClickListener {
                    dismiss()
                    //FaceRecognitionActivity.startFaceLogin(mActvity, mMobileOrEmail, true)
                })
            }
        }
        noFaceHasAuthDialog?.show()
    }

    private fun showPasswordErrorDialog(errorCount: String) {
        if (tipsDialog == null) {
            tipsDialog = CommonDialog(this).apply {
                setOnLeftClickListener(View.OnClickListener { dismiss() })
                banCancelable()
                setRightButtonText(getString(R.string.onbtnr))
                val htmlStr = java.lang.String.format(
                    getString(R.string.face_passwd_error_count_tip),
                    "<font color='#0181FF'>$errorCount</font>"
                )
                setContentText(Html.fromHtml(htmlStr).toString())
                setTitleText(getString(R.string.tip))
                show()
                goneCancel()
            }
        }
    }

    private fun check() {
        if (isFaceLogin) {
            mBinding.tvDlFaceLogin.isEnabled =
                !(TextUtils.isEmpty(mMobileOrEmail) || mMobileOrEmail.length < 5)
        }
        if (TextUtils.isEmpty(mMobileOrEmail) || mMobileOrEmail.length < 5) {
            mBinding.tvLoginSubmit.isEnabled = false
            return
        }
        if (TextUtils.isEmpty(mPwd) || mPwd.length < 8) {
            mBinding.tvLoginSubmit.isEnabled = false
            return
        }
        mBinding.tvLoginSubmit.isEnabled = true
    }

    private fun showHeadPicCut() {
        val username: String = PreferenceUtils.getString(this, "login_username");
        if (username.isEmpty()) {
            mBinding.lyHeadPicCute.gone()
        } else {
            mBinding.rlDlChooseLanguage.gone()
            mBinding.tvDlPasswordOrFaceLogin.text = getString(R.string.dl_swipe_face_login)
            mBinding.lyLoggedInMobileOrEmail.gone()
            mBinding.lyHeadPicCute.visible()
            mBinding.etMobileOrEmail.setText(username)
            mBinding.tvMobileOrEmail.text = username
            val userImg: String = PreferenceUtils.getString(this, "user_img")
            if (null != userImg) {
                ImageLoader.Builder(userImg, mBinding.imgLoginHead)
                    .setPlaceHolderRes(R.mipmap.login_head_default).showAsCircle()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.login_activity_login
    fun viewClick(view: View) {
        when (view.id) {
            // 跳转注册
            R.id.tv_dl_sign_up_now -> {
                ARouter.getInstance().build(ARouterUrl.Login.ROUTE_REGISTER_ACTIVITY).navigation()
            }
            R.id.tv_dl_retrieve_password -> {
                if (!TextUtils.isEmpty(mMobileOrEmail) && mMobileOrEmail.length > 5) {
                    mViewModel.checkEmailBinding(mMobileOrEmail)
                } else {
                    ToastUtils.showShort(getString(R.string.email_not_null))
                }
            }
            R.id.img_dl_pwd_close -> mBinding.edtDlPwd.setText("")
            R.id.img_dl_mobile_or_email_close -> mBinding.etMobileOrEmail.setText("")
            R.id.tv_dl_choose_language -> {
                //跳转选择语言
                ARouter.getInstance().build(ARouterUrl.Login.ROUTER_LANGUANG_ACTIVITY)
                    .withString("url", ARouterUrl.Login.ROUTE_LOGIN_ACTIVITY).navigation()
            }
            R.id.tv_login_submit -> {
                checkLogin()
            }
            R.id.tv_account_switch -> {
                mBinding.tvMobileOrEmail.text = ""
                cutShow()
                isCute = true
                mBinding.etMobileOrEmail.setText("")
                mBinding.edtDlPwd.setText("")
                mBinding.tvLoginSubmit.isEnabled = false
                mBinding.rlDlChooseLanguage.visibility = View.VISIBLE
            }
            R.id.tv_dl_password_or_face_login -> {
                if (isFaceLogin) {
                    isFaceLogin = false
                    mBinding.tvDlPasswordOrFaceLogin.text = getString(R.string.dl_swipe_face_login)
                    mBinding.lyPwdInput.visibility = View.GONE
                } else {
                    isFaceLogin = true
                    mBinding.tvDlPasswordOrFaceLogin.text =
                        getString(R.string.exit_login_btn_password_login)
                }
                showFaceOrAcountLogin(isFaceLogin)
                if (TextUtils.isEmpty(
                        mBinding.tvMobileOrEmail.text.toString().trim()
                    )
                ) {
                    mBinding.etMobileOrEmail.setText("")
                }
                mBinding.edtDlPwd.setText("")
            }
            R.id.tv_dl_face_login -> {
                if (!TextUtils.isEmpty(mMobileOrEmail)) {
                    mViewModel.checkAccount(mMobileOrEmail)
                } else {
                    if (!TextUtils.isEmpty(mBinding.tvMobileOrEmail.text.toString().trim())) {
                        mViewModel.checkAccount(
                            mBinding.tvMobileOrEmail.getText().toString().trim { it <= ' ' })
                    }
                }
            }
        }
    }

    private fun setViewLineBackgroundColor(viewLineBackgroundColor: View, paresColore: Int) {
        viewLineBackgroundColor.setBackgroundColor(paresColore)
    }

    /**
     * 切换显示登录按钮或刷脸登录
     *
     * @param faceLogin
     */
    private fun showFaceOrAcountLogin(faceLogin: Boolean) {
        mBinding.tvLoginSubmit.visibility = if (faceLogin) View.GONE else View.VISIBLE
        mBinding.tvDlFaceLogin.visibility = if (faceLogin) View.VISIBLE else View.GONE
        mBinding.lyPwdInput.visibility = if (faceLogin) View.GONE else View.VISIBLE
        mBinding.vPwdHide.visibility = if (faceLogin) View.VISIBLE else View.GONE
        if (isCute) {
            mBinding.lyLoggedInMobileOrEmail.visibility = View.VISIBLE
            mBinding.vPwdHide.visibility = View.GONE
        } else {
            val username: String = PreferenceUtils.getString(this, "login_username")
            if (TextUtils.isEmpty(username)) {
                mBinding.lyLoggedInMobileOrEmail.visibility = View.VISIBLE
            } else {
                mBinding.lyLoggedInMobileOrEmail.visibility = View.GONE
            }
        }
    }

    /**
     * 切换显示
     */
    private fun cutShow() {
        mBinding.edtDlPwd.clearFocus() //失去焦点
        mBinding.etMobileOrEmail.requestFocus() //获取焦点
        mBinding.tvDlPasswordOrFaceLogin.text = getString(R.string.dl_swipe_face_login)
        mBinding.lyLoggedInMobileOrEmail.visibility = View.VISIBLE
        mBinding.tvDlFaceLogin.visibility = View.GONE
        mBinding.tvLoginSubmit.visibility = View.VISIBLE
        mBinding.lyPwdInput.visibility = View.VISIBLE
        mBinding.lyHeadPicCute.visibility = View.GONE
    }

    /**
     * 账号登录
     */
    private fun checkLogin() {
        val status = mMobileOrEmail.contains("@")
        if (status) {
            if (!RegexCheckUtils.isEmail(mMobileOrEmail.trim { it <= ' ' })) {
                ToastUtils.showShort(getString(R.string.email_format_error))
                return
            }
        }
        initNeteaseCaptcha()
    }

    //图形验证码
    private fun initNeteaseCaptcha() {
        NoSenseCaptchaUtils.getCaptcha(this, object : CaptchaListener {
            override fun onReady() {}
            override fun onValidate(s: String, validate: String, s2: String) {
                if (!TextUtils.isEmpty(validate)) {
                    runOnUiThread { //验证成功
                        mViewModel.login(LoginFaceRequest().apply {
                            imageCode = 0
                            neCaptchaValidate = validate
                            account = mMobileOrEmail
                            password = mPwd
                            operatingSystem = 1
                            regDevice = Build.MANUFACTURER
                            regDeviceId =
                                UniversalID.getUniversalID(BaseApplication.getAppContext())
                            type = LoginFaceRequest.TYPE_MOBILE_EMAIL
                            picToken = null
                        }, null)
                    }
                }
            }

            override fun onError(i: Int, s: String) {}
            override fun onClose(closeType: CloseType) {}
        }).validate()
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when (v.id) {
            R.id.et_mobile_or_email -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(
                        mBinding.vDlUnderLineMobileOrEmail,
                        Color.parseColor("#5083FC")
                    )
                } else {
                    setViewLineBackgroundColor(
                        mBinding.vDlUnderLineMobileOrEmail,
                        Color.parseColor("#E9E9E9")
                    )
                }
            }
            R.id.edt_dl_pwd -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(
                        mBinding.vDlUnderLinePwd,
                        Color.parseColor("#5083FC")
                    )
                } else {
                    setViewLineBackgroundColor(
                        mBinding.vDlUnderLinePwd,
                        Color.parseColor("#E9E9E9")
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //避免内存泄露
        Captcha.getInstance().destroy()
    }
}