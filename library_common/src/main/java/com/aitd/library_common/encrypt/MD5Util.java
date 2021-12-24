package com.aitd.library_common.encrypt;

import java.security.MessageDigest;

/**
 * Description:MD5加密工具<br/>
 * date: 2018年7月27日 上午11:00:00 <br/>
 *
 * @author zhuMS
 */
public class MD5Util {


    public static String stringMD5(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str).toUpperCase();
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }


    public static String getMD5(byte[] bytes) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str = new char[32];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte[] tmp = md.digest();
            int k = 0;
            for(int i = 0; i < 16; ++i) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }
        return new String(str);
    }
    public static String getMD5(String var0) {
        Object var1 = null;
        if (var0 == null) {
            return null;
        } else {
            try {
                byte[] var2 = var0.getBytes();
                MessageDigest var3 = MessageDigest.getInstance("MD5");
                var3.update(var2);
                return ByteUtils.a(var3.digest());
            } catch (Exception var4) {
                return (String)var1;
            }
        }
    }
}
