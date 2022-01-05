// IMessageReceiptListener.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.Message;
interface IMessageReceiptListener {

    void onMessageReceiptReceived(inout Message message);
    void onMessageReceiptRead();
}