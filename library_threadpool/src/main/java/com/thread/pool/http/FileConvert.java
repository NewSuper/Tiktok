package com.thread.pool.http;

import android.os.Environment;

import java.io.File;

//字符串的转换器
public class FileConvert implements Converter<File> {

    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载目标文件夹

    private String folder;                  //目标文件存储的文件夹路径
    private String fileName;                //目标文件存储的文件名

    public FileConvert() {
        this(null);
    }

    public FileConvert(String fileName) {
        this(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER, fileName);
    }

    public FileConvert(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }



    @Override
    public File convertResponse(Response response) throws Throwable {
        return null;
    }

}