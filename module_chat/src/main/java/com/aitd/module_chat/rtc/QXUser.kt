package com.aitd.module_chat.rtc

import android.os.Parcelable
import android.view.SurfaceView
import kotlinx.android.parcel.Parcelize

@Parcelize
class QXUser(var userId: String = "") : Parcelable {
    var surfaceView: SurfaceView? = null
}