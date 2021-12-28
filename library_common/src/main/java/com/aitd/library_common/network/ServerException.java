package com.aitd.library_common.network;

/**
 * Author: palmer
 * time: 2019/10/28
 * email:lxlfpeng@163.com
 * desc: 自定义服务器端异常
 */
public class ServerException extends RuntimeException {
    public String code;
    public String errorMessage;
    public String data;

    public ServerException(String code, String message) {
        super(message);
        this.errorMessage = message;
        this.code = code;
    }

    public void setData(String data) {
        this.data = data;
    }
}
