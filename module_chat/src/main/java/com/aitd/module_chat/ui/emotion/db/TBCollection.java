package com.aitd.module_chat.ui.emotion.db;


import com.aitd.module_chat.ui.emotion.CollectionItem;

import org.greenrobot.greendao.annotation.*;

@Entity
public class TBCollection {

    @Id(autoincrement = true)
    private Long id;
    private int messageId;
    private String localPath;
    private String originUrl;
    private String type;
    private String ownerId;


    @Generated(hash = 2051643941)
    public TBCollection(Long id, int messageId, String localPath, String originUrl,
                        String type, String ownerId) {
        this.id = id;
        this.messageId = messageId;
        this.localPath = localPath;
        this.originUrl = originUrl;
        this.type = type;
        this.ownerId = ownerId;
    }


    @Generated(hash = 854285776)
    public TBCollection() {
    }


    public static TBCollection obtain(CollectionItem item) {
        TBCollection collection = new TBCollection();
        collection.messageId = item.getMessageId();
        collection.localPath = item.getLocalPath();
        collection.originUrl = item.getOriginUrl();
        collection.type = item.getType();
        collection.ownerId = item.getOwnerId();
        return collection;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getMessageId() {
        return this.messageId;
    }


    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }


    public String getLocalPath() {
        return this.localPath;
    }


    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }


    public String getOriginUrl() {
        return this.originUrl;
    }


    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }


    public String getType() {
        return this.type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getOwnerId() {
        return this.ownerId;
    }


    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
