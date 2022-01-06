package com.aitd.module_chat.lib.menu;

import com.aitd.module_chat.pojo.MessageType;

import java.util.ArrayList;
import java.util.List;

public class QXMenuConfig {
    private static QXMenuManager qxMenuManager;

    public static void init() {
        qxMenuManager = QXMenuManager.getInstance();
        initTextMessageMenu();
        initImageMessageMenu();
        initVoiceMessageMenu();
        initVideoMessageMenu();
        initImageTextMessageMenu();
        initFileMessageMenu();
        initGeoMessageMenu();
        initReplyMessageMenu();
        initAudioCallMessageMenu();
        initVideoCallMessageMenu();
    }

    private static void initTextMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_COPY);
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_TEXT, list);
    }

    private static void initImageMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_IMAGE, list);
    }

    private static void initVoiceMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_AUDIO, list);
    }

    private static void initVideoMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_VIDEO, list);
    }

    private static void initImageTextMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_IMAGE_AND_TEXT, list);
    }

    private static void initFileMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_FILE, list);
    }

    private static void initGeoMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_GEO, list);
    }

    private static void initReplyMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_REPLY);
        list.add(QXPresetMenuSet.QX_MENU_FORWARD);
        list.add(QXPresetMenuSet.QX_MENU_FAVORITE);
        list.add(QXPresetMenuSet.QX_MENU_CHECK);
        list.add(QXPresetMenuSet.QX_MENU_RECALL);
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_REPLY, list);
    }

    private static void initAudioCallMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_AUDIO_CALL, list);
    }

    private static void initVideoCallMessageMenu() {
        List<QXMenu> list = new ArrayList<>();
        list.add(QXPresetMenuSet.QX_MENU_DELETE);
        qxMenuManager.putMenuList(MessageType.TYPE_VIDEO_CALL, list);
    }
}
