package com.aitd.library_common.network;

public class ServerException extends RuntimeException {
    public String code;
    public String errorMessage;
    public String data;

    public ServerException(String code,String message){
        super(message);
        this.code = code;
        this.errorMessage = message;
    }

    public void setData(String data){
        this.data = data;
    }
}
