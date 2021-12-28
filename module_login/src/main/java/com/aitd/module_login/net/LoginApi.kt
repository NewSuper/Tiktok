package com.aitd.module_login.net

import com.aitd.library_common.encrypt.EncryptRequest
import com.aitd.library_common.network.BaseResponse
import com.aitd.module_login.bean.CheckAccountBean
import com.aitd.module_login.bean.CheckEmailBean
import com.aitd.module_login.bean.VerificationInviteBean
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Author : palmer
 * Date   : 2021/4/15
 * E-Mail : lxlfpeng@163.com
 * Desc   :
 */
interface LoginApi {
    /**
     * 根据邀请码查询用户信息
     */
    @GET("SocialFinance/share/invite/code/userMes")
    suspend fun userMes(@Query("inviteCode") inviteCode: String): BaseResponse<VerificationInviteBean>

    /**
     * 獲取驗證碼  登錄用的
     */
    @POST("SocialFinance/user/code")
    suspend fun get_sms(@Body rout: RequestBody): BaseResponse<Any>

    /**
     * 註冊//适配人脸登录API修改
     */
    @POST("SocialFinance/user/v2/regist")
    suspend fun register(
        @Header("encry") rsaEncry: String,
        @Body rout: RequestBody
    ): BaseResponse<Any>

    /**
     * 獲取找回驗證碼
     */
    @POST("SocialFinance/user/password/code")
    suspend fun get_findpwd_sms(
        @Header("commonSign") commonSign: String,
        @Header("timestamp") timestamp: Long,
        @Header("nonce") nonce: String, @Body rout: RequestBody
    ): BaseResponse<Any>

    /**
     *修改密码
     * */
    @POST("SocialFinance/user/v2/forgetPass")
    suspend fun modifyPwd(
        @Header("commonSign") commonSign: String,
        @Header("timestamp") timestamp: Long,
        @Header("nonce") nonce: String,
        @Body rout: RequestBody
    ): BaseResponse<Any>

    /**
     * 判断是否绑定邮箱
     */
    @GET("SocialFinance/user/v2/user/account")
    suspend fun checkEmailBinding(@Query("account") account: String): BaseResponse<CheckEmailBean>

    /**
     * 查询账号是否存在
     */
    @GET("SocialFinance/user/v2/checkAccount")
    suspend fun checkAccount(@Query("account") account: String): BaseResponse<CheckAccountBean>

    /**
     *登录
     */
    @POST("SocialFinance/user/v2/login")
    suspend fun Login(
        @Header("encry") rsaEncry: String,
        @Body request: EncryptRequest
    ): BaseResponse<Any>


    /**
     * 绑定邮箱获取验证码
     */
    @POST("SocialFinance/user/bind/email/code")
    suspend fun getBindEmailCode(
        @Header("commonSign") commonSign: String,
        @Header("timestamp") timestamp: Long,
        @Header("nonce") nonce: String,
        @Body body: RequestBody
    ): BaseResponse<Any>


    /**
     * 個人信息修改
     */
    @POST("SocialFinance/baseinfo/update")
    suspend fun updateBaseinfo(@Body rout: RequestBody): BaseResponse<Any>
}