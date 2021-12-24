package com.aitd.library_common.encrypt.aes;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * base64编码工具类
 *
 * @author wyy
 * @version 2015-11-25
 */
@SuppressWarnings("restriction")
public final class Base64Utils {


    public static String encodeFromString(String srcString) {
        return encodeFromString(srcString, Charsets.UTF8);
    }

    public static String encodeFromString(String srcString, String charsetName) {
        try {
            return encode(srcString.getBytes(charsetName), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String encode(byte[] srcBytes) {
        return encode(srcBytes, Charsets.UTF8);
    }

    public static String encode(byte[] srcBytes, String charsetName) {
        try {
            return new String(Base64.encode(srcBytes, Base64.NO_WRAP), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decodeToString(String base64String) {
        return decodeToString(base64String, Charsets.UTF8);
    }

    public static String decodeToString(String base64String, String charsetName) {
        try {
            return new String(decode(base64String, charsetName), charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] decode(String base64String) {
        return decode(base64String);
    }

    public static byte[] decode(String base64String, String charsetName) {
        try {
            return Base64.decode(base64String.getBytes(charsetName), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String... args) {
        String src = "day day up!";

        String enstr = encodeFromString(src);
        System.out.println(enstr);
        String destr = decodeToString(enstr);
        System.out.println(destr);
    }
}
