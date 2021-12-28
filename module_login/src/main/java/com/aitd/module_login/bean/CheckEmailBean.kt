package com.aitd.module_login.bean

class CheckEmailBean(
    var account: String,
    var email: String,
    var emailBindFlag: Int,// 是否绑定邮箱     0否:1是
    var smsFlag: Int// 是否开启短信通道 0关闭：1开启
)