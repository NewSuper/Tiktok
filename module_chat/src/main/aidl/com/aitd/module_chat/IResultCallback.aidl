// IResultCallback.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

interface IResultCallback {

        void onSuccess();

        void onFailed(int errorOrdinal);
}