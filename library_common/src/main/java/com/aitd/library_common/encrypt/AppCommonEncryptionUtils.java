package com.aitd.library_common.encrypt;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.aitd.library_common.base.Constans;
import com.aitd.library_common.data.UserDataBean;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 加密共用参数
 *
 * @author Jack
 */
public class AppCommonEncryptionUtils {
    private static String APP_KEY = "appKey=";
    private static String BUSINESS_KEY = "&business=";   // 业务参
    private static String NONCE_KEY = "&nonce=";      // 随机数
    private static String TIMESTAMP_KEY = "&timestamp=";  //时间
    private static String TOKEN_KEY = "&token=";
    private static String URL_KEY = "&url=";
    public static boolean isShowLog = true;  // 是否打开日志
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6xXyWXD5bHdmNGLmp2T6ZDgDi30tZhLUoIyHbstRCybnmnZ420qcF7hCHHMKKbjvAyYXeAZm95USF6zx0NIB1hOPlUswl0aWH7b23WFTcyY97NsLMIfnjU2SN3i8NPBfQslXT7zsU9f6aY5BIZWNu3IUdYSR8aBkBVjz2VYy29wIDAQAB";

    private static String getParameter(String business, String nonce, long timestamp, String url) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(APP_KEY + Constans.BASE_APP_KEY);
        if (!TextUtils.isEmpty(business)) {
            buffer.append(BUSINESS_KEY + business);
        }
        buffer.append(NONCE_KEY + nonce);
        buffer.append(TIMESTAMP_KEY + timestamp);
        buffer.append(TOKEN_KEY + UserDataBean.Companion.getToken());
        if (!TextUtils.isEmpty(url)) {
            buffer.append(URL_KEY + url);
        }
        return buffer.toString().trim();
    }

    /**
     * MD5加密
     *
     * @param business
     * @param timestamp
     * @param nonce
     * @return
     */
    public static String getCommonSign(String business, String nonce, long timestamp) {
        String parameter = getParameter(business, nonce, timestamp, null);
        String sign = MD5Util.stringMD5(parameter);
        showLog(parameter, sign);
        return sign;
    }

    public static String getCommonSign(String nonce, long timestamp, String url) {
        String parameter = getParameter(null, nonce, timestamp, url);
        String sign = MD5Util.stringMD5(parameter);
        showLog(parameter, sign);
        return sign;
    }

    public static String getCommonSign(String nonce, long timestamp, HashMap<String, String> url) {
        String parameter = getParameter(null, nonce, timestamp, getSortParamsUrls(url));
        String sign = MD5Util.stringMD5(parameter);
        showLog(parameter, sign);
        return sign;
    }

    private static void showLog(String parameter, String sign) {
        if (isShowLog) {
            Log.i("tag", "sing=加密之前=>" + parameter);
            Log.i("tag", "sing=加密码之后>" + sign);
        }
    }

    /**
     * 排序拼接公参
     *
     * @param params
     * @return
     */
    private static String getSortParamsUrls(HashMap<String, String> params) {
        StringBuilder urls = new StringBuilder();
        Map<String, String> sortParams = new TreeMap<String, String>(params);
        for (Map.Entry<String, String> entry : sortParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(value)) {
                urls.append(key).append("=").append(value).append("&");
            }
        }
        return urls.toString().trim().substring(0, urls.length() - 1);
    }

    /**
     * 随机数
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 时间
     *
     * @return
     */
    public static long getTimeMillis() {
        return System.currentTimeMillis();
    }


    public static String getRsaSign(String json) {
        String rsaSign = "";
        JSONObject object = new JSONObject();
        try {
            byte[] encryptedBytes = RSAUtil.encryptByPublicKey(json.getBytes(), PUBLIC_KEY);
            object.put("data", Base64.encodeToString(encryptedBytes, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        rsaSign = object.toString();
        Log.e("tag", "rsaSign=" + rsaSign);
        return rsaSign;
    }
}
