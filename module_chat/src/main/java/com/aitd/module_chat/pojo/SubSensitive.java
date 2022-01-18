package com.aitd.module_chat.pojo;

public class SubSensitive extends Sensitive {
    public void setSpecialCharacters(char[] chars) {
        SPECIAL_CHARS.clear();
        for (char c : chars) {
            SPECIAL_CHARS.add(c);
        }
    }
}
