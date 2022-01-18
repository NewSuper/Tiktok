package com.aitd.module_chat.pojo.event;

public enum  HeartBeatEvent {
    /**
     * 正常流程检测、发送心跳包
     */
    EVENT_CHECK_NORMALLY,
    /**
     * 立刻发送心跳包
     */
    EVENT_CHECK_IMMEDIATELY
}

