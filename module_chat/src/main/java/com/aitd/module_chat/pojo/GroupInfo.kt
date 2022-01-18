package com.aitd.module_chat.pojo

class GroupInfo(
    var groupName: String,
    var members: List<GroupUser>
)

class GroupUser(
    var userId: String,
    var name: String,
    var icon: String
)