package com.aitd.library_common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    // 获取当前时间
    public static String nowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String rString = simpleDateFormat.format(cal.getTime());
        return rString;
    }

}
