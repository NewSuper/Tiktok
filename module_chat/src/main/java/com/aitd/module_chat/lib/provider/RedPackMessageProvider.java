package com.aitd.module_chat.lib.provider;

import android.view.View;
import android.widget.TextView;

import com.aitd.module_chat.CustomMessage;
import com.aitd.module_chat.Message;
import com.aitd.module_chat.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedPackMessageProvider extends MessageProvider {

    @NotNull
    @Override
    public String getProviderTag() {
        return "QX:cccc";
    }

    @Override
    public int getViewId() {
        return R.layout.imui_layout_msg_custom_system;
    }

    @Override
    public void bindView(@Nullable View view, @NotNull Message data) {
        CustomMessage customMessage;
        if(data != null) {
            customMessage = (CustomMessage) data.getMessageContent();
            TextView tv_title = view.findViewById(R.id.tv_title);
            tv_title.setText(customMessage.getContent());
        }
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
