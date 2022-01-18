package com.aitd.module_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.utils.ThreadPoolUtils
import com.aitd.module_chat.ImageMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.QXError
import com.aitd.module_chat.R
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.lib.QXIMKit
import com.aitd.module_chat.pojo.MessageType
import com.aitd.module_chat.ui.image.decoder.ImageSource
import com.aitd.module_chat.ui.image.decoder.SubsamplingScaleImageView
import com.aitd.module_chat.utils.file.FileUtil
import com.aitd.module_chat.utils.file.GlideUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.aitd.module_chat.view.BottomPop
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lxj.xpopup.XPopup
import java.io.File

class ImagePageActivity : BaseActivity(), View.OnLongClickListener {

    // 从消息中查询当前会话所有聊天图片信息
    // 判断该消息是否被撤回
    companion object {
        val TAG: String = "ImagePageActivity"

        fun startActivity(context: Context, message: Message) {
            val intent = Intent(context, ImagePageActivity::class.java)
            intent.putExtra("message", message)
            context.startActivity(intent)
        }
    }

    private var message: Message? = null
    private lateinit var mViewPager: ViewPager

    private lateinit var mImagePagerAdapter: ImageAdapter
    private var curIndex = 0

    override fun getLayoutId(): Int = R.layout.imui_activity_image_page

    override fun onCreate(savedInstanceState: Bundle?) {
        //全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        message = intent.getParcelableExtra("message")
        mViewPager = findViewById(R.id.imui_image_browse_vp)
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                curIndex = position
            }
        })
        mImagePagerAdapter = ImageAdapter()


        message?.let {
            getConversationImage(it.conversationId)
        }
    }

    override fun init(saveInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    private fun getConversationImage(conversationId: String) {
        val messageTypes = mutableListOf<String>()
        messageTypes.add(MessageType.TYPE_IMAGE)
        QXIMClient.instance.getMessagesByType(conversationId, messageTypes, 0, 10, true, false,
            object : QXIMClient.ResultCallback<List<Message>>() {
                override fun onSuccess(data: List<Message>) {
                    val list = mutableListOf<ImageInfo>()
                    for (index in data.indices) {
                        val msg = data[index]
                        val imageMessage = msg.messageContent as ImageMessage
                        if (imageMessage.localPath.endsWith(".gif", true)
                            || imageMessage.originUrl.endsWith(".gif", true)) {
                            continue
                        }

                        val imageInfo = ImageInfo(msg, Uri.parse(imageMessage.localPath ?: ""),
                            Uri.parse(imageMessage.originUrl ?: ""))
                        list.add(imageInfo)
                    }

                    if (list.size > 0) {
                        for (index in list.indices) {
                            val imgInfo = list[index]
                            if (imgInfo.message.messageId == message!!.messageId) {
                                curIndex = index
                            }
                        }
                        mImagePagerAdapter.addData(list)
                        mImagePagerAdapter.notifyDataSetChanged()
                        mViewPager.adapter = mImagePagerAdapter
                        mViewPager.currentItem = curIndex
                    }
                }


                override fun onFailed(error: QXError) {
                    QLog.e(TAG, "onFailed:${error.code},${error.msg}")
                }

            })
    }

    fun onPictureLongClick(v: View, thumbUri: Uri?, largeImageUri: Uri?): Boolean {
        val pop = BottomPop(v.context,thumbUri.toString(),"image")
        XPopup.Builder(v.context)
            .asCustom(pop)
            .show()
        return true
    }

    override fun onLongClick(v: View): Boolean {
        val imageInfo = mImagePagerAdapter.imageList[curIndex]
        if (imageInfo != null) {
            val thumbUri = imageInfo.thumbUri
            if (thumbUri == null || thumbUri.toString().isNullOrEmpty())
                return false
            val largeImageUri = imageInfo.largeImageUri
            if (this.onPictureLongClick(v, thumbUri, largeImageUri)) {
                return true
            }
            if (largeImageUri == null)
                return false
        }
        return false
    }

    protected inner class ImageInfo(var message: Message, var thumbUri: Uri, var largeImageUri: Uri) {

    }

    protected inner class ImageAdapter : PagerAdapter() {

        var imageList = mutableListOf<ImageInfo>()

        fun addData(images: List<ImageInfo>) {
            imageList.addAll(images)
        }

        private fun createView(context: Context, imageInfo: ImageInfo): View {
            val view = LayoutInflater.from(context).inflate(R.layout.imui_item_image_page, null)
            val viewHolder = ViewHolder()
            viewHolder.photoView = view.findViewById(R.id.imui_image_photoView)
            viewHolder.progressBar = view.findViewById(R.id.imui_image_progressBar)
            viewHolder.progressTv = view.findViewById(R.id.imui_image_progressTv)
            viewHolder.photoView?.setOnLongClickListener(this@ImagePageActivity)
            viewHolder.photoView?.setOnClickListener {
                finish()
            }
            view.tag = viewHolder
            return view
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = createView(container.context, imageList[position])
            container.addView(view)
            loadImage(position, view)
            view.id = position
            return view
        }

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return imageList.size
        }

        private fun loadImage(position: Int, view: View) {
            val holder = view.tag as ViewHolder
            val thumbUri = imageList[position].thumbUri
            val largetUri = imageList[position].largeImageUri
            val messageId = imageList[position].message.messageId
            holder.progressBar?.visibility = View.VISIBLE
            holder.progressTv?.visibility = View.VISIBLE
            holder.progressTv?.text = getString(R.string.qx_loading)
            QLog.e(TAG,  "$messageId, loadImage thumbUri:${thumbUri},largetUri:$largetUri")
            if (largetUri != null) {
                ThreadPoolUtils.run {
                    val file = GlideUtil.getCacheFile(this@ImagePageActivity, largetUri.toString())
                    if (file != null) {
                        QLog.e(TAG,  "$messageId, laod largetUri")
                        runOnUiThread {
                            holder.progressBar?.visibility = View.GONE
                            holder.progressTv?.visibility = View.GONE
                        }
                        showImage(view.context, holder.photoView!!, file)
                        if (thumbUri == null || TextUtils.isEmpty(thumbUri.toString())) {
                            val resultUri = Uri.fromFile(file)
                            imageList[position].thumbUri = resultUri
                        }
                    } else {
                        if (!TextUtils.isEmpty(thumbUri.toString())) {
                            QLog.e(TAG, "$messageId,load thumbUri")
                            runOnUiThread {
                                holder.progressBar?.visibility = View.GONE
                                holder.progressTv?.visibility = View.GONE
                            }
                            showImage(view.context, holder.photoView!!, File(thumbUri.toString()))
                        } else {
                            // 加载网络图
                            QLog.e(TAG,  "$messageId,load from net")
                            Glide.with(view.context).downloadOnly().load(QXIMKit.getInstance().getRealUrl(largetUri.toString())).listener(object :
                                RequestListener<File> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                                    QLog.e(TAG,  "$messageId,load from net  onLoadFailed: ${e?.message}")
                                    runOnUiThread {
                                        holder.progressBar?.visibility = View.GONE
                                        holder.progressTv?.visibility = View.VISIBLE
                                        holder.progressTv?.text =  getString(R.string.qx_load_fail)
                                    }
                                    return true
                                }

                                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    QLog.e(TAG,  "$messageId,load from net  onResourceReady ${resource?.absolutePath}")
                                    ThreadPoolUtils.run {
                                        var resultUri: Uri? = null
                                        if (resource != null) {
                                            resultUri = Uri.fromFile(resource)
                                        }
                                        QLog.e(TAG,  "$messageId,load from net  address： $resultUri")
                                        resultUri?.let {
                                            imageList[position].thumbUri = it
                                            runOnUiThread {
                                                holder.progressBar?.visibility = View.GONE
                                                holder.progressTv?.visibility = View.GONE
                                                showImage(view.context,holder.photoView!!,resource)
//                                                holder.photoView!!.setImage(ImageSource.uri(it))
                                            }
                                        }
                                    }
                                    return true
                                }
                            }).preload()
                        }

                    }
                }

            }
        }

        private fun showImage(context: Context, photoView: SubsamplingScaleImageView, file: File?) {
            if (file == null)
                return
            val resultUri = Uri.fromFile(file)
            var path = ""
            if (resultUri.scheme == "file") {
                path = resultUri.toString().substring(5)
            } else if (resultUri.scheme == "content") {
                val cursor: Cursor? = context.applicationContext.contentResolver.query(resultUri, arrayOf("_data"), null as String?, null as Array<String?>?, null as String?)
                cursor?.moveToFirst()
                path = cursor?.getString(0)!!
                cursor?.close()
            } else {
                path = resultUri.toString()
            }
            runOnUiThread {
                photoView.orientation = FileUtil.readPictureDegree(context, path)
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, options)
                photoView.setImage(ImageSource.uri(resultUri))
            }
        }

        inner class ViewHolder(var photoView: SubsamplingScaleImageView? = null,
                               var progressBar: ProgressBar? = null,
                               var progressTv: TextView? = null) {
        }

    }

}