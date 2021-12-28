package com.aitd.module_login.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则检查是否合法
 * @author Jack
 */
public class LoginRegexUtils {
    // 包含数字
    private static String CONTAIN_DIGIT_REGEX = ".*[0-9].*";
    // 包含大写字母
    private static String CONTAIN_BIG_REGEX   = ".*[A-Z].*";
    // 包含小写字母
    private static String CONTAIN_SMALL_REGEX = ".*[a-z].*";
    // 包含大写字母，小写字母，数字，且不少于8位-12位
    private static  String CONTAIN_PWD_REGEX="^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])[0-9a-zA-Z@#$%^*]{8,12}$";

    /**
     * 判断字符串是否包含数字
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {
        if (TextUtils.isEmpty(str)){
            return false;
        }
        return str.matches(CONTAIN_DIGIT_REGEX);
    }

    /**
     * 判断字符串是否包含大写字母
     * @param str
     * @return
     */
    public static boolean isDigitBig(String str) {
        if (TextUtils.isEmpty(str)){
            return false;
        }
        return str.matches(CONTAIN_BIG_REGEX);
    }

    /**
     * 判断字符串是否包含小写字母
     * @param str
     * @return
     */
    public static boolean isDigitSmall(String str) {
        if (TextUtils.isEmpty(str)){
            return false;
        }
        return str.matches(CONTAIN_SMALL_REGEX);
    }
    /**
     * 判断字符串是否包含大写字母，小写字母，数字，且不少于8位-12位
     * @param str
     * @return
     */
    public static boolean isCheckPwd(String str) {
        if (TextUtils.isEmpty(str)){
            return false;
        }
        return str.matches(CONTAIN_PWD_REGEX);
    }
    /**
     * 使用正则表达式来判断字符串中是否包含字母
     * @param str 待检验的字符串
     * @return 返回是否包含
     * true: 包含字母 ;false 不包含字母
     */
    public static boolean judgeContainsStr(String str) {
        String regex=".*[a-zA-Z.]+.*";
        Matcher m= Pattern.compile(regex).matcher(str);
        return m.matches();
    }

    //判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
