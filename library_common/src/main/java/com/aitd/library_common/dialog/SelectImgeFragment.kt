package com.aitd.library_common.dialog

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.os.EnvironmentCompat
import androidx.fragment.app.DialogFragment
import com.aitd.library_common.R
import com.chad.library.adapter.base.listener.OnItemClickListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SelectImgeFragment : DialogFragment() {
    private val request_photo = 1
    private val request_from_album = 2
    private val crop_request_code = 3
    private var mCropOutput: Uri? = null
    private var mOnPicterSelectListener: (uri: Uri) -> Unit = {}

    // 是否是Android 10以上手机
    private val isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    //用于保存拍照图片的uri
    private var mCameraUri: Uri? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mCommonBottomDialog = CommonBottomDialog(
            context, mutableListOf(
                getString(R.string.takephoto),
                getString(R.string.seal_select_chat_bg_album)
            )
        )
        mCommonBottomDialog.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    openCamera()
                }
                1 -> {
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).run {
                        startActivityForResult(this, request_from_album)
                    }
                }
            }
        })
        return mCommonBottomDialog
    }

    //打开相机
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).run {
            activity?.let {
                if (resolveActivity(it.packageManager) != null) {
                    var photoFile: File? = null
                    var photoUri: Uri? = null
                    if (isAndroidQ) {
                        // 适配android 10
                        photoUri = createImageUri()
                    } else {
                        photoFile = createImageFile()
                        if (photoFile != null) {
                            photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                                FileProvider.getUriForFile(
                                    it,
                                    "${it.packageName}fileprovider",
                                    photoFile!!
                                )
                            } else {
                                Uri.fromFile(photoFile)
                            }
                        }
                    }
                    mCameraUri = photoUri
                    if (photoUri != null) {
                        putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        startActivityForResult(this, request_photo)
                    }
                }
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        return if (status == Environment.MEDIA_MOUNTED) {
            activity?.contentResolver?.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )
        } else {
            activity?.contentResolver?.insert(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                ContentValues()
            )
        }
    }

    /**
     * 创建保存图片的文件
     */
    private fun createImageFile(): File? {
        val imageName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir!!.exists()) {
            storageDir.mkdir()
        }
        val tempFile = File(storageDir, imageName)
        return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
            null
        } else tempFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                request_from_album -> {
                    cropImage(data.data!!)
                }
                request_photo -> {
                    cropImage(mCameraUri!!)
                }
                crop_request_code -> {
                    data.data?.let {
                        mOnPicterSelectListener.invoke(data.data!!)
                        dismiss()
                    }
                }

            }
        }
    }

    private fun cropImage(uri: Uri) {
        Intent("com.android.camera.action.CROP").apply {
            setDataAndType(uri, "image/*")
            putExtra("crop", true)
            if (Build.MANUFACTURER.contains("HUAWEI")) {
                putExtra("aspectX", 9998)
                putExtra("aspectY", 9999)
            } else {
                putExtra("aspectX", 1)
                putExtra("aspectY", 1)
            }
            // 裁剪宽高
            putExtra("outputX", 180)
            putExtra("outputY", 180)
            putExtra("return-data", false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            //输出到私有目录中
            mCropOutput = Uri.fromFile(getPrivateFile())
            putExtra(MediaStore.EXTRA_OUTPUT, mCropOutput)
            startActivityForResult(this, crop_request_code)
        }
    }

    /*
    * 创建私有目录下的文件
    * */
    private fun getPrivateFile(): File {
        return File(
            context?.getExternalFilesDir("")!!.absolutePath + "/" + SimpleDateFormat("yyyyMMddHHmmss").format(
                System.currentTimeMillis()
            ) + "photoTemp.jpg"
        )
    }

    fun setPicterSelectListener(onPicterSelectListener: (uri: Uri) -> Unit) {
        mOnPicterSelectListener = onPicterSelectListener
    }
}