package com.thread.pool.http;


import android.content.Context;
import android.os.Build;

import com.thread.pool.Priority;

import java.io.File;
import java.util.Map;

import androidx.annotation.RequiresApi;
import okhttp3.OkHttpClient;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class HttpManger implements IHttpEngine {
    //示例  调用代码
//    override fun queryMemberByPhone(phoneNum: String, callback: JsonObjectCallback<*>) {
//        val parameters =
//                OpenApiUtils.getInstance().newApiParameters()
//        parameters["name"] = "member.query"
//        val api = TPUtils.getObject(
//                BaseApp.getApplication(), KeyConstrant.KEY_AUTH_REGISTER,
//                RegistrationCode::class.java
//        )
//        val worker = TPUtils.getObject(
//                BaseApp.getApplication(), KeyConstrant.KEY_WORKER,
//                Worker::class.java
//        )
//        val reqData: MutableMap<String, Any> =
//        HashMap()
//        reqData["property"] = "mobile"
//        reqData["keyword"] = phoneNum
//        reqData["storeNo"] = api.storeNo
//        reqData["posNo"] = api.posNo
//        reqData["workerNo"] = worker.no
//        reqData["sourceSign"] = Global.TERMINAL_TYPE
//        parameters["data"] = GsonHelper.toJson(reqData)
//        parameters["sign"] = OpenApiUtils.getInstance().sign(api, parameters)
//        HttpManger.getSingleton().postJsonObject(
//                HttpUrl.BASE_API_URL,
//                parameters,
//                null,
//                callback
//        )
//    }


    private IHttpEngine httpEngine = null;

    private HttpManger() {
        httpEngine = new HttpEngineImpl();
    }

    public static HttpManger getSingleton() {
        return SingleHolder.mInstance;
    }

    private static class SingleHolder {
        static HttpManger mInstance = new HttpManger();
    }

    @Override
    public void init(Context context) {
        httpEngine.init(context);
    }

    @Override
    public void cancelTag(Object tag) {
        httpEngine.cancelTag(tag);
    }

    @Override
    public void cancelAll() {
        httpEngine.cancelAll();
    }

    @Override
    public void getString(String url, StringCallback callBack) {
        httpEngine.getString(url, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack) {
        httpEngine.getString(url, params, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        httpEngine.getString(url, params, tag, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        httpEngine.getString(url, params, callBack, priority);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        httpEngine.getString(url, params, tag, callBack, priority);
    }


    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack) {
        httpEngine.postString(url, params, callBack);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        httpEngine.postString(url, params, tag, callBack);
    }

    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        httpEngine.postString(url, params, callBack, priority);
    }

    @Override
    public <T> void postJsonObject(String url, Map<String, Object> params, String tag, BaseCallback<T> callback) {
        httpEngine.postJsonObject(url, params, tag, callback);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        httpEngine.postString(url, params, tag, callBack, priority);
    }


    @Override
    public void postJsonString(String url, Map<String, Object> params, StringCallback callBack) {
        httpEngine.postJsonString(url, params, callBack);
    }

    @Override
    public String postString(String url, Map<String, Object> params) {
        return httpEngine.postString(url, params);
    }

    @Override
    public void postFromString(String url, Map<String, String> params, StringCallback callBack) {
        httpEngine.postFromString(url, params, callBack);
    }

    @Override
    public void getFile(String url, FileCallback callBack) {
        httpEngine.getFile(url, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack) {
        httpEngine.getFile(url, params, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack) {
        httpEngine.getFile(url, params, tag, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack, Priority priority) {
        httpEngine.getFile(url, params, callBack, priority);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack, Priority priority) {
        httpEngine.getFile(url, params, tag, callBack, priority);
    }


    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack) {
        httpEngine.upFile(url, params, file, callBack);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack) {
        httpEngine.upFile(url, params, file, tag, callBack);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack, Priority priority) {
        httpEngine.upFile(url, params, file, callBack, priority);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack, Priority priority) {
        httpEngine.upFile(url, params, file, tag, callBack, priority);
    }


    @Override
    public void addCommonParams(Map<String, String> params) {
        httpEngine.addCommonParams(params);
    }

    @Override
    public void addCommonParams(String key, String value) {
        httpEngine.addCommonParams(key, value);
    }

    @Override
    public void clearCommonParams() {
        httpEngine.clearCommonParams();
    }

    @Override
    public void removeCommonParams(String key) {
        httpEngine.removeCommonParams(key);
    }

    @Override
    public void addCommonHeader(String name, String value) {
        httpEngine.addCommonHeader(name, value);
    }

    @Override
    public void addCommonHeaders(Map<String, String> headers) {
        httpEngine.addCommonHeaders(headers);
    }

    @Override
    public void removeCommonHeader(String name) {
        httpEngine.removeCommonHeader(name);
    }

    @Override
    public void clearCommonHeaders() {
        httpEngine.clearCommonHeaders();
    }

    @Override
    public OkHttpClient getHttpClient() {
        return httpEngine.getHttpClient();
    }

    @Override
    public OkHttpClient getDownloadClient() {
        return httpEngine.getHttpClient();
    }
}
