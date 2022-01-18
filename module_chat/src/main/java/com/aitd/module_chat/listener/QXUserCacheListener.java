package com.aitd.module_chat.listener;

import com.aitd.module_chat.QXUserInfo;
import com.aitd.module_chat.lib.QXContext;
import com.aitd.module_chat.pojo.QXGroupInfo;
import com.aitd.module_chat.pojo.QXGroupUserInfo;
import com.aitd.module_chat.utils.EventBusUtil;

public class QXUserCacheListener implements IQXCacheListener {

    public QXUserCacheListener() {

    }

    @Override
    public void onUserInfoUpdated(QXUserInfo qxUserInfo) {
        EventBusUtil.post(qxUserInfo);
    }

    @Override
    public void onGroupUserInfoUpdated(QXGroupUserInfo qxGroupUserInfo) {
        EventBusUtil.post(qxGroupUserInfo);
    }

    @Override
    public void onGroupUpdated(QXGroupInfo qxGroupInfo) {
        EventBusUtil.post(qxGroupInfo);
    }

    @Override
    public QXUserInfo getUserInfo(String userId) {
        return QXContext.getInstance().getUserInfoProvider() != null ? QXContext.getInstance().getUserInfoProvider().getUserInfo(userId) : null;
    }

    @Override
    public QXGroupUserInfo getGroupUserInfo(String groupId, String userId) {
        return QXContext.getInstance().getGroupUserInfoProvider() != null ? QXContext.getInstance().getGroupUserInfoProvider().getGroupUserInfo(groupId,userId) : null;
    }

    @Override
    public QXGroupInfo getGroupInfo(String groupId) {
        return QXContext.getInstance().getGroupProvider() != null ? QXContext.getInstance().getGroupProvider().getGroupInfo(groupId) : null;
    }
}
