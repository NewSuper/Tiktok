package com.aitd.module_main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aitd.library_common.utils.ScreenUtil;
import com.aitd.module_main.R;

import java.util.ArrayList;
import java.util.List;

public class PageIndicatorView extends LinearLayout {
    private Context mContext = null;
    private int margins = 4;//指示器间距
    private List<ImageView>indicatorViews = null;
    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        this.mContext =context;
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        margins = ScreenUtil.dip2px(context,margins);
    }
    //初始化指示器，默认选中第一页
    public void initIndicator(int count){
        if (indicatorViews == null){
            indicatorViews = new ArrayList<>();
        }else {
            indicatorViews.clear();
            removeAllViews();
        }
        ImageView view;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margins,margins,margins,margins);
        for (int i=0;i<count;i++){
            view = new ImageView(mContext);
            view.setImageResource(R.drawable.common_banner_pointer_unselected);
            addView(view,params);
            indicatorViews.add(view);
        }
        if (indicatorViews.size()>0){
            indicatorViews.get(0).setImageResource(R.drawable.common_banner_pointer_selected);
        }
    }
    //设置选中页
    public void setSelectedPage(int selected){
        try{
            for (int i=0;i<indicatorViews.size();i++){
                if (i==selected){
                    indicatorViews.get(i).setImageResource(R.drawable.common_banner_pointer_selected);
                }else {
                    indicatorViews.get(i).setImageResource(R.drawable.common_banner_pointer_unselected);
                }
            }
        }catch (Exception e){

        }
    }
}
