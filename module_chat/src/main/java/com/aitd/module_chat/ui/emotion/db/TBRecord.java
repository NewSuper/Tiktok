package com.aitd.module_chat.ui.emotion.db;


import com.aitd.module_chat.ui.emotion.RecordItem;

import org.greenrobot.greendao.annotation.*;


@Entity
public class TBRecord {

    @Id(autoincrement = true)
    private Long id;
    private String messageId;
    private String localPath;
    private String originUrl;
    private String type;
    private String ownerId;

    public static TBRecord obtain(RecordItem item) {
        TBRecord record = new TBRecord();
        record.messageId = item.getMessageId();
        record.localPath = item.getLocalPath();
        record.originUrl = item.getOriginUrl();
        record.type = item.getType();
        record.ownerId = item.getOwnerId();
        return record;
    }

    @Generated(hash = 398037346)
    public TBRecord(Long id, String messageId, String localPath, String originUrl,
                    String type, String ownerId) {
        this.id = id;
        this.messageId = messageId;
        this.localPath = localPath;
        this.originUrl = originUrl;
        this.type = type;
        this.ownerId = ownerId;
    }
    @Generated(hash = 1142041988)
    public TBRecord() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMessageId() {
        return this.messageId;
    }
    public void setMessageId(String messageId) {
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
