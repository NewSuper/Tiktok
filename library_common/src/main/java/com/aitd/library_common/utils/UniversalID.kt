package com.aitd.library_common.utils

import android.content.Context
import android.os.Environment
import android.provider.Settings
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object UniversalID {
    private val filePath = File.separator + "socialContact" + File.separator + "UUID"
    fun getUniversalID(context: Context): String {
        val androidId: String
        val fileRootPath = getPath(context) + filePath
        var uuid = readFile(fileRootPath)
        if (uuid == null || uuid == "") {
            androidId = "" + Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            uuid = try {
                if ("9774d56d682e549c" != androidId) {
                    UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8"))).toString()
                } else {
                    UUID.randomUUID().toString()
                }
            } catch (e: Exception) {
                UUID.randomUUID().toString()
            }
            if (uuid != "") {
                uuid = uuid!!.replace("-".toRegex(), "")
                saveUUID(context, uuid)
            }
        }
        if (null != uuid) {
            uuid = uuid.trim { it <= ' ' }
        }
        return uuid
    }

    private fun saveUUID(context: Context, UUID: String) {
        try {
            val ExternalSdCardPath = externalSdCardPath + filePath
            writeFile(ExternalSdCardPath, UUID)
            val InnerPath = context.filesDir.absolutePath + filePath
            writeFile(InnerPath, UUID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPath(context: Context): String? {
        //首先判断是否有外部存储卡，如没有判断是否有内部存储卡，如没有，继续读取应用程序所在存储
        var phonePicsPath = externalSdCardPath
        if (phonePicsPath == null) {
            phonePicsPath = context.filesDir.absolutePath
        }
        return phonePicsPath
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    private val devMountList: ArrayList<String>
        private get() {
            val toSearch = readFile("/system/etc/vold.fstab")!!
                .split(" ".toRegex()).toTypedArray()
            val out = ArrayList<String>()
            for (i in toSearch.indices) {
                if (toSearch[i].contains("dev_mount")) {
                    if (File(toSearch[i + 2]).exists()) {
                        out.add(toSearch[i + 2])
                    }
                }
            }
            return out
        }

    /**
     * 获取扩展SD卡存储目录
     *
     *
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     *
     * @return
     */
    val externalSdCardPath: String?
        get() {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val sdCardFile = File(Environment.getExternalStorageDirectory().absolutePath)
                return sdCardFile.absolutePath
            }
            var path: String? = null
            var sdCardFile: File? = null
            val devMountList = devMountList
            for (devMount in devMountList) {
                val file = File(devMount)
                if (file.isDirectory && file.canWrite()) {
                    path = file.absolutePath
                    val timeStamp = SimpleDateFormat("ddMMyyyy_HHmmss").format(Date())
                    val testWritable = File(path, "test_$timeStamp")
                    if (testWritable.mkdirs()) {
                        testWritable.delete()
                    } else {
                        path = null
                    }
                }
            }
            if (path != null) {
                sdCardFile = File(path)
                return sdCardFile.absolutePath
            }
            return null
        }

    fun readFile(filePath: String?): String? {
        var fileContent = ""
        val file = File(filePath)
        if (!file.isFile) {
            return null
        }
        var reader: BufferedReader? = null
        try {
            val `is` = InputStreamReader(FileInputStream(file))
            reader = BufferedReader(`is`)
            var line: String
            while (reader.readLine().also { line = it } != null) {
                fileContent += "$line "
            }
            reader.close()
            return fileContent
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return fileContent
    }

    @JvmOverloads
    fun writeFile(filePath: String?, content: String?, append: Boolean = false): Boolean {
        if (filePath == null || filePath.length == 0) {
            return false
        }
        var fileWriter: FileWriter? = null
        return try {
            makeDirs(filePath)
            fileWriter = FileWriter(filePath, append)
            fileWriter.write(content)
            fileWriter.close()
            true
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    throw RuntimeException("IOException occurred. ", e)
                }
            }
        }
    }

    fun makeDirs(filePath: String?): Boolean {
        val folderName = getFolderName(filePath)
        if (filePath == null || filePath.length == 0) {
            return false
        }
        val folder = File(folderName)
        return if (folder.exists() && folder.isDirectory) true else folder.mkdirs()
    }

    fun getFolderName(filePath: String?): String? {
        if (filePath == null || filePath.length == 0) {
            return filePath
        }
        val filePosi = filePath.lastIndexOf(File.separator)
        return if (filePosi == -1) "" else filePath.substring(0, filePosi)
    }
}