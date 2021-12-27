package com.aitd.library_common.imageload;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.DrawableRes;

public class ImageLoader {
    @DrawableRes
    public static int defaultPlaceholderRes = 0;//全局默认加载中的图片
    @DrawableRes
    public static int defaultErrorRes = 0;//全局默认加载失败的图片

    private Object model;
    private ImageView target;
    private RequestListener<Drawable> requestListener;
    private RequestOptions requestOptions;
    private Target<Drawable> targetDrawable;

    public ImageLoader(Builder builder) {
        this.model = builder.model;
        this.target = builder.target;
        this.requestListener = builder.requestListener;
        this.requestOptions = builder.requestOptions;
        this.targetDrawable = builder.targetDrawable;
        //判断非空情况
        if (target == null) {
            return;
        }
        //判断Activity是否关闭
        Context context = target.getContext();
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        try {
            RequestBuilder<Drawable> requestBuilder = Glide.with(target.getContext()).load(model).apply(requestOptions);
            if (requestListener != null) {
                requestBuilder.listener(requestListener);
            }
            if (targetDrawable == null) {
                requestBuilder.into(target);
            } else {
                requestBuilder.into(targetDrawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Builder Builder(Object model, ImageView target) {
        return new Builder(model, target);
    }

    public static class Builder {
        private Object model;
        private ImageView target;
        private boolean circleCrop; //是否加载圆形图片
        @DrawableRes
        private int placeHolderRes;//加载中显示的图片
        @DrawableRes
        private int errorRes;//加载错误显示的图片
        @DrawableRes
        private int defaultPlaceholderRes = ImageLoader.defaultPlaceholderRes;//默认加载中显示的图片
        @DrawableRes
        private int defaultErrorRes = ImageLoader.defaultErrorRes;//默认加载失败显示的图片
        private boolean showDefaultPlaceholder;//是否展示默认加载中的图片
        private boolean showDefaultError;//是否展示默认加载错误的图片
        private MultiTransformation multiTransformation;//转换器
        private RequestListener<Drawable> requestListener;//加载监听
        private DiskCacheStrategy diskCacheStrategy;//缓存方式
        private RequestOptions requestOptions;//加载配置
        private Target<Drawable> targetDrawable;//drawable加载器

        private Builder(Object model, ImageView target) {
            this.model = model;
            this.target = target;
        }

        public Builder(Target<Drawable> targetDrawable) {
            this.targetDrawable = targetDrawable;
        }

        public Builder setPlaceHolderRes(int placeHolderRes) {
            this.placeHolderRes = placeHolderRes;
            return this;
        }

        public Builder setErrorRes(int errorRes) {
            this.errorRes = errorRes;
            return this;
        }

        public Builder setShowDefaultPlaceholder(boolean showDefaultPlaceholder) {
            this.showDefaultPlaceholder = showDefaultPlaceholder;
            return this;
        }

        public Builder setShowDefaultError(boolean showDefaultError) {
            this.showDefaultError = showDefaultError;
            return this;
        }

        public Builder setShowDefaultRes(boolean showDefault) {
            if (showDefault) {
                this.showDefaultPlaceholder = true;
                this.showDefaultError = true;
            }
            return this;
        }

        public Builder setMultiTransformation(MultiTransformation multiTransformation) {
            this.multiTransformation = multiTransformation;
            return this;
        }

        public ImageLoader setRequestListener(RequestListener<Drawable> requestListener) {
            this.requestListener = requestListener;
            return build();
        }

        public ImageLoader setDiskCacheStrategy(DiskCacheStrategy diskCacheStrategy) {
            this.diskCacheStrategy = diskCacheStrategy;
            return build();
        }

        //加载图片
        public ImageLoader show() {
            return build();
        }

        //加载图片为园
        public ImageLoader showAsCircle() {
            this.circleCrop = true;
            return build();
        }

        //加载图片为圆角
        public ImageLoader showAsFillet(int fillet) {
            this.multiTransformation = new MultiTransformation<>(new CenterCrop(), new GlideRoundTransform(target.getContext(), fillet));
            return build();
        }

        private ImageLoader build() {
            if (requestOptions == null) {
                requestOptions = new RequestOptions();
            }
            if (circleCrop) {
                requestOptions.circleCrop();
            }
            if (showDefaultPlaceholder && defaultPlaceholderRes != 0) {
                requestOptions.placeholder(defaultPlaceholderRes);
            }
            if (showDefaultError && defaultErrorRes != 0) {
                requestOptions.error(defaultErrorRes);
            }
            if (placeHolderRes != 0) {
                requestOptions.placeholder(placeHolderRes);
            }
            if (errorRes != 0) {
                requestOptions.error(errorRes);
            }
            if (multiTransformation != null) {
                requestOptions.transform(multiTransformation);
            }
            if (diskCacheStrategy != null) {
                requestOptions.diskCacheStrategy(diskCacheStrategy);
            }
            return new ImageLoader(this);
        }
    }
}
