package com.tiktok.pojo

class CommentBean {
    var content: String? = null
        get() = if (field == null) "" else field
    var userBean: VideoBean.UserBean? = null
    var likeCount = 0
    var isLiked = false
}