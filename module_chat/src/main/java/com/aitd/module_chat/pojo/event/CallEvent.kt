package com.aitd.module_chat.pojo.event

class CallEvent(var cmd: Short,
                var roomId: String,
                var sendType: String,
                var targetId: String,
                var type: String,
                var userId: String,
                var memebers: List<String>) {
    var data:String? = null
}