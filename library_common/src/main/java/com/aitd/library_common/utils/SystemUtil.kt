package com.aitd.library_common.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.text.TextUtils
import java.io.*
import java.util.*

/**
 * 系统工具类
 */
object SystemUtil {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    val systemLanguage: String
        get() = Locale.getDefault().language

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    val systemLanguageList: Array<Locale>
        get() = Locale.getAvailableLocales()

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    @JvmStatic
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    @JvmStatic
    val systemModel: String
        get() = Build.MODEL

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    @JvmStatic
    val deviceBrand: String
        get() = Build.BRAND

    /**
     * 获取app versionCode
     */
    fun packageCode(context: Context): Int {
        val manager = context.packageManager
        var code = 0
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            code = info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return code
    }

    /**
     * 获取app  versionName
     */
    fun packageName(context: Context): String? {
        val manager = context.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }

    /**
     * 获取手机CPU型号
     *
     * @return 手机CPU型号
     */
    @JvmStatic
    val systemCpu: String
        get() = Build.CPU_ABI

    @JvmStatic
    fun memory(): String {
        //当前分配的总内存
        val totalMemory = (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()
        return Integer.valueOf(totalMemory.toInt()).toString() + ""
    }

    fun maxMemory(): Float {
        return (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024)).toFloat()
    }

    @JvmStatic
    fun totalMemory(): Float {
        return (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()
    }

    @JvmStatic
    fun freeMemory(): Float {
        return (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024)).toFloat()
    }
    /**
     * get CPU rate
     *
     * @return
     */
    //    public static int getProcessCpuRate() {
    //        StringBuilder tv = new StringBuilder();
    //        int rate = 0;
    //        try {
    //            String Result;
    //            Process p;
    //            p = Runtime.getRuntime().exec("top -n 1");
    //            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    //            while ((Result = br.readLine()) != null) {
    //                if (Result.trim().length() < 1) {
    //                    continue;
    //                } else {
    //                    String[] CPUusr = Result.split("%");
    //                    tv.append("USER:" + CPUusr[0] + "\n");
    //                    String[] CPUusage = CPUusr[0].split("User");
    //                    String[] SYSusage = CPUusr[1].split("System");
    //                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
    //                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");
    //                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
    //                    break;
    //                }
    //            }
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        return rate;
    //    }
    /**
     * 获取当前进程的CPU使用率
     *
     * @return CPU的使用率
     */
    @JvmStatic
    val curProcessCpuRate: Float
        get() {
            val totalCpuTime1 = totalCpuTime.toFloat()
            val processCpuTime1 = appCpuTime.toFloat()
            try {
                Thread.sleep(360)
            } catch (e: Exception) {
            }
            val totalCpuTime2 = totalCpuTime.toFloat()
            val processCpuTime2 = appCpuTime.toFloat()
            return (100 * (processCpuTime2 - processCpuTime1)
                    / (totalCpuTime2 - totalCpuTime1))
        }

    /**
     * 获取总的CPU使用率
     *
     * @return CPU使用率
     */
    @JvmStatic
    val totalCpuRate: Float
        get() {
            val totalCpuTime1 = totalCpuTime.toFloat()
            val totalUsedCpuTime1 = totalCpuTime1 - sStatus.idletime
            try {
                Thread.sleep(360)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val totalCpuTime2 = totalCpuTime.toFloat()
            val totalUsedCpuTime2 = totalCpuTime2 - sStatus.idletime
            return (100 * (totalUsedCpuTime2 - totalUsedCpuTime1)
                    / (totalCpuTime2 - totalCpuTime1))
        }

    /**
     * 获取系统总CPU使用时间
     *
     * @return 系统CPU总的使用时间
     */
    val totalCpuTime: Long
        get() {
            var cpuInfos: Array<String>? = null
            try {
                val reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream("/proc/stat")
                    ), 1000
                )
                val load = reader.readLine()
                reader.close()
                cpuInfos = load.split(" ".toRegex()).toTypedArray()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            sStatus.usertime = cpuInfos!![2].toLong()
            sStatus.nicetime = cpuInfos[3].toLong()
            sStatus.systemtime = cpuInfos[4].toLong()
            sStatus.idletime = cpuInfos[5].toLong()
            sStatus.iowaittime = cpuInfos[6].toLong()
            sStatus.irqtime = cpuInfos[7].toLong()
            sStatus.softirqtime = cpuInfos[8].toLong()
            return sStatus.totalTime
        }// 获取应用占用的CPU时间

    /**
     * 获取当前进程的CPU使用时间
     *
     * @return 当前进程的CPU使用时间
     */
    val appCpuTime: Long
        get() {
            // 获取应用占用的CPU时间
            var cpuInfos: Array<String>? = null
            try {
                val pid = Process.myPid()
                val reader = BufferedReader(
                    InputStreamReader(
                        FileInputStream("/proc/$pid/stat")
                    ), 1000
                )
                val load = reader.readLine()
                reader.close()
                cpuInfos = load.split(" ".toRegex()).toTypedArray()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return cpuInfos!![13].toLong() + cpuInfos[14].toLong() + cpuInfos[15]
                .toLong() + cpuInfos[16].toLong()
        }
    var sStatus = Status()
    @JvmStatic
    fun getCurrentProcessName(context: Context?): String? {
        var process = ""
        if (context != null) {
            val pid = Process.myPid()
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (am != null) {
                val infos = am.runningAppProcesses
                if (infos != null) {
                    val iterator: Iterator<*> = infos.iterator()
                    while (iterator.hasNext()) {
                        val info = iterator.next() as ActivityManager.RunningAppProcessInfo
                        if (info.pid == pid) {
                            process = info.processName
                            break
                        }
                    }
                }
            }
        }
        if (TextUtils.isEmpty(process)) {
            try {
                process = readProcessName()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return process
    }

    @Throws(IOException::class)
    private fun readProcessName(): String {
        val cmdlineBuffer = ByteArray(64)
        val stream = FileInputStream("/proc/self/cmdline")
        var success = false
        val result: String
        try {
            val n = stream.read(cmdlineBuffer)
            success = true
            val endIndex = indexOf(cmdlineBuffer, 0, n, 0.toByte())
            result = String(cmdlineBuffer, 0, if (endIndex > 0) endIndex else n)
        } finally {
            close(stream, !success)
        }
        return result
    }

    private fun indexOf(haystack: ByteArray, offset: Int, length: Int, needle: Byte): Int {
        for (i in haystack.indices) {
            if (haystack[i] == needle) {
                return i
            }
        }
        return -1
    }

    @Throws(IOException::class)
    fun close(closeable: Closeable?, hideException: Boolean) {
        if (closeable != null) {
            if (hideException) {
                try {
                    closeable.close()
                } catch (exception: IOException) {
                    exception.printStackTrace()
                }
            } else {
                closeable.close()
            }
        }
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    /*public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }*/
    @JvmStatic
    fun isInBackground(context: Context?): Boolean {
        return if (context == null) {
            true
        } else {
            var isInBackground = true
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses: List<*>?
            if (Build.VERSION.SDK_INT > 20) {
                runningProcesses = am.runningAppProcesses
                if (runningProcesses == null) {
                    return true
                }
                val var4 = runningProcesses.iterator()
                while (true) {
                    var processInfo: ActivityManager.RunningAppProcessInfo
                    do {
                        if (!var4.hasNext()) {
                            return isInBackground
                        }
                        processInfo = var4.next() as ActivityManager.RunningAppProcessInfo
                    } while (processInfo.importance != 100)
                    val var6 = processInfo.pkgList
                    val var7 = var6.size
                    for (var8 in 0 until var7) {
                        val activeProcess = var6[var8]
                        if (activeProcess == context.packageName) {
                            // QLog.d("SystemUtil", "the process is in foreground:" + activeProcess);
                            return false
                        }
                    }
                }
            } else {
                runningProcesses = am.getRunningTasks(1)
                val componentInfo =
                    (runningProcesses[0] as ActivityManager.RunningTaskInfo).topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            isInBackground
        }
    }

    class Status {
        var usertime: Long = 0
        var nicetime: Long = 0
        var systemtime: Long = 0
        var idletime: Long = 0
        var iowaittime: Long = 0
        var irqtime: Long = 0
        var softirqtime: Long = 0
        val totalTime: Long
            get() = (usertime + nicetime + systemtime + idletime + iowaittime
                    + irqtime + softirqtime)
    }
}