package com.aitd.library_common.base;

public class Constans {
    public static final int SDKAPPID = 1400440195;
    public static final String BASE_APP_KEY_TEST = "3ccf3f154c944464a25771e870c0f32f";                                // 测试
    public static String BASE_APP_KEY = "AFwwDQYJKoCIhvcNAQEBBQADSwEw4AJBALWgVFXBO5W7aju9GzCRHlKkM5AMzEb";  //正试
    public static final String ERCODE = "http://reg.aitdcoin.com/#/download";
    public static final String SGP_APP = "http://download.sgpexchange.com/ ";
    public static String IMO_KEY = "EAD06EB2476A11C7";
    public static String airticalUrl = "https://download.aitdcoin.com/aitd/article?";
    public static String jintianUrl = "https://download.aitdcoin.com/aitd/recommendedAllowanceRule?";
    public static String BUSINESS_ARTICLE = "https://download.aitdcoin.com/aitd/article/businessArticle";  // 商学院
    public static String INVATE_PERSON_URL = "https://reg.aitdcoin.com";//新環境正式
    public static String BASE_URL = "https://api.aitdcoin.com/";  //新環境正式
    public static String BASE_URL_TOUBAO = "https://api.aitdcoin.com/";  //新環境正式
    public static String BaseWebUrl1 = "https://workorder.aitdcoin.com";//新環境正式
    public static String BASE_LIVE = "https://api.aitdcoin.com/";//新環境正式
    public static String BASE_ZHIYA = "https://api.aitdcoin.com/";//新環境正式
    public static String BASE_KEFU_URL = "https://customer.aitdcoin.com:443/im/text/0mnpw5.html";
    public static String EVENT_URL = "http://datacenter-push-log.aitdcoin.com/";  //埋点上报
    public static String APP_ID = "0e24af20-dfa9-4aa9-b56e-4bcbbcdac919";
    public static String APP_SECRET = "267f56ce-f729-4f1e-ab6b-fdf4c8a612e4";
    public static String IM_APP_KEY = "192ab05579ea421f9e8a1e1932732c45";
    public static String IM_APP_URL = "https://qx-api.aitdcoin.com/";
    public static String NOTICE_OF_CLAIM = "";

    public static void appVersionSwitch(int version) {
        if (version == VersionConstant.VERSION_TEST) {
            INVATE_PERSON_URL = "http://reg-test.aitdcoin.com";//新测试域名
            BASE_URL_TOUBAO = "http://api-test.aitdcoin.com/";  //新测试域名
            BaseWebUrl1 = "http://workorder-test.aitdcoin.com";//新测试域名
            BASE_LIVE = "http://api-test.aitdcoin.com/";//直播服务
            BASE_URL = "http://api-test.aitdcoin.com/";  //用户服务
            BASE_ZHIYA = "http://api-test.aitdcoin.com/";//新测试域名
            BASE_KEFU_URL = "https://customer-test.aitdcoin.com/help/index.html";
            airticalUrl = "http://download-test.aitdcoin.com/aitd/article?";
            BUSINESS_ARTICLE = "https://download-test.aitdcoin.com/aitd/article/businessArticle";  // 商学院
            BASE_APP_KEY = BASE_APP_KEY_TEST;
            EVENT_URL = "http://point-upload-test.aitdcoin.com/";
            IM_APP_KEY = "a7541e36a0414238a3ee92de482dac72";
            IM_APP_URL = "https://qx-api-beta.aitdcoin.com/";
            NOTICE_OF_CLAIM = "https://download-test.aitdcoin.com/aitd/claimsNotice?registNo=";
        } else if (version == VersionConstant.VERSION_DEV) {
            INVATE_PERSON_URL = "https://reg-test.aitdcoin.com";//新开发域名
            BASE_URL = "https://api-dev.aitdcoin.com/";  //新开发域名
            BASE_URL_TOUBAO = "https://api-dev.aitdcoin.com/";  //新开发域名
            BaseWebUrl1 = "https://workorder-test.aitdcoin.com";//新开发域名
            BASE_LIVE = "https://api-dev.aitdcoin.com/";//新开发域名
            BASE_ZHIYA = "http://api-dev.aitdcoin.com/";//新开发域名
            BASE_KEFU_URL = "https://customer-test.aitdcoin.com/help/index.html";
            airticalUrl = "http://download-test.aitdcoin.com/aitd/article?";
            jintianUrl = "https://download-test.aitdcoin.com/aitd/recommendedAllowanceRule?";
            BASE_APP_KEY = BASE_APP_KEY_TEST;
            EVENT_URL = "http://point-upload-dev.aitdcoin.com/";
            IM_APP_KEY = "a7541e36a0414238a3ee92de482dac72";
            IM_APP_URL = "https://qx-api-beta.aitdcoin.com/";
            BUSINESS_ARTICLE = "https://download-test.aitdcoin.com/aitd/article/businessArticle";  // 商学院
            NOTICE_OF_CLAIM = "https://download-test.aitdcoin.com/aitd/claimsNotice?registNo=";
        } else if (version == VersionConstant.VERSION_PRODUCE) {
            INVATE_PERSON_URL = "https://reg.hznixiya.com";   //新環境正式
            BASE_URL = "https://api.hznixiya.com/";  //新環境正式
            BASE_URL_TOUBAO = "https://api.hznixiya.com/";  //新環境正式
            BASE_LIVE = "https://api.hznixiya.com/";//新環境正式
            BASE_ZHIYA = "https://api.hznixiya.com/";//新環境正式
            BaseWebUrl1 = "https://workorder.hznixiya.com";//新環境正式
            BASE_KEFU_URL = "https://customer.hznixiya.com/help/index.html";
            airticalUrl = "https://download.hznixiya.com/aitd/article?";
            jintianUrl = "https://download.hznixiya.com/aitd/recommendedAllowanceRule?";
            EVENT_URL = "https://point-upload.hznixiya.com/";
            APP_ID = "af3d1def-4e0b-4adb-a93b-e1363c21b4bf";
            APP_SECRET = "da719b06-07c2-4bd9-bc18-136c7acd0c15";
            IM_APP_KEY = "192ab05579ea421f9e8a1e1932732c45";
            IM_APP_URL = "https://qx-api.hznixiya.com/";
            BUSINESS_ARTICLE = "https://download.hznixiya.com/aitd/article/businessArticle";  // 商学院
            NOTICE_OF_CLAIM = "https://download.hznixiya.com/aitd/claimsNotice?registNo=";
        } else if (version == VersionConstant.VERSION_PRE_ENVIROMENT) {  // 预发布服务地址 www-pre.sgpexchange.com
            INVATE_PERSON_URL = "http://reg-pre.aitdcoin.com";//新开发域名
            BASE_URL = "http://api-pre.aitdcoin.com/";  //新开发域名
            BASE_URL_TOUBAO = "http://api-pre.aitdcoin.com/";  //新开发域名
            BaseWebUrl1 = "http://api-pre.aitdcoin.com";//新开发域名
            BASE_LIVE = "http://api-pre.aitdcoin.com/";//新开发域名
            BASE_ZHIYA = "http://api-pre.aitdcoin.com/";//新开发域名
            BASE_KEFU_URL = "https://customer.aitdcoin.com:443/im/text/0mnpw5.html";
            airticalUrl = "https://download-pre.aitdcoin.com/aitd/article?";
            jintianUrl = "https://download-pre.aitdcoin.com/aitd/recommendedAllowanceRule?";
            BASE_APP_KEY = BASE_APP_KEY_TEST;
            EVENT_URL = "http://point-upload-pre.aitdcoin.com/";
            APP_ID = "9c52a9db-0ecc-48bc-a78d-4c823d292533";
            APP_SECRET = "755abfa1-2663-455f-a76a-d344e9e9c4eb";
            IM_APP_KEY = "1307f884c42342d1841e0363179f59c9"; //"b51702d42bf54fcb9d1624227f8ea637";
            IM_APP_URL = "https://qx-api-beta.aitdcoin.com/";
            BUSINESS_ARTICLE = "https://download-pre.aitdcoin.com/aitd/article/businessArticle";  // 商学院
            NOTICE_OF_CLAIM = "https://download-pre.aitdcoin.com/aitd/claimsNotice?registNo=";
        }
    }

    public interface Key {
        int DEVICE_OS = 1;
        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6xXyWXD5bHdmNGLmp2T6ZDgDi30tZhLUoIyHbstRCybnmnZ420qcF7hCHHMKKbjvAyYXeAZm95USF6zx0NIB1hOPlUswl0aWH7b23WFTcyY97NsLMIfnjU2SN3i8NPBfQslXT7zsU9f6aY5BIZWNu3IUdYSR8aBkBVjz2VYy29wIDAQAB";
    }

}

