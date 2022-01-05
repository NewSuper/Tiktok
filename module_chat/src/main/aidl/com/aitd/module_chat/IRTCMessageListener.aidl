// IRTCMessageListener.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

import com.aitd.module_chat.RTCSignalData;
interface IRTCMessageListener {
  void onReceive(in RTCSignalData signalData);
}