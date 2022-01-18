package com.aitd.module_chat.listener;

import com.aitd.module_chat.QXUserInfo;
import com.aitd.module_chat.pojo.QXGroupInfo;
import com.aitd.module_chat.pojo.QXGroupUserInfo;

public interface IQXCacheListener {

    void onUserInfoUpdated(QXUserInfo qxUserInfo);

    void onGroupUserInfoUpdated(QXGroupUserInfo qxGroupUserInfo);

    void onGroupUpdated(QXGroupInfo qxGroupInfo);

    QXUserInfo getUserInfo(String userId);

    QXGroupUserInfo getGroupUserInfo(String groupId, String userId);

    QXGroupInfo getGroupInfo(String groupId);
}
