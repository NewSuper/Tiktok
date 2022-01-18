package com.aitd.module_chat.utils;

import com.aitd.module_chat.lib.db.entity.MessageEntity;
import com.aitd.module_chat.pojo.ConversationType;
import com.aitd.module_chat.pojo.UserInfoCache;

public class TargetIdUtil {

    public static String getTargetId(MessageEntity messageEntity) {
        String targetId = "";
        if(messageEntity.getSendType().equals(ConversationType.TYPE_PRIVATE)) {
            targetId = messageEntity.getFrom();
            if(targetId.equals(UserInfoCache.INSTANCE.getUserId())) {
                targetId = messageEntity.getTo();
            }
        } else if(messageEntity.getSendType().equals(ConversationType.TYPE_GROUP)) {
            //to 为群组id
            targetId = messageEntity.getTo();
        } else if(messageEntity.getSendType().equals(ConversationType.TYPE_SYSTEM)) {
            //from 为系统id
            targetId = "QX:SYSTEM";
        }

        return targetId;
    }

    public static String getTargetId(String type, String from, String to) {
        String targetId = "";
        if(type.equals(ConversationType.TYPE_PRIVATE)) {
            targetId = from;
            if(targetId.equals(UserInfoCache.INSTANCE.getUserId())) {
                targetId = to;
            }
        } else if(type.equals(ConversationType.TYPE_GROUP)) {
            //to 为群组id
            targetId = to;
        } else if(type.equals(ConversationType.TYPE_SYSTEM)) {
            //from 为系统id
            targetId = "QX:SYSTEM";
        }

        return targetId;
    }
}
