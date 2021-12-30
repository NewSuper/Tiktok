package com.huke.library_pdf

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.router.ARouterUrl
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.huke.library_pdf.databinding.ActivityPdfViewerBinding

@Route(path = ARouterUrl.PDFViewer.ROUTE_PDF_ACTIVITY)
class PDFViewerActivity : BaseActivity() {
    @Autowired(name = "title")
    @JvmField
    var titleString = ""

    @Autowired(name = "file")
    @JvmField
    var file = ""
    lateinit var mBind: ActivityPdfViewerBinding
    override fun init(saveInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        mBind.tlTitle.run {
            setTitleText(titleString)
            setBackOnClickListener { finish() }
        }
        mBind.webPdf.settings.apply {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }
        mBind.webPdf.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoadingDialog()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoadingDialog()
            }
        }
        preView("file://android_asset/$file")
    }

    override fun getRealPageView(inflater: LayoutInflater): View {
        val realPageView = super.getRealPageView(inflater)
        mBind = DataBindingUtil.bind(realPageView)!!
        return realPageView
    }

    private fun preView(preUrl: String) {
        mBind.webPdf.loadUrl("file://android_asset/index.html?$preUrl")
    }

    override fun onDestroy() {
        super.onDestroy()
        mBind.webPdf.destroy()
    }

    override fun getLayoutId(): Int = R.layout.activity_pdf_viewer
}