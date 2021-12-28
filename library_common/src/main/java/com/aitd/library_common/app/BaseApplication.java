package com.aitd.library_common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.aitd.library_common.base.Constans;
import com.aitd.library_common.base.VersionConstant;
import com.aitd.library_common.data.UserResponse;
import com.aitd.library_common.language.MultiLanguageUtil;
import com.aitd.library_common.utils.JsonUtil;
import com.aitd.library_common.utils.PreferenceUtils;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.tencent.mmkv.MMKV;

import androidx.multidex.MultiDexApplication;

public class BaseApplication extends MultiDexApplication {

    private static Context mApplicationContext;
    private static Application mApplication;
    private static UserResponse userBean = UserResponse.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationContext = this;
        mApplication = this;
        initARouter();
        initMMKV();
        initUserCache();
        initLanguage();
        Constans.appVersionSwitch(VersionConstant.VERSION);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(base));
    }

    private void initMMKV() {
        MMKV.initialize(this);
    }

    private void initARouter() {
      //  if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
       // }
        ARouter.init(this);
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }

    //检测本地目录是否有缓存用户登录
    private static void initUserCache() {
        String userJson = PreferenceUtils.getString(getAppContext(), "user");
        if (!TextUtils.isEmpty(userJson)) {
            userBean = JsonUtil.json2Bean(userJson, UserResponse.class);
        }
    }

    //登录成功后对用户数据进行缓存
    public static void setUserCache(String json) {
        PreferenceUtils.setString(getAppContext(), "user", json + "");
        userBean = JsonUtil.json2Bean(json, UserResponse.class);
    }

    private void initLanguage() {
        registerActivityLifecycleCallbacks(MultiLanguageUtil.callbacks);
    }

    public static UserResponse getUserBean() {
        if (userBean == null) {
            userBean = UserResponse.getInstance();
        }
        return userBean;
    }

    //AutoSize适配
    public static void adaptScreen(Activity activity, int sizeInPx, boolean isVerticalSlide) {
        DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();//系统屏幕尺寸
        DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();//app屏幕尺寸
        DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();//activity屏幕尺寸
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / sizeInPx;
        } else {
            activityDm.density = activityDm.heightPixels / sizeInPx;
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (160 * activityDm.densityDpi);
        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }
}
