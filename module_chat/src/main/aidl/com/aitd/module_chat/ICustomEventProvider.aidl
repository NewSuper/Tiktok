// ICustomEventProvider.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.Message;
interface ICustomEventProvider {
    String getCustomEventTag();
    void onReceiveCustomEvent(inout Message message);
}