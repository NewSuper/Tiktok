package com.aitd.module_login.bean;

public class LoginFaceRequest {
    public static final int TYPE_ACCOUT_FACE = 1; //登录方式:1账号人脸;2账号密码；3手机或者邮箱
    public static final int TYPE_ACCOUT_PASSWORD = 2;
    public static final int TYPE_MOBILE_EMAIL = 3;
    public static final int TYPE_REAL_NAME_IMG_FACE = 4;

    private String account;
    private String faceImage;
    private int operatingSystem;
    private String password;
    private String regDevice;
    private String regDeviceId;
    private String picToken;
    private int type;
    private int imageCode;
    private String neCaptchaValidate;

    public String getNeCaptchaValidate() {
        return neCaptchaValidate;
    }

    public void setNeCaptchaValidate(String neCaptchaValidate) {
        this.neCaptchaValidate = neCaptchaValidate;
    }

    public int getImageCode() {
        return imageCode;
    }

    public void setImageCode(int imageCode) {
        this.imageCode = imageCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }

    public int getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(int operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegDevice() {
        return regDevice;
    }

    public void setRegDevice(String regDevice) {
        this.regDevice = regDevice;
    }

    public String getRegDeviceId() {
        return regDeviceId;
    }

    public void setRegDeviceId(String regDeviceId) {
        this.regDeviceId = regDeviceId;
    }

    public int getType() {
        return type;
    }

    public String getPicToken() {
        return picToken;
    }

    public void setPicToken(String picToken) {
        this.picToken = picToken;
    }

    public void setType(int type) {
        this.type = type;
    }
}
