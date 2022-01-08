package com.aitd.module_chat.viewholder.record

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.aitd.module_chat.ImageMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.utils.file.GlideUtil
import com.aitd.module_chat.utils.file.SizeUtil
import kotlinx.android.synthetic.main.imui_layout_record_content_image.view.*

class RecordImageMessageHandler : RecordBaseMessageHandler() {
    override fun setContentView(itemView: View, contentLayout: ViewGroup, message: Message) {
        var contentView = createContentView(itemView, contentLayout, R.layout.imui_layout_record_content_image) as ViewGroup
        super.setContentView(itemView, contentLayout, message)
        var imageMessage = message.messageContent as ImageMessage
        var imageUrl = if (imageMessage.localPath.isNotEmpty()) {
            imageMessage.localPath
        } else {
            imageMessage.originUrl
        }

        //按图片宽高比自适应控件宽高
        var lp: ConstraintLayout.LayoutParams = contentView.iv_image.layoutParams as ConstraintLayout.LayoutParams
        var width = imageMessage.width.toFloat()
        var height = imageMessage.height.toFloat()

        var maxSize = 0
        var observer = contentLayout.viewTreeObserver
        var isDraw = false
        observer.addOnPreDrawListener {
            if (!isDraw) {
                maxSize = contentLayout.width
                contentView.iv_image.layoutParams = SizeUtil.calcHeight(width, height, maxSize, lp)
                GlideUtil.loadImage(itemView.context, imageUrl, contentView.iv_image)
                isDraw = true
            }
            true
        }

    }
}