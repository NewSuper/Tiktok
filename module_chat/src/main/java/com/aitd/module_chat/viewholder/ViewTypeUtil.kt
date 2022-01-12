package com.aitd.module_chat.viewholder

import com.aitd.module_chat.pojo.MessageType
import java.util.HashMap

object ViewTypeUtil {

    var mViewTypeMap = HashMap<String, Int>()
    val TYPE_LEFT = "Message_Left"
    val TYPE_RIGHT = "Message_Right"
    init {
        mViewTypeMap[MessageType.TYPE_NOTICE] = ViewType.type_notice
        mViewTypeMap[MessageType.TYPE_RECALL] = ViewType.type_recall
        mViewTypeMap[TYPE_LEFT] = ViewType.type_left
        mViewTypeMap[TYPE_RIGHT] = ViewType.type_right
    }

    object ViewType {
        val type_notice = 0
        val type_recall = type_notice + 1
        val type_left = type_recall + 1
        val type_right = type_left + 1

    }
}