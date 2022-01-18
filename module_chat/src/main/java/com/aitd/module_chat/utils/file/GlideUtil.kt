package com.aitd.module_chat.utils.file

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.aitd.library_common.imageload.GlideRoundTransform
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMKit
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.disklrucache.DiskLruCache
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.SafeKeyGenerator
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.EmptySignature
import java.io.File
import java.io.IOException

object GlideUtil {

    fun loadAvatar(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).apply(
            RequestOptions.bitmapTransform(
                CircleCrop()
            ).placeholder(R.mipmap.chat_avatar_default)
        ).into(imageView)

    }

    fun loadAvatar(context: Context, url: String, imageView: ImageView, transform: CornerTransform) {

        Glide.with(context).load(url).apply(
            RequestOptions.bitmapTransform(
                transform
            ).placeholder(R.mipmap.chat_avatar_default)
        ).into(imageView)
    }

    fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.chat_avatar_default)
            .fitCenter()
        ).load(QXIMKit.getInstance().getRealUrl(url)).into(imageView)
    }

    /**
     * 加载头像标签
     */
    fun loadAvatarFlag(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }


    fun loadBackground(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
            .centerCrop()
            .fitCenter()
        ).load(QXIMKit.getInstance().getRealUrl(url)).into(imageView)
    }

    fun loadAvatar(context: Context, resId: Int, imageView: ImageView) {
        Glide.with(context).load(resId).apply(
            RequestOptions.bitmapTransform(
                CircleCrop()
            ).placeholder(R.mipmap.chat_avatar_default)
        ).into(imageView)
    }

    fun getCacheFile(context: Context, url: String?): File? {
        if (url.isNullOrEmpty())
            return null
        val dataCacheKey = DataCacheKey(GlideUrl(url), EmptySignature.obtain())
        val safeKeyGenerator = SafeKeyGenerator()
        val safeKey = safeKeyGenerator.getSafeKey(dataCacheKey)
        try {
            val cacheSize = 500 * 1000 * 1000
            val diskLruCache = DiskLruCache.open(File(context.cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, cacheSize.toLong())
            val value = diskLruCache[safeKey]
            if (value != null) {
                return value.getFile(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    fun showAvatar(context: Context?, imageView: ImageView?, url: Uri?) {
        Glide.with(context!!).load(url).apply(
            RequestOptions.bitmapTransform(
                CircleCrop()
            ).placeholder(R.mipmap.ic_avatar_default)
        ).into(imageView!!)
    }

    fun showAvatar2(context: Context?, imageView: ImageView?, url: Uri?) {
        Glide.with(context!!).load(url).placeholder(R.mipmap.ic_avatar_default).into(imageView!!)
    }

    fun showBlurTransformation(context: Context?, imageView: ImageView?, url: Uri?) {
        if (url != null) {
            try {
                Glide.with(context!!).load(url)
                    .apply(RequestOptions.bitmapTransform(GaussTransformation(context, 5, 15)))
                    .into(
                        imageView!!
                    )
            } catch (exception: Exception) {
                exception.printStackTrace()
            } catch (noSuchMethodError: NoSuchMethodError) {
                noSuchMethodError.printStackTrace()
            }
        }
    }

    fun showRemotePortrait(context: Context?, imageView: ImageView?, url: Uri?) {
        val requestOptions = RequestOptions()
        requestOptions.transform(GlideRoundTransform())
        requestOptions.priority(Priority.HIGH)
        requestOptions.placeholder(R.mipmap.ic_avatar_default)
        if (url == null) {
            Glide.with(context!!).load(R.mipmap.ic_avatar_default).apply(requestOptions)
                .into(imageView!!)
        } else {
            Glide.with(context!!).load(url).apply(requestOptions).into(imageView!!)
        }
    }

}