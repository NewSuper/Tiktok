package com.aitd.module_chat.utils

import android.content.Context
import com.aitd.module_chat.R
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

    @JvmStatic
    fun getTime(time: Long): String {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return format.format(Date(time))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getDuration(duration: Int): String {
        var hour = duration / 3600
        var min = duration % 3600 / 60 //取3600的余数，再除60就是分钟数
        var sec = duration % 3600 % 60 //取3600的余数，再取60的余数，就是秒数
        return if (hour == 0) {
            if (min < 10) {
                if (sec < 10) {
                    "0$min:0$sec"
                } else {
                    "0$min:$sec"
                }
            } else {
                if (sec < 10) {
                    "$min:0$sec"
                } else {
                    "$min:$sec"
                }
            }
        } else {
            if (min < 10) {
                if (sec < 10) {
                    "$hour:0$min:0$sec"
                } else {
                    "$hour:0$min:$sec"
                }
            } else {
                if (sec < 10) {
                    "$hour:$min:0$sec"
                } else {
                    "$hour:$min:$sec"
                }
            }
        }
    }
    fun getTimeString(resources: Context, timesamp: Long): String? {
        var result: String? = ""
        val todayCalendar = Calendar.getInstance()
        val otherCalendar = Calendar.getInstance()
        otherCalendar.timeInMillis = timesamp

//        String timeFormat = resources.getString(R.string.qx_time_format);
//        String yearTimeFormat =  resources.getString(R.string.qx_time_format_1);
        var am_pm = ""
        val hour = otherCalendar[Calendar.HOUR_OF_DAY]
        if (hour >= 0 && hour < 6) {
            am_pm = resources.getString(R.string.qx_time_early_moning)
        } else if (hour >= 6 && hour < 12) {
            am_pm = resources.getString(R.string.qx_time_moning)
        } else if (hour == 12) {
            am_pm = resources.getString(R.string.qx_time_noon)
        } else if (hour > 12 && hour < 18) {
            am_pm = resources.getString(R.string.qx_time_afternoon)
        } else if (hour >= 18) {
            am_pm = resources.getString(R.string.qx_time_night)
        }
        val yearTemp = todayCalendar[Calendar.YEAR] == otherCalendar[Calendar.YEAR]
        result = if (yearTemp) {
            val todayMonth = todayCalendar[Calendar.MONTH]
            val otherMonth = otherCalendar[Calendar.MONTH]
            if (todayMonth == otherMonth) { //表示是同一个月
                val temp = todayCalendar[Calendar.DATE] - otherCalendar[Calendar.DATE]
                when (temp) {
                    0 -> getHourAndMin(timesamp)
                    1 -> resources.getString(R.string.qx_time_yesterday, getHourAndMin(timesamp))
                    2, 3, 4, 5, 6 -> {
                        val dayOfMonth = otherCalendar[Calendar.WEEK_OF_MONTH]
                        val todayOfMonth = todayCalendar[Calendar.WEEK_OF_MONTH]
                        if (dayOfMonth == todayOfMonth) { //表示是同一周
                            val dayOfWeek = otherCalendar[Calendar.DAY_OF_WEEK]
                            if (dayOfWeek != 1) { //判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                val dayNames = resources.resources.getStringArray(R.array.qx_day)
                                dayNames[otherCalendar[Calendar.DAY_OF_WEEK] - 1].toString() + getHourAndMin(
                                    timesamp
                                )
                            } else {
                                getTime(resources, am_pm, timesamp)
                            }
                        } else {
                            getTime(resources, am_pm, timesamp)
                        }
                    }
                    else -> getTime(resources, am_pm, timesamp)
                }
            } else {
                getTime(resources, am_pm, timesamp)
            }
        } else {
            getYearHourAndDay(resources, am_pm, timesamp)
        }
        return result
    }

    fun getDate(time: Long): String? {
        try {
            val format = SimpleDateFormat("MM-dd")
            return format.format(Date(time))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
           // QLog.e(TimeUtil.TAG, "时间格式化错误:getDate time:$time")
        }
        return ""
    }


    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    fun getYearHourAndDay(context: Context, time: Long): String? {
        try {
            val format = SimpleDateFormat(context.getString(R.string.qx_time_format_3))
            return format.format(Date(time))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
           // QLog.e(TimeUtil.TAG, "时间格式化错误: getHourAndMin time:$time")
        }
        return ""
    }

    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    fun getHourAndMin(time: Long): String {
        try {
            val format = SimpleDateFormat("HH:mm")
            return format.format(Date(time))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //QLog.e(TimeUtil.TAG, "时间格式化错误: getHourAndMin time:$time")
        }
        return ""
    }


    /**
     * 获取完整格式例如：M月d日 晚上 15:30
     *
     * @param activity
     * @param am_pm
     * @param timesamp
     * @return
     */
    fun getTime(activity: Context, am_pm: String, timesamp: Long): String? {
        var hourDayStr = ""
        try {
            val format = SimpleDateFormat(activity.getString(R.string.qx_time_format_2))
            hourDayStr = format.format(Date(timesamp))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
           // QLog.e(TimeUtil.TAG, "时间格式化错误: getTime time:$am_pm")
        }
        return hourDayStr + " " + am_pm + " " + getHourAndMin(timesamp)
    }

    /**
     * 获取完整格式例如：yyyy年M月d日 晚上 15:30
     *
     * @param activity
     * @param am_pm
     * @param timesamp
     * @return
     */
    fun getYearHourAndDay(activity: Context, am_pm: String, timesamp: Long): String? {
        var yearHourDayStr = ""
        try {
            val format = SimpleDateFormat(activity.getString(R.string.qx_time_format_3))
            yearHourDayStr = format.format(Date(timesamp))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
           // QLog.e(TimeUtil.TAG, "时间格式化错误: getYearHourAndDay time:$timesamp")
        }
        return yearHourDayStr + " " + am_pm + " " + getHourAndMin(timesamp)
    }


    fun isSameday(curr: Long, last: Long): Boolean {
        val date1 = Date(curr)
        val calendar = Calendar.getInstance()
        calendar.time = date1
        val Year1 = calendar[Calendar.YEAR]
        val Month1 = calendar[Calendar.MONTH]
        val Day1 = calendar[Calendar.DAY_OF_MONTH]
        val date2 = Date(last)
        calendar.time = date2
        val Year2 = calendar[Calendar.YEAR]
        val Month2 = calendar[Calendar.MONTH]
        val Day2 = calendar[Calendar.DAY_OF_MONTH]
        return (Year1 == Year2
                && Month1 == Month2
                && Day1 == Day2)
    }
}