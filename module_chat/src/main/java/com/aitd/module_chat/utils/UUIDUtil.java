package com.aitd.module_chat.utils;

import java.util.UUID;

public class UUIDUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
