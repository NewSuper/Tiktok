package com.aitd.module_chat.api

import androidx.lifecycle.MutableLiveData
import com.aitd.library_common.base.BaseViewModel
import com.blankj.utilcode.util.ToastUtils
import java.util.HashMap

class ChatViewModel : BaseViewModel() {
    val userPayPwdStateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val hangQingSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val coinlimit: MutableLiveData<Boolean> = MutableLiveData()

    fun getPayState(userId: String) {
        val map = HashMap<String, Any>()
        map["userId"] = userId
        launch({
            ChatProvider.requestService.getUserPayPwdState(map)
        }, {
            userPayPwdStateSuccess.postValue(true)
        }, {
            ToastUtils.showShort(it.errorMessage)
        }, false)
    }

    fun getHangQing(){
        val map = HashMap<String, Any>()
        launch({
            ChatProvider.requestService.getHangQing(map)
        },{
            hangQingSuccess.postValue(true)
        },{
            ToastUtils.showShort(it.errorMessage)
        },false)
    }
    fun getCoinLimit(){
        launch({
           ChatProvider.requestService.coinlimit()
        },{
            hangQingSuccess.postValue(true)
        },{
            ToastUtils.showShort(it.errorMessage)
        },false)
    }
}