package com.aitd.module_login.bean

class VerificationInviteBean(
    var msg: String? = null,
    var img: String? = null,
    var nickname: String? = null,
    var status: String? = null,//0 未验证 1验证成功 2验证失败
    var userId: String? = null,
    var inviteCode: String? = null
)