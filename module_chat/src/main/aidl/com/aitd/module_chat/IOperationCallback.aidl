// IOperationCallback.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

interface IOperationCallback {
    void onComplete();
    void onFailure(int code);
}