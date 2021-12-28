package com.aitd.library_common.network

import com.aitd.library_common.base.Constans
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Author : palmer
 * Date   : 2021/6/28
 * E-Mail : lxlfpeng@163.com
 * Desc   :
 */
object RetrofitProvider {
    private var mServiceMap: HashMap<String, Any> = HashMap()
    private var mOkHttpClient: OkHttpClient = OkHttpClientProvider.getDefaultOkHttpClient()
    private var mRetrofitClient: Retrofit

    init {
        mRetrofitClient = createRetrofitClient(mOkHttpClient)
    }

    private fun createRetrofitClient(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(Constans.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    fun <T> getApiService(serviceClass: Class<T>): T? {
        var result: T?
        synchronized(mServiceMap) {
            if (mServiceMap.containsKey(serviceClass.name)) {
                result = mServiceMap[serviceClass.name] as T
            } else {
                result = mRetrofitClient.create(serviceClass)
                mServiceMap.put(serviceClass.name, result!!)
            }
        }
        return result
    }
}