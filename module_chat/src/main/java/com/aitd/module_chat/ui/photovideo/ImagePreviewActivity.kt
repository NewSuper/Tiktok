package com.aitd.module_chat.ui.photovideo

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_preview_image.*


/**
 * 本地图片预览
 */
class ImagePreviewActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_preview_image

    override fun init(saveInstanceState: Bundle?) {
        //全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //透明导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)


        iv_image.enable()
        var url = intent.getStringExtra(Camera2Config.INTENT_PLUGIN_PATH_KEY)
        Glide.with(this).load(url).apply(RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher)).into(iv_image)

        iv_close.setOnClickListener {
            onBackPressed()
        }

        tv_save.setOnClickListener {
            var intent = Intent()
            intent.putExtra(Camera2Config.INTENT_PLUGIN_TYPE_KEY, Camera2Config.INTENT_PATH_SAVE_PIC);
            intent.putExtra(Camera2Config.INTENT_PLUGIN_PATH_KEY, url);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}