package com.aitd.module_chat.rtc

enum class QXCallState {
    ERROR_NET,
    TIME_OUT,
    DISCONNECTED,
    HANGUP,
    OTHER_HANGUP,
    REFRUSE,
    OTHER_REFRUSE,
    CANCEL,
    OTHER_CANCEL,
    EXIT,
    MESSAGE_OBJECT_BUSING,
    NOT_FRIEND,
    ERROR_PARAM,
    UNKOWN
}