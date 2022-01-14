package com.aitd.module_login.ui

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.lifecycle.Observer
import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.library_common.extend.gone
import com.aitd.library_common.extend.visible
import com.aitd.library_common.router.ARouterUrl
import com.aitd.library_common.utils.SimpleTextWatcher
import com.aitd.library_common.utils.UniversalID
import com.aitd.module_login.R
import com.aitd.module_login.databinding.LoginActivityBindMailBinding
import com.aitd.library_common.utils.RegexCheckUtils
import com.aitd.module_login.vm.LoginViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import java.util.*

/**
 * Author : palmer
 * Date   : 2021/7/16
 * E-Mail : lxlfpeng@163.com
 * Desc   : 绑定邮箱
 */
@Route(path = ARouterUrl.Login.ROUTER_BIND_MAIL_ACTIVITY)
class BindMailActivity : BaseMvvmActivity<LoginViewModel, LoginActivityBindMailBinding>() {
    private var mEmail: String = ""
    private var mCode: String = ""
    private var mPassword: String = ""
    private var isEnabled = false
    private var isEmail = true
    private var mCountDownTimer: CountDownTimer? = null

    override fun init(savedInstanceState: Bundle?) {
        mBinding.tvRegSendCode.isEnabled = false
        mBinding.edtBindEmail.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mEmail = s.toString().trim { it <= ' ' }
                isEnabled = false
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgBindEmailClose.visible()
                    if (TextUtils.isEmpty(mEmail) || !RegexCheckUtils.isEmail(mEmail.trim { it <= ' ' })) {
                    } else {
                        isEnabled = true
                    }
                } else {
                    mBinding.imgBindEmailClose.gone()
                    mBinding.lyBindEmailErrorShow.gone()
                }
                setEdtEmail()
                mBinding.tvRegSendCode.isEnabled = isEnabled
                setEnabled()
            }
        })
        mBinding.edtBindEmailCode.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mCode = s.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(mCode)) {
                    setUnderLineBindEmail("#5083FC")
                    mBinding.imgBindEmailCodeClose.visible()
                } else {
                    mBinding.imgBindEmailCodeClose.gone()
                }
                setEnabled()
            }
        })
        mBinding.edtBindEmailInputPwd.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mPassword = s.toString().trim { it <= ' ' }
                if (!TextUtils.isEmpty(mPassword)) {
                    setUnderLineBindEmailPwd("#5083FC")
                    mBinding.imgBindEmailPwdClose.visible()
                } else {
                    mBinding.imgBindEmailPwdClose.gone()
                }
                setEdtEmailPwd()
                setEnabled()
            }
        })
        mBinding.edtBindEmail.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                setViewLineBackgroundColor(
                    mBinding.vUnderLineBindEmail,
                    Color.parseColor("#5083FC")
                )
            } else {
                setViewLineBackgroundColor(
                    mBinding.vUnderLineBindEmail,
                    Color.parseColor("#E9E9E9")
                )
            }
        }
        mBinding.cbBindEmailPwdHideShow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 显示密码
                mBinding.edtBindEmailInputPwd.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                // 隐藏密码
                mBinding.edtBindEmailInputPwd.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            mBinding.edtBindEmailInputPwd.setSelection(mBinding.edtBindEmailInputPwd.length())
        }
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
        mViewModel.sendSmsSuccess.observe(this, Observer {
            if (it) {
                mCountDownTimer?.start()
            }
        })
        mViewModel.bindMailSuccess.observe(this, Observer {

            if (it) {
                BaseApplication.getUserBean().email = mEmail
                setResult(3600)
                finish()
            }

        })
    }

    private fun setEdtEmail() {
        isEmail = true
        mBinding.lyBindEmailErrorShow.gone()
        setUnderLineBindEmail("#5083FC")
    }

    private fun setEdtEmailPwd() {
        setUnderLineBindEmailPwd("#5083FC")
        mBinding.lyLoginPwdInputError.gone()
    }

    private fun setEnabled() {
        mBinding.tvBindEmailSubmit.isEnabled =
            isEnabled && !TextUtils.isEmpty(mCode) && mCode.length >= 4 && !TextUtils.isEmpty(
                mPassword
            ) && mPassword.length >= 6
    }

    private var isLoginPwdEqual = false

    override fun getLayoutId(): Int = R.layout.login_activity_bind_mail

    fun viewOnclick(view: View) {
        when (view.id) {
            R.id.img_fp_back -> {
                finish()
            }
            R.id.tv_bind_email_submit -> {
                check()
                if (!isLoginPwdEqual) {
                    setUnderLineBindEmailPwd("#F26353")
                }
                if (!isEnabled) {
                    setUnderLineBindEmail("#F26353")
                }
                if (!isEmail) {
                    existedShow()
                }
                if (isLoginPwdEqual && isEnabled && isEmail) {
                    bindEmail()
                }
            }
            R.id.tv_reg_send_code -> {
                sendMessage()
            }
            R.id.img_bind_email_close -> {
                mBinding.edtBindEmail.setText("")
            }
            R.id.img_bind_email_code_close -> {
                mBinding.edtBindEmailCode.setText("")
            }
            R.id.img_bind_email_pwd_close -> {
                mBinding.edtBindEmailInputPwd.setText("")
            }
        }
    }

    private fun bindEmail() {
        val map = HashMap<String, Any>()
        val userSn: String = BaseApplication.getUserBean().userSn
        val deviceId: String = UniversalID.getUniversalID(this)
        map["operatingSystem"] = 1
        map["userSn"] = userSn
        map["deviceId"] = deviceId
        map["userId"] = BaseApplication.getUserBean().userId
        map["loginPassword"] = mPassword
        map["email"] = mEmail
        map["code"] = mCode
        val json = Gson().toJson(map)
        mViewModel.bindMail(json)
    }

    private fun sendMessage() {
        mViewModel.sendBindMailSmsCode(mEmail)
    }

    private fun check() {
        isLoginPwdEqual = false
        val loginPwd: String = "123456789"
        if (!TextUtils.isEmpty(loginPwd)) {
            if (!TextUtils.isEmpty(mPassword)) {
                if (loginPwd == mPassword) {
                    isLoginPwdEqual = true
                    mBinding.lyLoginPwdInputError.gone()
                } else {
                    mBinding.lyLoginPwdInputError.visible()
                }
            }
        }
        if (TextUtils.isEmpty(mEmail) || !RegexCheckUtils.isEmail(mEmail.trim { it <= ' ' })) {
            mBinding.tvMatcherContainsEmail.text = getString(R.string.email_format_error)
            mBinding.lyBindEmailErrorShow.visible()
        } else {
            isEnabled = true
            mBinding.lyBindEmailErrorShow.gone()
        }
    }

    private fun setUnderLineBindEmailPwd(color: String) {
        mBinding.vUnderLineSetPwd.setBackgroundColor(Color.parseColor(color))
    }

    private fun existedShow() {
        mBinding.tvMatcherContainsEmail.text = getString(R.string.user_mailbox_already_exists)
        mBinding.lyBindEmailErrorShow.visible()
        setUnderLineBindEmail("#F26353")
    }

    private fun setUnderLineBindEmail(color: String) {
        mBinding.vUnderLineBindEmail.setBackgroundColor(Color.parseColor(color))
    }

    private fun setViewLineBackgroundColor(viewLineBackgroundColor: View, paresColore: Int) {
        viewLineBackgroundColor.setBackgroundColor(paresColore)
    }

    override fun onDestroy() {
        super.onDestroy()
        mCountDownTimer?.onFinish()
        mCountDownTimer?.cancel()
    }
}