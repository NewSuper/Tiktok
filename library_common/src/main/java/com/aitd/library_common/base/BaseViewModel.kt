package com.aitd.library_common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitd.library_common.extend.launchData
import com.aitd.library_common.network.BaseResponse
import com.aitd.library_common.network.ExceptionHandle
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import kotlinx.coroutines.CoroutineScope

class BaseViewModel:ViewModel() {

    var showDialog:UnPeekLiveData<Boolean> = UnPeekLiveData() //多状态显示
    fun<T> launch (
        api:suspend CoroutineScope.() -> BaseResponse<T>,
        success:suspend (BaseResponse<T>) ->Unit,
        error:suspend (ExceptionHandle.ResponeThrowable) -> Unit ={},
        isShowLoading: Boolean = false
    ){
        var loading : suspend (Boolean)->Unit = {}
        if (isShowLoading){
            loading = {
                if (it){
                    showDialog.postValue(true)
                }else{
                    showDialog.postValue(false)
                }
            }
        }
        viewModelScope.launchData(api,success,error,loading)
    }
}