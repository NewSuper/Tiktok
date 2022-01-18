package com.aitd.library_common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object Base64ImageUtil {
    /**
     * 伟入url.先压缩后转base64
     *
     * @param srcPath
     * @return
     */
    fun getImageBase64(srcPath: String?): String? {
        try {
            val newOpts = BitmapFactory.Options()
            newOpts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(srcPath, newOpts)
            newOpts.inJustDecodeBounds = false
            val w = newOpts.outWidth
            val h = newOpts.outHeight
            val hh = 1280f
            val ww = 720f
            var be = 1
            if (w > h && w > ww) {
                be = (newOpts.outWidth / ww).toInt()
            } else if (w < h && h > hh) {
                be = (newOpts.outHeight / hh).toInt()
            }
            if (be <= 0) be = 1
            newOpts.inSampleSize = be
            val bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
            return compressImageToBase64(bitmap)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return null
    }

    fun compressImageToBase64(bitmap: Bitmap?): String? {
        if (null == bitmap) {
            return null
        }
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            var options = 100
            while (baos.toByteArray().size / (1024 * 3) > 100) {
                baos.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
                options -= 10
            }
            val bitmapBytes = baos.toByteArray()
            return Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}