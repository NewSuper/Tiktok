package com.aitd.module_chat.utils

import com.aitd.library_common.app.BaseApplication
import com.aitd.library_common.utils.GlobalContextManager
import com.aitd.module_chat.http.BaseResponre
import com.aitd.module_chat.http.HttpCallback
import com.aitd.module_chat.http.HttpLogger
import com.aitd.module_chat.listener.DownloadCallback
import com.aitd.module_chat.listener.UploadCallback
import com.aitd.module_chat.pojo.AccountConfig
import com.aitd.module_chat.pojo.GroupInfo
import com.aitd.module_chat.pojo.ReqCreateGroup
import com.aitd.module_chat.pojo.UploadBean
import com.aitd.module_chat.utils.file.FileUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.ejlchina.okhttps.GsonMsgConvertor
import com.ejlchina.okhttps.HTTP
import com.ejlchina.okhttps.OkHttps
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.logging.HttpLoggingInterceptor

class OkhttpUtils {

    companion object {
        private const val TAG = "OkhttpUtils"
        private const val BASE_URL = "https://qx-thirdpart-beta.aitdcoin.com/qx-api/demo-service"
        // 生产环境
//        private const val BASE_URL = "http://qx-thirdpart.aitdcoin.com:9900/qx-api/demo-service"

        fun getUsers(callback: HttpCallback<String>) {
            val http: HTTP = HTTP.builder()
                .config { builder ->
                    val logInterceptor = HttpLoggingInterceptor(HttpLogger())
                    logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    builder.addInterceptor(logInterceptor)
                }
                .baseUrl(BASE_URL)
                .addMsgConvertor(GsonMsgConvertor())
                .build()
            http.async("/im/user/getUsers")
                .setOnResponse {
                    val type = object : TypeToken<BaseResponre<AccountConfig.Data>>() {}.type
                    val responre =
                        Gson().fromJson<BaseResponre<AccountConfig.Data>>(it.body.toString(), type)
                    QLog.d(TAG, "${responre.data}")
                    if (responre.code == 1000) {
                        AccountConfig.accList.clear()
                        AccountConfig.accList.addAll(responre.data!!.users)
                        QLog.d(TAG, "account user:${responre.data!!.users}")
                        callback.onSuccess("")
                    } else {

                    }
                }
                .get()
        }

        fun createGroup(reqCreateGroup: ReqCreateGroup, callback: HttpCallback<String>) {
            val http: HTTP = HTTP.builder()
                .config { builder ->
                    val logInterceptor = HttpLoggingInterceptor(HttpLogger())
                    logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    builder.addInterceptor(logInterceptor)
                }
                .baseUrl(BASE_URL)
                .addMsgConvertor(GsonMsgConvertor())
                .build()
            http.async("/im/group/createDemo")
                .bodyType(OkHttps.JSON)
                .setBodyPara(reqCreateGroup)
                .setOnException { callback.onError(0, "ioe") }
                .setOnResponse {
                    val responre = it.body.toBean(BaseResponre::class.java)
                    if (responre.code == 1000) {
                        QLog.d(TAG, "$responre")
                        callback.onSuccess("")
                    } else {
                        callback.onError(responre.code, responre.message)
                    }
                }
                .post()
        }

        fun getGroupInfo(groupId: String, callback: HttpCallback<GroupInfo>) {
            val http: HTTP = HTTP.builder()
                .config { builder ->
                    val logInterceptor = HttpLoggingInterceptor(HttpLogger())
                    logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                    builder.addInterceptor(logInterceptor)
                }
                .baseUrl(BASE_URL)
                .addMsgConvertor(GsonMsgConvertor())
                .build()
            http.async("/im/group/get")
                .addUrlPara("groupId", groupId)
                .setOnException { callback.onError(0, "ioe") }
                .setOnResponse {
                    val type = object : TypeToken<BaseResponre<GroupInfo>>() {}.type
                    val groupInfo =
                        Gson().fromJson<BaseResponre<GroupInfo>>(it.body.toString(), type)
                    if (groupInfo != null) {
                        if (groupInfo.code == 1000) {
                            callback.onSuccess(groupInfo.data!!)
                        } else {
                            callback.onError(groupInfo.code, groupInfo.message)
                        }
                    }
                }
                .get()
        }

        fun download(path: String, callback: DownloadCallback) {
            val http: HTTP = HTTP.builder()
                .addMsgConvertor(GsonMsgConvertor())
                .build()
            http.async(path).setOnResponse {
                it.body.setOnProcess { process ->
                    // 已发送字节数
                    val doneBytes: Long = process.doneBytes
                    // 总共的字节数
                    val totalBytes: Long = process.totalBytes
                    // 已发送的比例
                    val rate: Double = process.rate
                    // 是否发送完成
                    val isDone: Boolean = process.isDone
                    QLog.d(
                        TAG,
                        "upload file data totalBytes:${totalBytes / 1024}KB,doneBytes:${doneBytes / 1024}KB"
                    )
                    callback.onProgress((rate * 100).toInt())
               }.toFolder(FileUtil.getAudioDir(BaseApplication.getAppContext()))
               // }.toFolder(FileUtil.getAudioDir(GlobalContextManager.instance.context!!))
                    .setOnFailure {
                        callback.onFailed(0, "${it.exception.message}")
                    }
                    .setOnSuccess { file ->
                        QLog.d(TAG, "download file path:${file.path}")
                        callback.onCompleted(file.path)
                    }.start()
            }.get()
        }

        fun upload(filePath: String, callback: UploadCallback) {
            val http: HTTP = HTTP.builder()
                .baseUrl(BASE_URL)
                .addMsgConvertor(GsonMsgConvertor())
                .build()
            http.async("/im/file/uploadFile")
                .addFilePara("file", filePath)
                .stepBytes(1024)
                .setOnException {
                    callback.onFailed(0, "${it.message}")
                }
                .setOnResponse {
                    try {
                        val type = object : TypeToken<BaseResponre<UploadBean>>() {}.type
                        val data = it.body.toString()
                        QLog.d(TAG, "upload file data:${data}")
                        val uploadBean = Gson().fromJson<BaseResponre<UploadBean>>(data, type)
                        if (uploadBean.code == 1000) {
                            callback.onCompleted(uploadBean.data?.fileDownloadUri)
                        } else {
                            callback.onFailed(uploadBean.code, uploadBean.message)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setOnProcess { process ->
                    // 已发送字节数
                    val doneBytes: Long = process.doneBytes
                    // 总共的字节数
                    val totalBytes: Long = process.totalBytes
                    // 已发送的比例
                    val rate: Double = process.rate
                    // 是否发送完成
                    val isDone: Boolean = process.isDone
                    QLog.d(TAG, "upload file data totalBytes:${totalBytes / 1024}KB")
                    callback.onProgress((rate * 100).toInt())
                }
                .post()
        }
    }
}