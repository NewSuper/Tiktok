package com.aitd.module_chat.utils.file;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class GlideRoundTransform extends BitmapTransformation {

    private static float radius = 8.0F;

    public GlideRoundTransform() {
        this(8);
    }

    public GlideRoundTransform(int dp) {
        radius = Resources.getSystem().getDisplayMetrics().density * (float)dp;
    }

    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        } else {
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0.0F, 0.0F, (float)source.getWidth(), (float)source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}
