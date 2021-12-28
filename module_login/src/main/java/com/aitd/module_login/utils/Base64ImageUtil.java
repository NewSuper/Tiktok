package com.aitd.module_login.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64ImageUtil {
    /**
     * 伟入url.先压缩后转base64
     *
     * @param srcPath
     * @return
     */
    public static String getImageBase64(String srcPath) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = 1280f;
            float ww = 720f;
            int be = 1;
            if (w > h && w > ww) {
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) be = 1;
            newOpts.inSampleSize = be;
            Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            return compressImageToBase64(bitmap);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String compressImageToBase64(Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / (1024 * 3) > 100) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
            byte[] bitmapBytes = baos.toByteArray();
            String result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
