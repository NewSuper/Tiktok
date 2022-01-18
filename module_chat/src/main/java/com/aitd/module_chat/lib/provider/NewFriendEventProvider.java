package com.aitd.module_chat.lib.provider;

import com.aitd.module_chat.ICustomEventProvider;
import com.aitd.module_chat.Message;
import com.aitd.module_chat.utils.qlog.QLog;

public class NewFriendEventProvider extends ICustomEventProvider.Stub {

    /**
     * 设置自定义push的tag
     *
     * @return
     */

    @Override
    public String getCustomEventTag() {
        return "QX:NewFriend";
    }

    /**
     * 接收自定义push消息
     *
     * @param message
     */
    @Override
    public void onReceiveCustomEvent(Message message) {
        QLog.i("onReceiveCustomEvent", "" + message.getMessageType() + " " + message.getConversationType());
    }
}
