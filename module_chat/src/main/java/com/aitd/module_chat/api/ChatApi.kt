package com.aitd.module_chat.api

import com.aitd.library_common.network.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.util.HashMap

interface ChatApi {
    /**
     * 获取交易密码设置状态
     */
    @POST("SocialInsurance/user/digiccy/account/getUserPayPwdState")
    suspend fun getUserPayPwdState(@QueryMap map: HashMap<String, Any>): BaseResponse<Any>

    /**
     * 获取行情（币——USDT
     */
    @GET("SocialInsurance/user/digiccy/account/quotes/usdt")
    suspend fun getHangQing(@QueryMap map: HashMap<String, Any>): BaseResponse<Any>

    /**
     * 查询红包限制额度
     */
    @GET("SocialRedpacket/redPacket/coin/limit")
    suspend fun coinlimit(): BaseResponse<Any>
}