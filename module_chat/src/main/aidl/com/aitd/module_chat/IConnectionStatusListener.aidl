// IConnectionStatusListener.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements

interface IConnectionStatusListener {
  void onChanged(int code);
}