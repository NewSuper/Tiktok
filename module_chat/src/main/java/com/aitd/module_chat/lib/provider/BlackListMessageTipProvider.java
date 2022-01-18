package com.aitd.module_chat.lib.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitd.module_chat.Message;
import com.aitd.module_chat.QXError;
import com.aitd.module_chat.R;
import com.aitd.module_chat.pojo.BlackListError;
import com.google.gson.Gson;

public class BlackListMessageTipProvider {
    public static void handleView(String errorMsg, Message message, View errorRootView) {
        try {
            String[] msg = errorMsg.split("-");
            int code = Integer.parseInt(msg[0]);
            String content = msg[1];
            if (code == QXError.MESSAGE_BLACK_LIST.getCode()) {
                BlackListError blackListError = new Gson().fromJson(content, BlackListError.class);
                switch (blackListError.getCode()) {
                    case 2:
                        //拉黑
                        handleBlackList(message, errorRootView);
                        break;
                    case 0:
                        //陌生人
                        handleStranger(message, errorRootView);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void handleBlackList(Message message, View errorRootView) {
        ViewGroup viewGroup = (ViewGroup) errorRootView;
        viewGroup.removeAllViews();
        LayoutInflater.from(errorRootView.getContext()).inflate(R.layout.imui_layout_msg_failed_by_refuse, viewGroup, true);
    }

    public static void handleStranger(Message message, View errorRootView) {
        ViewGroup viewGroup = (ViewGroup) errorRootView;
        viewGroup.removeAllViews();
        LayoutInflater.from(errorRootView.getContext()).inflate(R.layout.imui_layout_msg_failed_not_friend, viewGroup, true);

    }
}

