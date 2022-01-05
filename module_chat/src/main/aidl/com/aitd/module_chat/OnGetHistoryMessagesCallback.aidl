// OnGetHistoryMessagesCallback.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.Message;
interface OnGetHistoryMessagesCallback {
      void onComplete(inout Message message);
}