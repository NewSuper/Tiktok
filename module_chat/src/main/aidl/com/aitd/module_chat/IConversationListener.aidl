// IConversationListener.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.Conversation;
interface IConversationListener {
 void onChanged(inout List<Conversation> list);
 }