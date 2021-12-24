package com.aitd.library_common.network

class BaseResponse<T> {
    val data: T? = null
    val msg: String? = null
    val code: String? = null
}