package com.huke.library_qrcode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.huke.library_qrcode.databinding.ActivityScanBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception

@Route(path = ARouterUrl.QRCode.ROUTE_SCAN_ACTIVITY)
class ScanActivity : BaseActivity(), QRCodeView.Delegate, CoroutineScope by MainScope() {
    lateinit var mBind: ActivityScanBinding
    private val REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 2

    override fun init(saveInstanceState: Bundle?) {
        mBind.qrcodeTitle.setBackOnClickListener { finish() }
        mBind.zxingview.setDelegate(this)
        mBind.txtOpenPhoto.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).run {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                startActivityForResult(this, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY)
            }
        }
    }

    override fun getRealPageView(inflater: LayoutInflater): View {
        val realPageView = super.getRealPageView(inflater)
        mBind = DataBindingUtil.bind<ActivityScanBinding>(realPageView)!!
        return realPageView
    }

    override fun getLayoutId(): Int = R.layout.activity_scan
    override fun onStart() {
        super.onStart()
        PermissionUtils.permission(android.Manifest.permission.CAMERA)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    mBind.zxingview.startCamera()
                    mBind.zxingview.startSpotAndShowRect()
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>
                ) {

                }

            }).request()
    }

    override fun onStop() {
        mBind.zxingview.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        mBind.zxingview.onDestroy()
        super.onDestroy()
        cancel()
    }

    override fun onScanQRCodeSuccess(result: String?) {
        result?.let {
            Intent().run {
                putExtra("result", result)
                setResult(Activity.RESULT_OK, this)
                finish()
            }
        }
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText: String = mBind.zxingview.scanBoxView.tipText
        val ambientBrightnessTip = getString(R.string.shanguandeng)
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mBind.zxingview.scanBoxView.tipText = tipText + ambientBrightnessTip
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                mBind.zxingview.scanBoxView.tipText = tipText
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.showShort(getString(R.string.toastdakai))
        mBind.zxingview.postDelayed(kotlinx.coroutines.Runnable { finish() }, 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY &&
            resultCode == Activity.RESULT_OK && data != null
        ) {
            val uri = data.data
            val cr = contentResolver
            try {
                if (uri != null) {
                    val mBitmap = MediaStore.Images.Media.getBitmap(cr, uri)
                    mBitmap?.let {
                        launch {
                            flow<String> {
                                emit(QRCodeDecoder.syncDecodeQRCode(mBitmap))
                            }.flowOn(Dispatchers.IO).onStart {
                                showLoadingDialog()
                            }.onCompletion {
                                hideLoadingDialog()
                            }.catch {
                                ToastUtils.showShort(getString(R.string.choice_album_agin_tips))
                            }.collect {
                                Intent().run {
                                    putExtra("result", it)
                                    setResult(Activity.RESULT_OK, this)
                                    finish()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}