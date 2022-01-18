package com.aitd.module_chat.pojo.event;

import com.aitd.module_chat.Message;

public class EventManage {
    /**
     * 图片详情界面，点击转发
     */
    public static class OnImagePageClick {
        public int clickType;//1表示转发，2表示收藏，3表示保存图片
        public Message message;
        public boolean imgCollect;//
        public OnImagePageClick(int clickType, Message message, boolean imgCollect) {
            this.clickType = clickType;
            this.message = message;
            this.imgCollect = imgCollect;
        }
    }

    /**
     * 视频详情播放界面，点击转发
     */
    public static class OnVideoPlayClick {
        public int clickType;//1表示转发，2表示收藏
        public Message message;
        public boolean videoCollect;

        public OnVideoPlayClick(int clickType, Message message, boolean videoCollect) {
            this.clickType = clickType;
            this.message = message;
            this.videoCollect = videoCollect;
        }
    }


}
