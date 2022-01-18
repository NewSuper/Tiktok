package com.aitd.module_chat.view

import android.content.Context
import com.aitd.library_common.utils.ThreadPoolUtils
import com.aitd.module_chat.R
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.file.KitStorageUtils
import com.aitd.module_chat.utils.qlog.QLog
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.imui_pop_bottom.view.*
import java.io.File
import java.io.IOException

class BottomPop(var con: Context, var url: String, var mediaType: String) : BottomPopupView(con) {

    override fun getImplLayoutId(): Int = R.layout.imui_pop_bottom

    override fun onCreate() {
        super.onCreate()

        imui_pop_bottom_cancel.setOnClickListener {
            dismiss()
        }

        imui_pop_bottom_save.setOnClickListener {
            dismiss()
            try {
                if (url.isNullOrEmpty()) {
                    ToastUtil.toast(con, "${con.getString(R.string.qx_download_failed, "url is null")}")
                } else {
                    var file = File(url)
                    if (file != null) {
                        if (url.startsWith("file:///")) {
                            file = File(url.substring("file:///".length - 1))
                        }
                        QLog.e("BottomPop", "${file.absolutePath}")
                        ThreadPoolUtils.run {
                            var result = KitStorageUtils.saveMediaToPublicDir(context, file, mediaType)
                            val toastPath = when (mediaType) {
                                "image" -> KitStorageUtils.getImageSavePath(con)
                                "video" -> KitStorageUtils.getVideoSavePath(con)
                                else -> KitStorageUtils.getFileSavePath(con)
                            }
                            post {
                                if (result) {
                                    ToastUtil.toast(con, "${con.getString(R.string.qx_download_save_path, "${toastPath}")}")
                                } else {
                                    ToastUtil.toast(con, "${con.getString(R.string.qx_download_failed, "")}")
                                }
                            }
                        }
                    } else {
                        ToastUtil.toast(con, "${con.getString(R.string.qx_download_failed, "")}")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}