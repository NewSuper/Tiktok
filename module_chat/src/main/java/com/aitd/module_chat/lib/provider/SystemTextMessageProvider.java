package com.aitd.module_chat.lib.provider;

import android.view.View;
import android.widget.TextView;

import com.aitd.module_chat.CustomMessage;
import com.aitd.module_chat.Message;
import com.aitd.module_chat.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class SystemTextMessageProvider extends MessageProvider {

    @NotNull
    @Override
    public String getProviderTag() {
        return "SYS:TextMsg";
    }

    @Override
    public int getViewId() {
        return R.layout.imui_layout_msg_custom_system;
    }

    @Override
    public void bindView(@Nullable View view, @NotNull Message data) {
        CustomMessage customMessage;
        String messageTitle = "";
        String messageContent = "";
        if(data != null) {
            customMessage = (CustomMessage) data.getMessageContent();
            JSONObject json = null;
            try {
                json = new JSONObject(customMessage.getContent());
                if(json != null){
                    messageTitle = json.getString("title");
                    messageContent = json.getString("content");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView tv_title = view.findViewById(R.id.tv_title);
            TextView tv_content = view.findViewById(R.id.tv_content);
            tv_title.setText(messageTitle);
            tv_content.setText(messageContent);
        }
    }

    /**
     * 是否为通知消息
     * @return
     */
    @Override
    public boolean isNotice() {
        return true;
    }

    @NotNull
    @Override
    public BubbleStyle getBubbleStyle() {
        return null;
    }

    @Override
    public void onClick(@Nullable View view, @NotNull Message data) {

    }
}
