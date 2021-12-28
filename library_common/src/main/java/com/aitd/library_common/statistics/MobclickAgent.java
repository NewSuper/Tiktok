package com.aitd.library_common.statistics;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.aitd.library_common.app.BaseApplication;
import com.aitd.library_common.base.Constans;
import com.aitd.library_common.encrypt.AppCommonEncryptionUtils;
import com.aitd.library_common.encrypt.MD5Util;
import com.aitd.library_common.utils.DateUtils;
import com.aitd.library_common.utils.SystemUtil;
import com.blankj.utilcode.util.DeviceUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 事件上报传
 *
 * @author Jack
 */

public class MobclickAgent {

    private MobclickAgent() {
    }

    public static MobclickAgent getInstance() {
        return SingleHoler.instance;
    }

    private static class SingleHoler {
        private static MobclickAgent instance = new MobclickAgent();
    }

    private HashMap<String, String> mMap = new HashMap<>();
    private static final String TAG = "upload";

    /**
     * 安装事件
     *
     * @param activity
     */
    public void statInstall(Activity activity) {
        try {
            mMap.put("pageCode", EventConstant.APP_Global);
            setJsonObject(EventConstant.INSTALL, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        upload(activity);
    }

    /**
     * eventType	1	冷启动事件
     *
     * @param activity
     */
    public void startColdApp(Activity activity, String startElapsedTime) {
        try {
            mMap.put("pageCode", EventConstant.APP_Global);
            mMap.put("startType", "1");
            mMap.put("startElapsedTime", startElapsedTime);
            Log.i(TAG, startElapsedTime + "<=startElapsedTime");
            setJsonObject(EventConstant.COLD_BOOT, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        upload(activity);
    }

    /**
     * eventType	2	热启动事件
     *
     * @param activity
     */
    public void startHotApp(Activity activity) {
        try {
            mMap.put("pageCode", EventConstant.APP_Global);
            mMap.put("startType", "2");
            setJsonObject(EventConstant.HOT_BOOT, 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        upload(activity);
    }

    /**
     * 操作事件
     *
     * @param activity
     * @param event
     */
    public void eventAction(Activity activity, String event, String actionName, String actionDes, String pageCode) {
        try {
            if (!TextUtils.isEmpty(pageCode)) {
                mMap.put("pageCode", pageCode);                                // 页面编码
            }
            setJsonObject(event, 6);
            setAction(actionName, actionDes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        upload(activity);
    }

    /**
     * 上报日志
     */
    private void upload(Activity activity) {
        commonJson(activity);
        long timestamp = AppCommonEncryptionUtils.getTimeMillis();
        StringBuilder secretStr = new StringBuilder();
        Map<String, String> sortParams = new TreeMap<String, String>(mMap);
        for (Map.Entry<String, String> entry : sortParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(value)) {
                secretStr.append(key).append(value);
            }
        }
        String signStr = Constans.APP_ID + secretStr.toString().trim() + Constans.APP_SECRET + timestamp;
        String sign = MD5Util.getMD5(signStr);
        Log.i(TAG, "secretStr:=>" + secretStr);
        Log.i(TAG, "signStr:=>" + signStr);
        Log.i(TAG, "sign:=>" + sign);
        Log.i(TAG, "url=>" + Constans.EVENT_URL + "api/point/v1/report");
//        OkHttpUtils.postString().url(Constans.EVENT_URL+"api/point/v1/report")
//                .addHeader("sign",sign)
//                .addHeader("timestamp",timestamp+"")
//                .content(new Gson().toJson(sortParams))
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.i(TAG,"onError:"+e.toString());
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.i(TAG,"onResponse:"+response);
//                    }
//                });
    }

    /**
     * eventType	1	装机事件
     * eventType	3	异常事件
     * eventType	4	页面加载事件
     * eventType	5	图片视频加载事件
     * eventType	6	操作事件
     *
     * @param eventCode
     * @param eventType
     * @throws JSONException
     */
    private void setJsonObject(String eventCode, int eventType) throws JSONException {
        mMap.put("eventCode", eventCode);                        // 事件编码
        mMap.put("eventType", eventType + "");                     // 事件类型
    }

    /**
     * 描述操作信息
     *
     * @param operatingItems
     * @param eventDescription
     * @throws JSONException
     */
    private void setAction(String operatingItems, String eventDescription) throws JSONException {
        mMap.put("operationItems", operatingItems);            // 操作事项
        mMap.put("eventDescription", eventDescription);        // 事件描述
    }

    /**
     * 公参
     *
     * @param activity
     * @throws JSONException
     */
    private void commonJson(Activity activity) {
        mMap.put("machineId", getUUID());                                    // 机器ID
        mMap.put("appId", Constans.APP_ID);                                  // 应用编码
        if (BaseApplication.getUserBean() != null && !TextUtils.isEmpty(BaseApplication.getUserBean().getUserId())) {
            mMap.put("uid", BaseApplication.getUserBean().getUserId());
        }
        mMap.put("installTime", DateUtils.nowTime());                         // 页面编码
        mMap.put("installIp", getLocalIpAddress(activity));                   // 启动IP
        mMap.put("osType", "1");
        mMap.put("oSVersion", SystemUtil.getSystemVersion());
        mMap.put("cpuVersion", SystemUtil.getSystemCpu());                     // cpu型号
        mMap.put("phoneVersion", SystemUtil.getSystemModel());                 // 事件编码eventType
        mMap.put("phoneBrand", SystemUtil.getDeviceBrand());                  // 版本号
        mMap.put("network", InternetUtil.getNetworkState(activity) + "");         // 网络类型
        mMap.put("operationTime", DateUtils.nowTime());                        // 页面编码
        mMap.put("startFinishiTime", DateUtils.nowTime());                     // 页面编码
        mMap.put("startIp", getLocalIpAddress(activity));                      // 启动IP
        if (Build.VERSION.SDK_INT <= 25) {
            mMap.put("cpuUsage", Math.round(SystemUtil.getTotalCpuRate()) + "");             //  CPU使用率
            mMap.put("appCpuUsage", Math.round(SystemUtil.getCurProcessCpuRate()) + "");     //  APP使用CPU比例
        } else {
            mMap.put("cpuUsage", "0");                                                   //  CPU使用率
            mMap.put("appCpuUsage", "0");                                                //  APP使用CPU比例
        }
        mMap.put("memory", SystemUtil.memory() + "");                                        //   内存
        mMap.put("memoryUsage", SystemUtil.totalMemory() / SystemUtil.freeMemory() + "");     //  内存使用率
        mMap.put("appMemoryUsage", SystemUtil.totalMemory() / SystemUtil.freeMemory() + "");  //  app内存使用率
        mMap.put("appMemorySize", SystemUtil.totalMemory() + "");                             //  APP内存大小
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    private static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    private static String getDeviceID() {
        String deviceID = "";
        try {
            //一共13位  如果位数不够可以继续添加其他信息
            deviceID = "" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                    Build.USER.length() % 10;
        } catch (Exception e) {
            return "";
        }
        return deviceID;
    }

    private String getDeviceUUid() {
        String androidId = DeviceUtils.getAndroidID();
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) androidId.hashCode() << 32));
        return deviceUuid.toString();
    }

    private final String PREF_KEY_UUID = "pref_key_uuid";

    private String getAppUUid() {
        String uuid = SPUtils.getInstance().getString(PREF_KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            SPUtils.getInstance().save(PREF_KEY_UUID, uuid);
        }
        return uuid;
    }

    private String getUUID() {
        String uuid = getDeviceUUid();
        if (TextUtils.isEmpty(uuid)) {
            uuid = getAppUUid();
        }
        return uuid;
    }
}
