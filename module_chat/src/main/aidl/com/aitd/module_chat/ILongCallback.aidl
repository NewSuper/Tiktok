// ILongCallback.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

interface ILongCallback {
    void onComplete(long result);
       void onFailure(int code) ;
}