package com.aitd.module_chat.ui.photovideo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.R
import kotlinx.android.synthetic.main.activity_video_preview.*


/**
 * 拍摄的本地视频预览
 */
open class VideoPreviewActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_video_preview
    var url: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    override fun init(saveInstanceState: Bundle?) {

    }

    open fun initData() {
        url = intent.getStringExtra(Camera2Config.INTENT_PLUGIN_PATH_KEY)
    }

    open fun initView() {
        val localMediaController = MyMediaController(findViewById(R.id.title_layout), this)
        preview_video_view.setMediaController(localMediaController)
        if (!TextUtils.isEmpty(url)) {
            //加载本地路径
            playVideo(url.toString())
        }

        iv_close.setOnClickListener {
            onBackPressed()
        }

        tv_save.setOnClickListener {
            var intent = Intent()
            intent.putExtra(Camera2Config.INTENT_PLUGIN_TYPE_KEY, Camera2Config.INTENT_PATH_SAVE_VIDEO);
            intent.putExtra(Camera2Config.INTENT_PLUGIN_PATH_KEY, url);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    open fun playVideo(path: String) {
        runOnUiThread {
            pb.visibility = View.GONE
            preview_video_view.setVideoURI(Uri.parse(path))
            preview_video_view.start()
        }
    }

    class MyMediaController(var view : View?, context: Context?) : MediaController(context) {
        override fun show() {
            super.show()
            view?.visibility = View.VISIBLE
        }
        override fun hide() {
            super.hide()
            view?.visibility = View.VISIBLE
        }
    }
}