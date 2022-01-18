package com.aitd.module_login.utils;

import android.app.Activity;
import android.content.Intent;

import com.aitd.library_common.app.BaseApplication;
import com.aitd.library_common.data.UserResponse;
import com.aitd.library_common.router.ARouterUrl;
import com.aitd.library_common.utils.PreferenceUtils;
import com.aitd.module_login.R;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

public class LoginResultHelper {
    public static void login(Activity context, UserResponse userResponse, String username, String password) {
        if (null != userResponse) {
            String userJson = new Gson().toJson(userResponse);
            BaseApplication.setUserCache(userJson);
            PreferenceUtils.setString(context, "user_img", userResponse.getImg());
            PreferenceUtils.setString(context, "login_username", username);
            PreferenceUtils.setString(context, "login_password", password);
            loginSuccess(context);
        } else {
            ToastUtils.showShort(context.getString(R.string.server_err_tips));
        }
    }

    private static void loginSuccess(Activity context) {
        UserResponse bean = BaseApplication.getUserBean();
        if (bean.getGender().equals("1") || bean.getGender().equals("0")) {
            ARouter.getInstance().build(ARouterUrl.Main.ROUTE_MAIN_ACTIVITY)
                    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .navigation();
            context.finish();
        } else {
            ARouter.getInstance().build(ARouterUrl.Login.ROUTER_SETTING_USER_ACTIVITY)
                    .navigation();
        }
    }
}
