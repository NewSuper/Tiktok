package com.aitd.module_login.vm

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.base.BaseViewModel
import com.aitd.library_common.base.Constans
import com.aitd.library_common.encrypt.AppCommonEncryptionUtils
import com.aitd.library_common.encrypt.EncryptHelper
import com.aitd.library_common.encrypt.EncryptRequest
import com.aitd.library_common.extend.launchDataResult
import com.aitd.library_common.network.BaseResponse
import com.aitd.library_common.utils.UniversalID
import com.aitd.module_login.R
import com.aitd.module_login.bean.CheckAccountBean
import com.aitd.module_login.bean.CheckEmailBean
import com.aitd.module_login.bean.LoginFaceRequest
import com.aitd.module_login.bean.VerificationInviteBean
import com.aitd.module_login.net.NetLoginProvider
import com.aitd.module_login.utils.Base64ImageUtil
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class LoginViewModel : BaseViewModel() {
    val ERROR_USER_PASSWORD = "STEB2003" //
    val ERROR_FACE_NOT_MATCH = "STEB2001" //人脸匹配失败
    val ERROR_OFFSITE_LOGIN = "STEB2000" //异地登录

    val sendSmsSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val showLoginDialog: MutableLiveData<Boolean> = MutableLiveData()
    val registSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val invatationCodeInfo: MutableLiveData<VerificationInviteBean> = MutableLiveData()
    val modifyStatus: MutableLiveData<Boolean> = MutableLiveData()
    val checkEmailBind: MutableLiveData<CheckEmailBean> = MutableLiveData()
    val loginResult = MutableLiveData<BaseResponse<Any>>()
    val checkAccount = MutableLiveData<CheckAccountBean>()
    val bindMailSuccess = MutableLiveData<Boolean>()


    fun sendSmsCode(account: String) {
        val request = kotlin.run {
            val map = HashMap<String, String>()
            map["account"] = account
            val business = Gson().toJson(map)
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), business)
        }
        launch({
            NetLoginProvider.requestService.get_sms(request)
        }, {
            sendSmsSuccess.postValue(true)
            ToastUtils.showShort(StringUtils.getString(R.string.code_send_success))
        }, {
            if (it.code == "502") {
                showLoginDialog.postValue(true)
            } else {
                ToastUtils.showShort(it.errorMessage)
            }
        }, true)
    }

    fun register(header: String, registerString: String) {
        val requestBody: RequestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registerString)
        launch({
            NetLoginProvider.requestService.register(header, requestBody)
        }, {
            ToastUtils.showShort(StringUtils.getString(R.string.register_success))
            registSuccess.postValue(true)
        }, {
            ToastUtils.showShort(it.errorMessage)
            registSuccess.postValue(false)
        }, true)

    }

    fun loadInvatationCodeInfo(invatationCode: String) {
        launch({
            NetLoginProvider.requestService.userMes(invatationCode)
        }, {
            it.data?.inviteCode = invatationCode
            invatationCodeInfo.postValue(it.data)
        }, {
            ToastUtils.showShort(it.errorMessage)
        }, false)
    }

    fun sendForgetSmsCode(account: String) {
        val map = HashMap<String, String>()
        map["account"] = account
        val business = Gson().toJson(map)
        val nonce = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val commonSign: String = AppCommonEncryptionUtils.getCommonSign(business, nonce, timestamp)
        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), business)
        launch({
            NetLoginProvider.requestService.get_findpwd_sms(
                commonSign,
                timestamp,
                nonce,
                requestBody
            )
        }, {
            sendSmsSuccess.postValue(true)
            ToastUtils.showShort(StringUtils.getString(R.string.code_send_success))
        }, {
            ToastUtils.showShort(it.errorMessage)
        }, true)
    }

    fun modifyPwd(account: String, password: String, code: String) {
        val requestMap = hashMapOf("account" to account, "password" to password, "code" to code)
        requestMap["deviceId"] = UniversalID.getUniversalID(BaseApplication.getAppContext())
        requestMap["operatingSystem"] = Constans.Key.DEVICE_OS.toString()
        val jsonString = Gson().toJson(requestMap)

        val signString = AppCommonEncryptionUtils.getRsaSign(jsonString)
        val nonce = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val commonString = AppCommonEncryptionUtils.getCommonSign(signString, nonce, timestamp)
        val request =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), signString)
        launch({
            NetLoginProvider.requestService.modifyPwd(
                commonString,
                timestamp,
                nonce,
                request
            )
        }, {
            modifyStatus.postValue(true)
        }, {
            ToastUtils.showShort(it.errorMessage)
        }, true)
    }

    fun checkEmailBinding(account: String) {
        launch({ NetLoginProvider.requestService.checkEmailBinding(account) }, {
            checkEmailBind.postValue(it.data)
        }, {}, true)
    }

    fun login(loginFaceRequest: LoginFaceRequest, bitmap: Bitmap?) {
        viewModelScope.launchDataResult({
            if (bitmap != null && TextUtils.isEmpty(loginFaceRequest.picToken)) {
                var base64Image = Base64ImageUtil.compressImageToBase64(bitmap)
                base64Image = "data:image/png;base64,${base64Image}"
                loginFaceRequest.faceImage = base64Image
            }
            val requestJson = Gson().toJson(loginFaceRequest)
            LogUtils.d("LoginViewModel", requestJson);
            //加密请求参数
            val encryptRequest = EncryptRequest()
            var headerRsaEncry = ""
            val bean: EncryptHelper.EncryptBean = EncryptHelper.encryptData(requestJson)
            if (bean != null) {
                encryptRequest.data = bean.aesParamContent
                headerRsaEncry = bean.rsaEncry
            }
            NetLoginProvider.requestService.Login(headerRsaEncry, encryptRequest)
        }, {
            loginResult.postValue(it)
        }, {
            if (it) {
                showDialog.postValue(true)
            } else {
                showDialog.postValue(false)
            }
        })
    }

    fun checkAccount(account: String) {
        launch(
            { NetLoginProvider.requestService.checkAccount(account) }, {
                checkAccount.postValue(it.data)
            }, {
                ToastUtils.showShort(it.errorMessage)
            }, true
        )
    }

    fun sendBindMailSmsCode(account: String) {
        val map = HashMap<String, String>()
        map["account"] = account
        val business = Gson().toJson(map)
        val nonce = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val commonSign: String = AppCommonEncryptionUtils.getCommonSign(business, nonce, timestamp)
        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), business)
        launch({
            NetLoginProvider.requestService.getBindEmailCode(
                commonSign,
                timestamp,
                nonce,
                requestBody
            )
        }, {
            sendSmsSuccess.postValue(true)
            ToastUtils.showShort(StringUtils.getString(R.string.code_send_success))
        }, {
            ToastUtils.showShort(it.errorMessage)
        }, true)
    }

    fun bindMail(content: String) {
        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content)
        launch({
            NetLoginProvider.requestService.updateBaseinfo(requestBody)
        }, {
            bindMailSuccess.postValue(true)
        }, {}, true)
    }
}