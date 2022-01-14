package com.aitd.module_login.ui

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.aitd.library_common.base.BaseMvvmActivity
import com.aitd.library_common.dialog.CommonDialog
import com.aitd.library_common.extend.gone
import com.aitd.library_common.extend.visible
import com.aitd.library_common.router.ARouterUrl
import com.aitd.module_login.R
import com.aitd.module_login.databinding.LoginActivityFindPwdBinding
import com.aitd.library_common.utils.RegexCheckUtils
import com.aitd.library_common.utils.SimpleTextWatcher
import com.aitd.library_common.alias.hidePassword
import com.aitd.library_common.alias.showPassword
import com.aitd.module_login.vm.LoginViewModel
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ToastUtils

/**
 * Author : palmer
 * Date   : 2021/6/23
 * E-Mail : lxlfpeng@163.com
 * Desc   : 找回密码
 */
@Route(path = ARouterUrl.Login.ROUTE_FINFPWD_ACTIVITY)
class FindPwdActivity : BaseMvvmActivity<LoginViewModel, LoginActivityFindPwdBinding>(),
    View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener {

    private var password = "" // 密码

    private var code = "" // 验证码

    private var surePwd = "" // 确认密码

    private var mCountDownTimer: CountDownTimer? = null

    @Autowired(name = "email")
    @JvmField
    var userName: String = ""

    @Autowired
    @JvmField
    var emailBindFlag: Int = 0

    override fun init(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        hasExtra()
        initHidePwd()
        getIsEmailShow()
        mBinding.edtFpSurePwd.onFocusChangeListener = this
        mBinding.edtFpSetPwd.onFocusChangeListener = this
        mBinding.edtFpCode.onFocusChangeListener = this
        mBinding.edtFpSetPwd.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                password = s.toString().trim { it <= ' ' }
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgFpSetPwdClose.visible()
                    checksShow(password)
                    if (RegexCheckUtils.isCheckPwd(password)) {
                        mBinding.lyForgetPwdShow.gone()
                    } else {
                        mBinding.lyForgetPwdShow.visible()
                    }
                } else {
                    mBinding.imgFpSetPwdClose.gone()
                    mBinding.lyForgetPwdShow.gone()
                }
            }
        })
        mBinding.edtFpSurePwd.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                surePwd = s.toString().trim { it <= ' ' }
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgFpSurePwdClose.visible()
                } else {
                    mBinding.imgFpSurePwdClose.gone()
                }
            }
        })
        mBinding.edtFpCode.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                code = s.toString()
                check()
                if (!TextUtils.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.imgFpCodeClose.visible()
                } else {
                    mBinding.imgFpCodeClose.gone()
                }
            }
        })
        mBinding.edtFpMobile.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBinding.tvFpSendCode.isClickable = !TextUtils.isEmpty(s.toString())
            }
        })
        mBinding.cbFpSurePwdHideShow.setOnCheckedChangeListener(this)
        mBinding.cbFpSetPwdHideShow.setOnCheckedChangeListener(this)
        mCountDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.tvFpSendCode.apply {
                    isClickable = false
                    text =
                        getString(R.string.reg_send_verification_code, millisUntilFinished / 1000)
                }
            }

            override fun onFinish() {
                mBinding.tvFpSendCode.apply {
                    text = getString(R.string.reg_get_verification_code)
                    isClickable = true
                }
            }
        }

        mBinding.edtFpMobile.setText(userName)

        initObserve()
    }

    private fun initObserve() {
        mViewModel.sendSmsSuccess.observe(this, Observer {
            if (it) {
                mCountDownTimer?.start()
            }
        })
        mViewModel.modifyStatus.observe(this, Observer {
            if (it) {
                ToastUtils.showShort(getString(R.string.find_pwd_success))
                finish()
            }
        })
    }

    /**
     * 检查是否正常
     * @param text
     */
    private fun checksShow(text: String) {
        if (RegexCheckUtils.isDigitBig(text)) {
            setHitShow(mBinding.imgMatcherContainsOne, mBinding.tvMatcherContainsOne, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsOne, mBinding.tvMatcherContainsOne, 0)
        }
        if (RegexCheckUtils.isDigitSmall(text)) {
            setHitShow(mBinding.imgMatcherContainsTwo, mBinding.tvMatcherContainsTwo, 1)
        } else {
            setHitShow(mBinding.imgMatcherContainsTwo, mBinding.tvMatcherContainsTwo, 0)
        }
        if (RegexCheckUtils.isDigit(text)) {
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
     * @param imageView
     * @param textView
     * @param tag 1.正常 2.错误
     */
    private fun setHitShow(imageView: ImageView, textView: TextView, tag: Int) {
        imageView.setImageResource(if (tag == 1) R.mipmap.login_ic_check_ok else R.mipmap.login_ic_check_fail)
        textView.setTextColor(if (tag == 1) Color.parseColor("#5083FC") else Color.parseColor("#666666"))
    }

    private fun check() {
        if (TextUtils.isEmpty(userName) || userName.length < 5) {
            mBinding.tvFpSureSubmit.isEnabled = false
            return
        }
        if (TextUtils.isEmpty(password) || !RegexCheckUtils.isCheckPwd(password)) {
            mBinding.tvFpSureSubmit.isEnabled = false
            return
        }
        if (TextUtils.isEmpty(code) || code.length < 6) {
            mBinding.tvFpSureSubmit.isEnabled = false
            return
        }
        if (TextUtils.isEmpty(surePwd) || surePwd.length < 5) {
            mBinding.tvFpSureSubmit.isEnabled = false
            return
        }
        mBinding.tvFpSureSubmit.isEnabled = true
    }

    private var commonDialog: CommonDialog? = null

    override fun getLayoutId(): Int = R.layout.login_activity_find_pwd

    fun viewClick(view: View) {
        when (view.id) {
            R.id.img_fp_back -> {
                finish()
            }
            R.id.tv_fp_sure_submit -> {
                subimitforgetPwd()
            }
            R.id.tv_fp_send_code -> {
                if (isCheckMobile()) {
                    mViewModel.sendForgetSmsCode(userName)
                }
            }
            R.id.img_fp_sure_pwd_close -> mBinding.edtFpSurePwd.setText("")
            R.id.img_fp_set_pwd_close -> mBinding.edtFpSetPwd.setText("")
            R.id.img_fp_code_close -> mBinding.edtFpCode.setText("")
        }
    }

    private fun subimitforgetPwd() {
        if (isCheckPwdEquals() && RegexCheckUtils.isCheckPwd(password)) {
            mViewModel.modifyPwd(userName, password, code)
        }
    }

    /**
     * 判断两密码是否相同
     * @return
     */
    private fun isCheckPwdEquals(): Boolean {
        var isPwd = false
        if (password == surePwd) {
            isPwd = true
        }
        mBinding.lyForgetPwdCheckDublePwd.visibility = if (isPwd) View.GONE else View.VISIBLE
        return isPwd
    }

    /**
     * 判断是否为空
     */
    private fun isCheckMobile(): Boolean {
        val isEmail = userName.contains("@")
        var isBack = true
        if (!isEmail) {
            if (RegexCheckUtils.judgeContainsStr(userName)) {
                ToastUtils.showShort(getString(R.string.phone_format_error))
                isBack = false
            }
        } else {
            if (!RegexCheckUtils.isEmail(userName)) {
                ToastUtils.showShort(getString(R.string.email_format_error))
                isBack = false
            }
        }
        return isBack
    }

    private fun hasExtra() {

        mBinding.edtFpMobile.setText(userName)
        val isEmail: Boolean = userName.contains("@")
        mBinding.edtFpMobileOrEmail.setHint(
            if (isEmail) getString(R.string.reg_emial) else getString(
                R.string.reg_mobile
            )
        )

//        if (intent.hasExtra(com.huke.socialcontact.ui.activity.users.NewForgetPwdActivity.KEY_USERNAME)) {
//            userName =
//                intent.getStringExtra(com.huke.socialcontact.ui.activity.users.NewForgetPwdActivity.KEY_USERNAME)
//            mEdtFpMobile.setText(userName)
//            val isEmail: Boolean = userName.contains("@")
//            mFyMobileOrEmial.setHint(if (isEmail) getString(R.string.reg_emial) else getString(R.string.reg_mobile))
//        }
//        if (intent.hasExtra(com.huke.socialcontact.ui.activity.users.NewForgetPwdActivity.KEY_BIND_FLAG)) {
//            emailBindFlag = intent.getIntExtra(
//                com.huke.socialcontact.ui.activity.users.NewForgetPwdActivity.KEY_BIND_FLAG,
//                0
//            )
//        }
    }

    private fun initHidePwd() {
        mBinding.cbFpSetPwdHideShow.isChecked = false
        mBinding.cbFpSurePwdHideShow.isChecked = false
        mBinding.edtFpSetPwd.transformationMethod = showPassword.getInstance()
        mBinding.edtFpSurePwd.transformationMethod = showPassword.getInstance()
    }

    private fun getIsEmailShow() {
        val isEmail: Boolean = userName.contains("@")
        if (!isEmail) {
            showGoBindEmail()
        }
    }

    private fun showGoBindEmail() {
        if (commonDialog == null) {
            commonDialog = CommonDialog(this)
        }
        commonDialog?.apply {
            setOnLeftClickListener(View.OnClickListener { commonDialog?.dismiss() })
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentText(getString(R.string.user_go_bind_email_hit))
            setRightButtonText(getString(R.string.sq_i_know))
            show()
            visibleTitle()
            goneCancel()
        }

    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        when (v.id) {
            R.id.edt_fp_sure_pwd -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineSurePwd,
                        Color.parseColor("#5083FC")
                    )
                } else {
                    mBinding.lyForgetPwdCheckDublePwd.gone()
                    setViewLineBackgroundColor(
                        mBinding.vUnderLineSurePwd,
                        Color.parseColor("#E9E9E9")
                    )
                }
            }
            R.id.edt_fp_set_pwd -> {
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
            R.id.edt_fp_code -> {
                if (hasFocus) {
                    setViewLineBackgroundColor(mBinding.vUnderLineCode, Color.parseColor("#5083FC"))
                } else {
                    setViewLineBackgroundColor(mBinding.vUnderLineCode, Color.parseColor("#E9E9E9"))
                }
            }
        }
    }

    private fun setViewLineBackgroundColor(viewLineBackgroundColor: View, paresColore: Int) {
        viewLineBackgroundColor.setBackgroundColor(paresColore)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.cb_fp_sure_pwd_hide_show -> {
                if (isChecked) {
                    // 显示密码
                    mBinding.edtFpSurePwd.transformationMethod = hidePassword.getInstance()
                } else {
                    // 隐藏密码
                    mBinding.edtFpSurePwd.transformationMethod = showPassword.getInstance()
                }
                mBinding.edtFpSurePwd.setSelection(mBinding.edtFpSurePwd.length())
            }
            R.id.cb_fp_set_pwd_hide_show -> {
                if (isChecked) {
                    // 显示密码
                    mBinding.edtFpSetPwd.transformationMethod = hidePassword.getInstance()
                } else {
                    // 隐藏密码
                    mBinding.edtFpSetPwd.transformationMethod = showPassword.getInstance()
                }
                mBinding.edtFpSetPwd.setSelection(mBinding.edtFpSetPwd.length())
            }
        }

    }
}