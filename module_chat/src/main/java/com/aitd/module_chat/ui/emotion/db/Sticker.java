package com.aitd.module_chat.ui.emotion.db;


import com.aitd.module_chat.lib.QXIMKit;
import com.aitd.module_chat.ui.emotion.StickerItem;

import org.greenrobot.greendao.annotation.*;

@Entity
public class Sticker {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String localPath;
    private String originUrl;
    private int width;
    private int height;
    private int index;
    private String belong;

    @Generated(hash = 11161876)
    public Sticker(Long id, String name, String localPath, String originUrl,
                   int width, int height, int index, String belong) {
        this.id = id;
        this.name = name;
        this.localPath = localPath;
        this.originUrl = originUrl;
        this.width = width;
        this.height = height;
        this.index = index;
        this.belong = belong;
    }
    @Generated(hash = 1542104920)
    public Sticker() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setId(long id) {
        this.id = id;
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
    public String getBelong() {
        return this.belong;
    }
    public void setBelong(String belong) {
        this.belong = belong;
    }

    public static Sticker obtain(StickerItem stickerItem) {
        Sticker sticker = new Sticker();
        sticker.belong = QXIMKit.getInstance().getCurUserId();
        sticker.localPath = stickerItem.getLocalPath();
        sticker.originUrl = stickerItem.getOriginUrl();
        sticker.width = stickerItem.getWidth();
        sticker.height = stickerItem.getHeight();
        sticker.name = stickerItem.getName();
        sticker.index = stickerItem.getIndex();
        return sticker;
    }
}
