// ISendMessageCallback.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.Message;
interface ISendMessageCallback {
   void onAttached(inout Message message);
    void onSuccess();
    void onError(int errorOrdinal, inout Message message);
}