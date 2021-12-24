package com.aitd.library_common.encrypt;

import com.aitd.library_common.base.Constans;
import com.aitd.library_common.encrypt.aes.AESUtils;

import java.util.Random;

public class EncryptHelper {
    public static class EncryptBean {
        String aesParamContent;
        String rsaEncry;

        public String getAesParamContent() {
            return aesParamContent;
        }

        public void setAesParamContent(String aesParamContent) {
            this.aesParamContent = aesParamContent;
        }

        public String getRsaEncry() {
            return rsaEncry;
        }

        public void setRsaEncry(String rsaEncry) {
            this.rsaEncry = rsaEncry;
        }
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static EncryptBean encryptData(String paramContentJson) {
        try {
            String aesKey = getRandomString(16);
            String aesParamContent = AESUtils.encrypt(paramContentJson, aesKey);
//        String aesParamContent = AesEncryptUtils.encrypt(paramContentJson, aesKey);

            EncryptRequest encryptRequest = new EncryptRequest();
            encryptRequest.setData(aesParamContent);
            //
            String headerRsaEncry = RSAUtil.encryptByPublicKeyString(aesKey, Constans.Key.publickey);

            EncryptBean bean = new EncryptBean();
            bean.setAesParamContent(aesParamContent);
            bean.setRsaEncry(headerRsaEncry);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
