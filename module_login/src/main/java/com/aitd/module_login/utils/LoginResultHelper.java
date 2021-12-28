package com.aitd.module_login.utils;

import android.app.Activity;
import android.os.Handler;

import com.aitd.library_common.app.BaseApplication;
import com.aitd.library_common.data.UserResponse;
import com.aitd.library_common.router.ARouterUrl;
import com.aitd.library_common.utils.PreferenceUtils;
import com.aitd.module_login.R;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;

public class LoginResultHelper {
    public static void loginAndShiMing(Activity context, UserResponse userResponse, String username, String password) {
        if (null != userResponse) {
            //没有实名认证
            if (!"1".equals(userResponse.getAuthState())) {

            } else {

            }
        } else {
            ToastUtils.showShort(context.getString(R.string.hk_notice_15));
        }
    }

    public static String getAccount() {
        try {
            String account = (null != BaseApplication.getUserBean() ? BaseApplication.getUserBean().getAccount() : null);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static String getUserId(Context context) {
//        try {
//            String userId = (null != BaseApplication.getUserBean() ? BaseApplication.getUserBean().getUserId() : null);
//            if (TextUtils.isEmpty(userId)) {
//                userId = PreferenceUtils.getString(context, "userId");
//            }
//            return userId;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }


    //  public static QXIMClient.ConnectCallBack connectCallBack;
    private static Handler mainHandler;

    public static void login(Activity context, UserResponse userResponse, String username, String password) {
        if (null != userResponse) {
            String userJson = new Gson().toJson(userResponse);
            BaseApplication.setUserCache(userJson);
            PreferenceUtils.setString(context, "user_img", userResponse.getImg());
            PreferenceUtils.setString(context, "login_username", username);
            PreferenceUtils.setString(context, "login_password", password);

            loginSuccess(context);
//            PreferenceUtils.setString(context, "currentToken", BaseApplication.getUserBean().getToken());
//            PreferenceUtils.setString(context, "user_img", userResponse.getImg());
//            PreferenceUtils.setString(context, "picToken", userResponse.getPicToken());
//            PreferenceUtils.setString(context, "login_password", password);
//            PreferenceUtils.setString(context, "login_username", username);
//            PreferenceUtils.setString(context, "userId", userResponse.getUserId());
//            //保存picToken
//            if (TextUtils.isEmpty(password)) {
//                String account = userResponse.getAccount();
//                //  FaceManagerDelegate.get().updatePicToken(account, userResponse.getPicToken());
//            }

            //  ARouter.getInstance().build(ARouterUrl.Main.ROUTE_MAIN_ACTIVITY).navigation();
            // context.finish();

//            //连接QXIM
//            WorkThredUtil.getInstance().execute(new Runnable() {
//                @Override
//                public void run() {
//                    if (connectCallBack == null) {
//                        connectCallBack = new QXIMClient.ConnectCallBack() {
//                            @Override
//                            public void onSuccess(@Nullable String s) {
//                                Log.e("登录: ", "onSuccess");
//                                QXIMManager.getInstance().cacheUserInfo();
//                                if (mainHandler == null) {
//                                    mainHandler = new Handler(Looper.getMainLooper());
//                                }
//                                mainHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        loginSuccess(context, userResponse);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onError(@Nullable String s) {
//                                Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onDatabaseOpened(int i) {
//                            }
//                        };
//
//                    }
//
//                    QXIMKit.connect(BaseApplication.getUserBean().getImToken(), connectCallBack);
//                }
//            });
        } else {
            ToastUtils.showShort(context.getString(R.string.server_err_tips));
        }
    }


    private static void loginSuccess(Activity context) {
        UserResponse bean = BaseApplication.getUserBean();
        // String gender = bean.getGender();
        // String userSn = bean.getUserSn();
//            if(!TextUtils.isEmpty(gender) && (gender.contains("1") || gender.contains("0"))){ //服务器返回值为Int json转换为double了
        if (bean.getGender().equals("1") || bean.getGender().equals("0")) {
//              Intent intent = new Intent(context, MainActivity.class);
//              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//              context.startActivity(intent);

//            ARouter.getInstance().build(ARouterUrl.Main.ROUTE_MAIN_ACTIVITY)
//                    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .navigation();

  System.out.println("--------1111111111---------");
            ARouter.getInstance().build(ARouterUrl.Login.ROUTER_SETTING_USER_ACTIVITY)
                    .navigation();
            //context.finish();
            System.out.println("--------2222222---------");
        } else {
            // Intent intent = new Intent(context, AddUserInfoActivity.class);
            // intent.putExtra("userData", userResponse);
            // context.startActivity(intent);
            ARouter.getInstance().build(ARouterUrl.Login.ROUTER_SETTING_USER_ACTIVITY)
                    .navigation();


            //   context.finish();
        }
        // connectCallBack = null;
        mainHandler = null;
    }
}
