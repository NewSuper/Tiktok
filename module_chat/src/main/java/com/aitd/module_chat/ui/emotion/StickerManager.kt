package com.aitd.module_chat.ui.emotion

import android.content.Context
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXContext
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.utils.ToastUtil
import java.util.*

/**
 * 表情管理
 */
class StickerManager private constructor() {

    private val TAG = "StickerManager"

    // 系统表情
    private lateinit var categoryDefault: StickerCategory
    var updateSticker: IUpdateSticker? = null

    // 用户自定义表情
    private lateinit var categoryUser: StickerCategory

    //    private var isSync = false
    private var isUserSync = false


    fun initSticker() {
        categoryDefault = StickerCategory(STICKER_DEFAULT, true, 0)
        categoryUser = StickerCategory(STICKER_DEFAULT_USER_FAV, false, 0)
        addFavSticker(STICKER_DEFAULT_USER_FAV_ADD, "", "")
    }

    private fun addFavSticker(name: String?, localPath: String, originUrl: String) {
        val stickerItem =
            StickerItem(STICKER_DEFAULT_USER_FAV, name, localPath, originUrl, 0, 0, -1)
        categoryUser.stickers.add(0, stickerItem)
    }

    fun addFavStickerByManager(name: String?, localPath: String, originUrl: String) {
        val stickerItem =
            StickerItem(STICKER_DEFAULT_USER_FAV, name, localPath, originUrl, 0, 0, -1)
        categoryUser.stickers.add(1, stickerItem)
        updateSticker?.addSticker(stickerItem)
    }

    fun addFavSticker(
        context: Context,
        localPath: String,
        originUrl: String,
        width: Int,
        height: Int
    ) {
        if (!checkIsExit(originUrl)) {
            stickIndex++
            val stickerItem = StickerItem(
                STICKER_DEFAULT_USER_FAV,
                "",
                localPath,
                originUrl,
                width,
                height,
                stickIndex
            )
            QXContext.getInstance().conversationEmotionClickListener.addSticker(
                context,
                stickerItem,
                object : QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback {
                    override fun onSuccess() {
                        categoryUser.stickers.add(1, stickerItem)
                        updateSticker?.addSticker(stickerItem)
                    }

                    override fun onFail() {

                    }

                })
        } else {
            ToastUtil.toast(
                QXContext.getInstance().context,
                QXContext.getString(R.string.qx_is_exist)
            )
        }

    }

    private fun checkIsExit(originUrl: String): Boolean {
        return QXContext.getInstance().emotionStickerProvider.isHasSticker(originUrl)
    }

    fun addAllDefaultSticker() {
        var provider: QXIMKit.QXStickerProvider? = QXContext.getInstance().emotionStickerProvider
            ?: return
        provider?.getAllSticker(QXIMKit.getInstance().curUserId, STICKER_DEFAULT) { data ->
            data?.let {
                categoryDefault.stickers.clear()
                categoryDefault.stickers.addAll(it)
            }
        }
    }

    fun addAllUserSticker() {
        var provider: QXIMKit.QXStickerProvider? = QXContext.getInstance().emotionStickerProvider
            ?: return
        provider?.getAllSticker(QXIMKit.getInstance().curUserId, STICKER_DEFAULT_USER_FAV) { data ->
            data?.let {
                if (it.size > 0) {
                    stickIndex = it[0].index!!
                }
                categoryUser.stickers.addAll(it)
                isUserSync = true
            }
        }
    }

    /**
     * 删除
     */
    fun delFavSticker(context: Context, stickerItem: StickerItem) {
        QXContext.getInstance().conversationEmotionClickListener.delSticker(
            context,
            stickerItem,
            object : QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback {
                override fun onSuccess() {
                    categoryUser.stickers.remove(stickerItem)
                    updateSticker?.removeSticker(stickerItem)
                }

                override fun onFail() {
                }

            })
    }

    fun onLongClickSticker(context: Context, stickerItem: StickerItem) {
        QXContext.getInstance().conversationEmotionClickListener.onLongClickSticker(
            context,
            stickerItem,
            object : QXIMKit.ConversationStickerClickListener.QXStickerOperationCallback {
                override fun onSuccess() {
                    categoryUser.stickers.remove(stickerItem)
                    updateSticker?.removeSticker(stickerItem)
                }

                override fun onFail() {
                }

            })
    }

    /**
     * 批量删除 服务器地址
     */
    fun delFavSticker(list: List<String>) {
        val delList = mutableListOf<StickerItem>()
        for (delPath in list) {
            for (stickItem in categoryUser.stickers) {
                if (stickItem.originUrl == delPath) {
                    delList.add(stickItem)
                }
            }
        }
        categoryUser.stickers.removeAll(delList)
        updateSticker?.removeSticker(delList)
    }

    /**
     * 位置变换
     */
    fun moveStickerPosition(from: Int, to: Int) {
        Collections.swap(categoryUser.stickers, from, to)
        updateSticker?.notifyStickerPosition(from, to)
    }

    fun getDefaultStickerList(): List<StickerItem> {

        addAllDefaultSticker()
        return categoryDefault.stickers
    }

    fun getUserStickerList(): List<StickerItem> {
        if (isUserSync)
            return categoryUser.stickers
        addAllUserSticker()
        return categoryUser.stickers
    }

    fun asyncUserStickerList(callback: (List<StickerItem>) -> Unit) {
        if (isUserSync) {
            callback!!.invoke(categoryUser.stickers)
            return
        }

        var provider: QXIMKit.QXStickerProvider? = QXContext.getInstance().emotionStickerProvider
            ?: return
        provider?.getAllSticker(QXIMKit.getInstance().curUserId, STICKER_DEFAULT_USER_FAV) { data ->
            data?.let {
                if (it.size > 0) {
                    stickIndex = it[0].index!!
                }
                categoryUser.stickers.addAll(it)
                isUserSync = true
                callback!!.invoke(categoryUser.stickers)
            }
        }
    }

    fun asyncDefaultStickerList(callback: (List<StickerItem>) -> Unit) {
        var provider: QXIMKit.QXStickerProvider? = QXContext.getInstance().emotionStickerProvider
            ?: return
        provider?.getAllSticker(QXIMKit.getInstance().curUserId, STICKER_DEFAULT) { data ->
            data?.let {
                categoryDefault.stickers.clear()
                categoryDefault.stickers.addAll(it)
                callback!!.invoke(categoryDefault.stickers)
            }
        }
    }

    companion object {
        const val STICKER_DEFAULT = "system"
        const val STICKER_DEFAULT_USER_FAV = "user"
        const val STICKER_DEFAULT_USER_FAV_ADD = "user_fav_add"
        var stickIndex = 0

        // 默认图
        @JvmStatic
        var placeHolderId: Int = 0

        @JvmStatic
        val instance: StickerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy StickerManager()
        }
    }
}