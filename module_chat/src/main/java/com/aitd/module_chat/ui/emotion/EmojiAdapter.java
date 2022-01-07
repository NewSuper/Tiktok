package com.aitd.module_chat.ui.emotion;


import com.aitd.module_chat.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;

public class EmojiAdapter extends BaseQuickAdapter<EmojiBean, BaseViewHolder> {


    public EmojiAdapter(@Nullable List<EmojiBean> data, int index, int pageSize) {
        super(R.layout.imui_chat_emotion_emoji_child,  data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmojiBean item) {
        //判断是否为最后一个item
//        if (item.getId()==0) {
//             helper.setBackgroundRes(R.id.et_emoji,R.mipmap.rc_icon_emoji_delete );
//        } else {
        helper.setText(R.id.et_emoji,item.getUnicodeInt() );
//        }
    }
}
