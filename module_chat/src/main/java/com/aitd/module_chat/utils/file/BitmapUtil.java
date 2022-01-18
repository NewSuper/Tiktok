package com.aitd.module_chat.utils.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class BitmapUtil {
    public static String saveBitmap(Bitmap bitmap) {
        String path = AlbumUtils.getAlbumTypeFile(AlbumType.IMAGE,".jpg");
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            path = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static void compressImage(Context context, Uri uri, String targetPath, OnCompressListener listener) {
        //  Luban.with(context).load(uri).setTargetDir(targetPath).setCompressListener(listener).launch();
        Luban.with(context).load((List<String>) uri).setTargetDir(targetPath).setCompressListener(listener).launch();
    }


    /**
     * 图片压缩
     * @param context
     * @param filePath 文件路径
     * @param listener 压缩回调
     */
    public static void compressImage(Context context, String filePath, OnCompressListener listener) {
        Luban.with(context)
                .load(filePath)
                .ignoreBy(200)  // 忽略不压缩图片的大小,默认是100
                .setTargetDir(AlbumUtils.getAlbumTypeDir(AlbumType.IMAGE)) //设置压缩后文件存储位置
                .setCompressListener(listener).launch();
    }
}
