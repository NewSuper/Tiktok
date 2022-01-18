package com.aitd.module_chat.listener

import android.net.Uri

interface IAudioPlayListener {
    fun onStart(uri: Uri?)
    fun onStop(uri: Uri?)
    fun onComplete(uri: Uri?)
}
