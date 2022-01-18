package com.aitd.module_chat.pojo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ReqCreateGroup(var groupId:String,var members:List<String>,var name:String): Parcelable {
    override fun toString(): String {
        return "ReqCreateGroup(groupId='$groupId', members=$members, name='$name')"
    }
}