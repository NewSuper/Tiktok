package com.aitd.library_common.extend

import com.aitd.library_common.network.BaseResponse
import com.aitd.library_common.network.ExceptionHandle
import com.aitd.library_common.network.ServerException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> CoroutineScope.launchData(
    api: suspend CoroutineScope.() ->BaseResponse<T>,
    success:suspend (BaseResponse<T>)->Unit,
    error:suspend (ExceptionHandle.ResponeThrowable)->Unit ={},
    loading:suspend (Boolean)->Unit={}
) {
    this.launch {
        loading(true)
        try {
            withContext(Dispatchers.IO){
                val result = api()
                withContext(Dispatchers.Main){
                    result.code?.let {
                        if (it in arrayListOf<String>("200","209","204")){
                            success(result)
                        }else{
                            if(it == "STEB245"){
                                //
                            }
                            error(
                                ExceptionHandle.handleException(
                                    ServerException(result.code,result.msg)
                                )
                            )
                        }
                    }
                }
            }
        }catch (e:Exception){
            error(ExceptionHandle.handleException(e))
        }finally {
            loading(false)
        }
    }
}
//不对服务器返回的错误状态码做任何处理需要自己实现
fun<T> CoroutineScope.launchDataResult(
    api:suspend CoroutineScope.()->BaseResponse<T>,
    result: suspend (BaseResponse<T>)->Unit,
    loading: suspend (Boolean) -> Unit={}
){
    this.launch {
        loading(true)
        try {
            withContext(Dispatchers.IO){
                val result = api()

            }
        }catch (e:Exception){
            error(ExceptionHandle.handleException(e))
        }finally {
            loading(false)
        }
    }

}