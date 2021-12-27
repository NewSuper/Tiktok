package com.aitd.module_main.widget.bottombar;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class BottomBarLayout extends LinearLayout implements View.OnClickListener {
    private List<BottomBarItem> mTabViews;
    private List<Tab> mTabs;
    private OnTabCheckListener mOnTabCheckListener;
    private int mCurrentItem = -1;//当前条目的索引

    public void setOnTabCheckListener(OnTabCheckListener onTabCheckListener) {
        mOnTabCheckListener = onTabCheckListener;
    }

    public BottomBarLayout(Context context) {
        super(context);
        init();
    }

    public BottomBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        mTabViews = new ArrayList<>();
        mTabs = new ArrayList<>();
    }

    /**
     * 添加Tab
     *
     * @param tab
     */
    public void addTab(Tab tab) {
        BottomBarItem bottomBarItem = new BottomBarItem(getContext());
        loadTab(tab, bottomBarItem);
    }

    public void addTab(Tab tab, int tabLayoutRes) {
        BottomBarItem bottomBarItem = (BottomBarItem) LayoutInflater.from(getContext()).inflate(tabLayoutRes, null);
        loadTab(tab, bottomBarItem);
    }

    private void loadTab(Tab tab, BottomBarItem bottomBarItem) {
        bottomBarItem.getImageView().setImageResource(tab.mIconNormalResId);
        bottomBarItem.getTextView().setText(tab.mText);
        bottomBarItem.getTextView().setTextColor(tab.mNormalColor);
        bottomBarItem.setTag(mTabViews.size());
        bottomBarItem.setOnClickListener(this);

        mTabViews.add(bottomBarItem);
        mTabs.add(tab);
        addView(bottomBarItem);
    }


    /**
     * 设置选中Tab
     *
     * @param position
     */
    public void setCurrentItem(int position) {
        if (position >= mTabs.size() || position < 0) {
            position = 0;
        }
        mTabViews.get(position).performClick();
        updateState(position);
    }

    /**
     * 更新状态
     *
     * @param position
     */
    private void updateState(int position) {
        for (int i = 0; i < mTabViews.size(); i++) {
            BottomBarItem view = mTabViews.get(i);
            TextView textView = view.getTextView();
            ImageView imageView = view.getImageView();
            if (i == position) {
                imageView.setImageResource(mTabs.get(i).mIconPressedResId);
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                if (!animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                textView.setTextColor(mTabs.get(i).mSelectColor);
            } else {
                imageView.setImageResource(mTabs.get(i).mIconNormalResId);
                textView.setTextColor(mTabs.get(i).mNormalColor);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mCurrentItem == (int) v.getTag()) {
            return;
        }
        mCurrentItem = (int) v.getTag();
        if (mOnTabCheckListener != null) {
            mOnTabCheckListener.onTabSelected((BottomBarItem) v, mCurrentItem);
        }
        updateState(mCurrentItem);
    }

    public interface OnTabCheckListener {
        public void onTabSelected(BottomBarItem bottomBarItem, int position);
    }

    /**
     * 设置未读数
     *
     * @param position  底部标签的下标
     * @param unreadNum 未读数
     */
    public void setUnread(int position, int unreadNum) {
        mTabViews.get(position).setUnreadNum(unreadNum);
    }

    /**
     * 设置提示消息
     *
     * @param position 底部标签的下标
     * @param msg      未读数
     */
    public void setMsg(int position, String msg) {
        mTabViews.get(position).setMsg(msg);
    }

    /**
     * 隐藏提示消息
     *
     * @param position 底部标签的下标
     */
    public void hideMsg(int position) {
        mTabViews.get(position).hideMsg();
    }

    /**
     * 显示提示的小红点
     *
     * @param position 底部标签的下标
     */
    public void showNotify(int position) {
        mTabViews.get(position).showNotify();
    }

    /**
     * 隐藏提示的小红点
     *
     * @param position 底部标签的下标
     */
    public void hideNotify(int position) {
        mTabViews.get(position).hideNotify();
    }

    public BottomBarItem getBottomItem(int position) {
        return mTabViews.get(position);
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public static class Tab {
        private int mIconNormalResId;
        private int mIconPressedResId;
        private int mNormalColor;
        private int mSelectColor;
        private String mText;


        public Tab setText(String text) {
            mText = text;
            return this;
        }

        public Tab setNormalIcon(int res) {
            mIconNormalResId = res;
            return this;
        }

        public Tab setPressedIcon(int res) {
            mIconPressedResId = res;
            return this;
        }

        public Tab setColor(int color) {
            mNormalColor = color;
            return this;
        }

        public Tab setCheckedColor(int color) {
            mSelectColor = color;
            return this;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabViews != null) {
            mTabViews.clear();
        }
        if (mTabs != null) {
            mTabs.clear();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < mTabViews.size(); i++) {
            View view = mTabViews.get(i);
            int width = getResources().getDisplayMetrics().widthPixels / (mTabs.size());
            LayoutParams params = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);

            view.setLayoutParams(params);
        }

    }
}
