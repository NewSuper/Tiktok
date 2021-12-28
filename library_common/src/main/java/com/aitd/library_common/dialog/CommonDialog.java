package com.aitd.library_common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aitd.library_common.R;

import androidx.core.content.ContextCompat;

/**
 * Author : palmer
 * Date   : 2021/6/20
 * E-Mail : lxlfpeng@163.com
 * Desc   : 通用弹窗封装
 */

public class CommonDialog extends Dialog {

    private TextView titleTextView, contentTextView, leftTextView, rightTextView;
    private LinearLayout bottomLl;
    private View line, line2;
    private View.OnClickListener onLeftClickListener, onRightClickListener;
    private String titleText = "", contentText = "", leftButtonText = "", rightButtonText = "";
    private int leftTextColor, rightTextColor;

    public CommonDialog(Context context) {
        this(context, context.getString(R.string.cancel),context.getString(R.string.sures));
    }

    public CommonDialog(Context context, String leftText, String rightText) {
        super(context, R.style.tipdialog);
        leftButtonText = leftText;
        rightButtonText = rightText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dailog);
        titleTextView = findViewById(R.id.title_text);
        contentTextView = findViewById(R.id.content_text);
        leftTextView = findViewById(R.id.cancel);
        rightTextView = findViewById(R.id.confirm);
        bottomLl = findViewById(R.id.bottom_ll);
        line = findViewById(R.id.line);
        line2 = findViewById(R.id.line2);
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLeftClickListener == null) {
                    dismiss();
                } else {
                    onLeftClickListener.onClick(v);
                }
            }
        });
        leftTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRightClickListener == null) {
                    dismiss();
                } else {
                    onRightClickListener.onClick(v);
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        titleTextView.setText(titleText);
        if (!TextUtils.isEmpty(titleText)) {
            titleTextView.setVisibility(View.VISIBLE);
        }
        contentTextView.setText(contentText);
        leftTextView.setText(leftButtonText);
        rightTextView.setText(rightButtonText);
        if (leftTextColor != 0) {
            leftTextView.setTextColor(ContextCompat.getColor(getContext(), leftTextColor));
        }
        if (rightTextColor != 0) {
            rightTextView.setTextColor(ContextCompat.getColor(getContext(), rightTextColor));
        }
    }

    /**
     * 隐藏取消
     */
    public void goneCancel() {
        contentTextView.setTextColor(Color.parseColor("#333333"));
        contentTextView.setTextSize(16f);
        titleTextView.setVisibility(View.GONE);
        leftTextView.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
    }

    /**
     * 显示取消
     */
    public void visibleRightOnly() {
        leftTextView.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        bottomLl.setVisibility(View.VISIBLE);
    }

    /**
     * 显示确定
     */
    public void visibleLeftOnly() {
        rightTextView.setVisibility(View.VISIBLE);
        line2.setVisibility(View.VISIBLE);
        bottomLl.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题
     */
    public void goneTitle() {
        titleTextView.setVisibility(View.GONE);
    }

    /**
     * 显示标题
     */
    public void visibleTitle() {
        titleTextView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题内容
     */
    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    /**
     * 设置内容
     */
    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    /**
     * 设置关闭文字
     */
    public void setLeftButtonText(String leftButtonText) {
        this.leftButtonText = leftButtonText;
    }

    /**
     * 设置确认文字
     */
    public void setRightButtonText(String rightButtonText) {
        this.rightButtonText = rightButtonText;
    }

    /**
     * 左侧文字颜色
     */
    public void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
    }

    /**
     * 右侧文字颜色
     */
    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
    }

    /**
     * 禁止返回、禁止点击布局外消失
     */
    public void banCancelable() {
        setCancelable(false);
    }

    public void setOnLeftClickListener(View.OnClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
        if (rightTextView != null) {
            rightTextView.setOnClickListener(onLeftClickListener);
        }
    }

    public void setCancelClickListener(View.OnClickListener onClickListener) {
        this.onRightClickListener = onClickListener;
        if (leftTextView != null) {
            leftTextView.setOnClickListener(onClickListener);
        }
    }

}
