package com.aitd.module_login.utils;

import android.content.Context;

import com.aitd.library_common.language.LanguageSpUtil;
import com.netease.nis.captcha.Captcha;
import com.netease.nis.captcha.CaptchaConfiguration;
import com.netease.nis.captcha.CaptchaListener;

public class NoSenseCaptchaUtils {
    public static final String NOSENSECAPTCHAID = "06c4873f4bbe412a92a75ccf44be83d7";//网易图形验证码获取ID

    public static Captcha getCaptcha(Context context, CaptchaListener captchaListener) {
        return getCaptcha(context, NOSENSECAPTCHAID, captchaListener);
    }

    //获取验证码类
    public static Captcha getCaptcha(Context context, String noSenseCaptchaId, CaptchaListener captchaListener) {


        //图形验证码
        int failedMaxRetryCount = 3; //超时重连次数
        CaptchaConfiguration configuration = new CaptchaConfiguration.Builder()
                .captchaId(noSenseCaptchaId)// 验证码业务id
                // .url(captchaUrl) // 接入者无需设置，该接口为调试接口
                // 验证码类型，默认为普通验证码（滑块拼图、图中点选、短信上行），如果要使用无感知请设置以下类型,否则请不要设置
                // .mode(CaptchaConfiguration.ModeType.MODE_INTELLIGENT_NO_SENSE)
                .listener(captchaListener) // 验证码回调监听器
                //.timeout(1000 * 10) // 超时时间，一般无需设置
                .languageType(getLanguageType()) // 验证码语言类型，一般无需设置，可设置值请参看下面验证码语言枚举类介绍
                //.debug(true) // 是否启用debug模式，一般无需设置
                // 设置验证码框的位置和宽度，一般无需设置，不推荐设置宽高，后面将逐步废弃该接口
                //.position(-1, -1, 0, 0)
                // 自定义验证码滑动条滑块的不同状态图片
                //  .controlBarImageUrl(controlBarStartUrl, controlBarMovingUrl, controlBarErrorUrl)
                //  .backgroundDimAmount(dimAmount) // 验证码框遮罩层透明度，一般无需设置
                .touchOutsideDisappear(true)  // 点击验证码框外部是否消失，默认为系统默认配置(消失)，设置false不消失
                .useDefaultFallback(true) // 是否使用默认降级方案，默认开启
                .failedMaxRetryCount(failedMaxRetryCount) // 当出现服务不可用时，尝试加载的最大次数，超过此次数仍然失败将触发降级，默认3次
                .hideCloseButton(false)//是否隐藏右上角关闭按钮，默认不隐藏
                // .loadingText(etLoadingText.getText().toString()) // 设置loading文案
                // .loadingAnimResId(loadingAnimResId) // 设置loading动画，传入动画资源id
                //.ipv6(isIpv6) // 是否为ipv6网络
                .build(context); // Context，请使用Activity实例的Context
        return Captcha.getInstance().init(configuration);
    }

    private static CaptchaConfiguration.LangType getLanguageType() {
        String languageCode = LanguageSpUtil.getLanguageType().getCode();
        CaptchaConfiguration.LangType type;
        switch (languageCode) {
            case "1":
                type = CaptchaConfiguration.LangType.LANG_ZH_CN;
                break;
            case "3":
                type = CaptchaConfiguration.LangType.LANG_EN;
                break;
            case "4":
                type = CaptchaConfiguration.LangType.LANG_KO;
                break;
            case "5":
                type = CaptchaConfiguration.LangType.LANG_JA;
                break;
            default:
                type = CaptchaConfiguration.LangType.LANG_ZH_TW;
        }
        return type;
    }
}
