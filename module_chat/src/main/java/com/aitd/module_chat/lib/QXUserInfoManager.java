package com.aitd.module_chat.lib;


import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;

import com.aitd.module_chat.QXUserInfo;
import com.aitd.module_chat.listener.IQXCacheListener;
import com.aitd.module_chat.pojo.QXGroupInfo;
import com.aitd.module_chat.pojo.QXGroupUserInfo;

import java.util.ArrayList;
import java.util.List;

public class QXUserInfoManager {

    private LruCache<String, QXUserInfo> mUsreInfoCache;
    private LruCache<String, QXGroupUserInfo> mGroupUserInfoCache;
    private LruCache<String, QXGroupInfo> mGroupCache;
    private boolean mIsCacheUserInfo;
    private boolean mIsCacheGroupInfo;
    private boolean mIsCacheGroupUserInfo;
    private String mAppKey;
    private Context mContext;
    private IQXCacheListener mCacheListener;
    private boolean mInitialized;
    private List<String> groupUserInfoKey = new ArrayList<>();

    private QXUserInfoManager() {
        mInitialized = false;
        mUsreInfoCache = new LruCache(256);
        mGroupUserInfoCache = new LruCache(256);
        mGroupCache = new LruCache(256);
    }

    public void init(Context context, String appKey, IQXCacheListener listener) {
        if (TextUtils.isEmpty(appKey)) {
        } else if (mInitialized) {
        } else {
            mContext = context;
            mAppKey = appKey;
            mCacheListener = listener;
            mInitialized = true;
        }
    }

    public static QXUserInfoManager getInstance() {
        return QXUserInfoManager.SingletonHolder.sInstance;
    }

    public void setIsCacheUserInfo(boolean mIsCacheUserInfo) {
        this.mIsCacheUserInfo = mIsCacheUserInfo;
    }

    public void setIsCacheGroupInfo(boolean mIsCacheGroupInfo) {
        this.mIsCacheGroupInfo = mIsCacheGroupInfo;
    }

    public void setIsCacheGroupUserInfo(boolean mIsCacheGroupUserInfo) {
        this.mIsCacheGroupUserInfo = mIsCacheGroupUserInfo;
    }

    public void setUserInfo(QXUserInfo qxUserInfo) {
        if (this.mIsCacheUserInfo) {
            if (!TextUtils.isEmpty(qxUserInfo.getId())) {
                this.mUsreInfoCache.put(qxUserInfo.getId(), qxUserInfo);
            }
        }
        if (this.mCacheListener != null) {
            this.mCacheListener.onUserInfoUpdated(qxUserInfo);
        }
    }

    public void setGroupInfo(QXGroupInfo qxGroupInfo) {
        if (this.mIsCacheGroupInfo) {
            if (!TextUtils.isEmpty(qxGroupInfo.getId())) {
                this.mGroupCache.put(qxGroupInfo.getId(), qxGroupInfo);
            }
        }
        if (this.mCacheListener != null) {
            this.mCacheListener.onGroupUpdated(qxGroupInfo);
        }
    }

    public QXUserInfo getUserInfo(String userId) {
        if (TextUtils.isEmpty(userId))
            return null;
        QXUserInfo userInfo = null;
        if (this.mIsCacheUserInfo) {
            userInfo = this.mUsreInfoCache.get(userId);
            if (userInfo != null) {
                return userInfo;
            }
        }

        return this.mCacheListener.getUserInfo(userId);
    }

    public void requestUserInfoUpdate(String userId) {
        mCacheListener.getUserInfo(userId);
    }

    public void requestGroupInfoUpdate(String groupId) {
        mCacheListener.getGroupInfo(groupId);
    }

    public void removeUserInfo(String userId) {
        if (this.mIsCacheUserInfo && this.mUsreInfoCache != null) {
            this.mUsreInfoCache.remove(userId);
        }
    }

    public void removeGroupUserInfo(String userId) {
        for (String key : groupUserInfoKey) {
            if (key.endsWith(userId)) {
                if (this.mIsCacheGroupInfo && this.mGroupUserInfoCache != null) {
                    this.mGroupUserInfoCache.remove(key);
                    return;
                }
            }
        }
    }

    public void setGroupUserInfo(QXGroupUserInfo qxGroupUserInfo) {
        if (this.mIsCacheGroupUserInfo) {
            String key = qxGroupUserInfo.getGroupId() + "_tqxd_" + qxGroupUserInfo.getUserId();
            if (!groupUserInfoKey.contains(key)) {
                groupUserInfoKey.add(key);
            }
            this.mGroupUserInfoCache.put(key, qxGroupUserInfo);
        }
        if (this.mCacheListener != null) {
            this.mCacheListener.onGroupUserInfoUpdated(qxGroupUserInfo);
        }
    }

    public QXGroupInfo getGroup(String groupId) {
        if (TextUtils.isEmpty(groupId))
            return null;
        QXGroupInfo qxGroupInfo = null;
        if (this.mIsCacheGroupInfo) {
            qxGroupInfo = this.mGroupCache.get(groupId);
            // 如果缓存没有
            if (qxGroupInfo == null) {
                // 调用第三方的信息提供
                qxGroupInfo = this.mCacheListener.getGroupInfo(groupId);
            }
        } else if (this.mCacheListener != null) {
            qxGroupInfo = this.mCacheListener.getGroupInfo(groupId);
        }
        return qxGroupInfo;
    }

    public QXGroupInfo refreshGroupInfo(String groupId) {
        return this.mCacheListener.getGroupInfo(groupId);
    }

    public QXGroupUserInfo getGroupUserInfo(String groupId, String userId) {
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(userId))
            return null;
        QXGroupUserInfo qxGroupUserInfo = null;
        String key = groupId + "_tqxd_" + userId;
        if (this.mIsCacheGroupUserInfo) {
            qxGroupUserInfo = this.mGroupUserInfoCache.get(key);
            if (qxGroupUserInfo == null) {
                qxGroupUserInfo = this.mCacheListener.getGroupUserInfo(groupId, userId);
            }
        } else if (this.mCacheListener != null) {
            qxGroupUserInfo = this.mCacheListener.getGroupUserInfo(groupId, userId);
        }
        return qxGroupUserInfo;
    }

    public void refreshGroupUserInfo(QXGroupUserInfo info) {
        if(info == null) {
            return;
        }
        if (TextUtils.isEmpty(info.getUserId())) {
            return;
        }

        if (this.mIsCacheGroupUserInfo) {
            for (String key : groupUserInfoKey) {
                if (key.endsWith(info.getUserId())) {
                    QXGroupUserInfo groupUserInfo  = mGroupUserInfoCache.get(key);
                    groupUserInfo.setNoteName(info.getNoteName());
                    groupUserInfo.setAvatarUri(info.getAvatarUri());
                    groupUserInfo.setAvatarExtraUrl(info.getAvatarExtraUrl());
                    groupUserInfo.setNameExtraUrl(info.getNameExtraUrl());
                    mGroupUserInfoCache.remove(key);
                    mGroupUserInfoCache.put(key, groupUserInfo);
                    mCacheListener.onGroupUserInfoUpdated(groupUserInfo);
                }
            }
        }
    }

    public QXGroupUserInfo getGroupUserInfo(String userId) {
        if (TextUtils.isEmpty(userId))
            return null;

        if (this.mIsCacheGroupUserInfo) {
            for (String key : groupUserInfoKey) {
                if (key.endsWith(userId)) {
                    return this.mGroupUserInfoCache.get(key);
                }
            }
        }
        return null;
    }

    private static class SingletonHolder {
        static QXUserInfoManager sInstance = new QXUserInfoManager();

        private SingletonHolder() {
        }
    }
}

