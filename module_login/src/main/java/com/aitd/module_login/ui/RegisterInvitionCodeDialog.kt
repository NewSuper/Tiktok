package com.aitd.module_login.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aitd.library_common.extend.gone
import com.aitd.library_common.extend.launchData
import com.aitd.library_common.extend.visible
import com.aitd.library_common.imageload.ImageLoader
import com.aitd.module_login.R
import com.aitd.module_login.bean.VerificationInviteBean
import com.aitd.module_login.databinding.LoginDialogRegisterInvitionCodeBinding
import com.aitd.module_login.net.NetLoginProvider
import com.aitd.library_common.utils.SimpleTextWatcher
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * Author: palmer
 * time: 2020/12/21
 * email:lxlfpeng@163.com
 * desc:
 */
class RegisterInvitionCodeDialog(
    private val mContext: Context,
    private val verificationInviteBean: VerificationInviteBean?
) : Dialog(
    mContext, R.style.tipdialog
), CoroutineScope by MainScope(), View.OnClickListener {
    private var inviteBean: VerificationInviteBean? = null
    lateinit var binding: LoginDialogRegisterInvitionCodeBinding
    lateinit var clickResult: (verificationInviteBean: VerificationInviteBean?) -> Unit


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.login_dialog_register_invition_code,
            null,
            false
        )
        setContentView(binding.root)
        // 设置宽度为屏宽, 靠近屏幕底部。
        val win = window
        // 一定要设置Background，如果不设置，window属性设置无效
        win!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //全屏化对话框
        val params = win.attributes
        params.gravity = Gravity.CENTER
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
        binding.txtLeftButton.setOnClickListener(this)
        binding.llRightButton.setOnClickListener(this)
        binding.etInput.addTextChangedListener(object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s)) {
                    binding.txtErrorTips.gone()
                    binding.confirm.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.common_text_gray_B8
                        )
                    )
                } else {
                    binding.confirm.setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            R.color.common_text_blue
                        )
                    )
                }
            }

        })
        if (verificationInviteBean == null) {
            binding.etInput.visibility = View.VISIBLE
            binding.txtTitle.text = mContext.getString(R.string.invitation_input_code)
            binding.confirm.text = mContext.getString(R.string.invitation_code_verify)
        } else {
            binding.txtTitle.text = mContext.getString(R.string.invitation_code_modify)
            binding.etInput.visibility = View.VISIBLE
            binding.clOldInfo.visibility = View.VISIBLE
            binding.confirm.text = mContext.getString(R.string.invitation_code_verify)
            Glide.with(mContext).load(verificationInviteBean.img)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(R.mipmap.login_head_default).error(R.mipmap.login_head_default)
                .into(binding.ivOldHeader)
            binding.ivOldName.text = verificationInviteBean.nickname
            binding.txtOldCode.text = verificationInviteBean.inviteCode
        }
    }

    fun setOnclick(click: (verificationInviteBean: VerificationInviteBean?) -> Unit) {
        clickResult = click
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.txt_left_button -> {
                dismiss()
            }
            R.id.ll_right_button -> {
                if (inviteBean != null) {
                    clickResult(inviteBean)
                    this.dismiss()
                    return
                }
                val inviteCode = binding.etInput.text.toString().trim()
                if (inviteCode.isNotEmpty()) {
                    binding.txtErrorTips.gone()
                    binding.progressBar1.visibility
                    binding.llRightButton.isClickable = false
                    // ToastUtils.showShort("请求网络数据")
                    launchData({ NetLoginProvider.requestService.userMes(inviteCode) }, {
                        inviteBean = it.data?.apply {
                            this.inviteCode = inviteCode
                            ImageLoader.Builder(img, binding.ivNewHeader).showAsCircle()
                            binding.ivNewName.text = nickname;
                            binding.txtNewCode.text = inviteCode;
                            binding.viewMask.visible()
                            binding.clNewInfo.visible()
                            binding.etInput.gone()
                        }
                        if (verificationInviteBean != null) {
                            binding.confirm.text =
                                mContext.getString(R.string.invitation_code_sure_change);
                            binding.clNewInfo.setBackgroundResource(R.drawable.shape_ebf2fe_5dp);
                            binding.txtReplaceTips.visible()
                        } else {
                            binding.confirm.text = mContext.getString(R.string.sure);
                        }
                    }, {
                        binding.txtErrorTips.visible()
                    }, {
                        if (it) {
                            binding.txtErrorTips.gone()
                            binding.progressBar1.visible()
                            binding.llRightButton.isClickable = false;
                        } else {
                            binding.progressBar1.gone()
                            binding.llRightButton.isClickable = true;
                        }
                    })
                }
            }
        }
    }

}