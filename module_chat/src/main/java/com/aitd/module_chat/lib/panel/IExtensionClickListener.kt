package com.aitd.module_chat.lib.panel

import android.content.Intent
import java.util.LinkedHashMap

interface IExtensionClickListener {

    fun onImageResult(data: LinkedHashMap<String, Int>?, sendOrigin: Boolean)

    fun onLocationResult(data: Intent)

    fun onFileReuslt(data: Intent)
}