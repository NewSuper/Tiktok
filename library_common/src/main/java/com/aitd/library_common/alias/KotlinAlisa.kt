package com.aitd.library_common.alias

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View

typealias showPassword = PasswordTransformationMethod
typealias hidePassword = HideReturnsTransformationMethod

typealias viewOnClick = ((v:View) -> Unit)