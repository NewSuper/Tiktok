// ICallReceiveMessageListener.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

import com.aitd.module_chat.CallReceiveMessage;
interface ICallReceiveMessageListener {
    void onReceive(in CallReceiveMessage receiveMessage);
}