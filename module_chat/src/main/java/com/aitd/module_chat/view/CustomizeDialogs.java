package com.aitd.module_chat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aitd.library_common.utils.ScreenUtil;
import com.aitd.library_common.utils.StringUtil;
import com.aitd.module_chat.R;
import com.blankj.utilcode.util.StringUtils;

public class CustomizeDialogs extends Dialog {
    private Context m_context;

    private LinearLayout m_dialogLayout = null;

    /**
     * 自定义View
     */
    private LinearLayout m_switchView = null;
    private TextView dialogTitleText = null;
    private TextView dialogMsgText = null;

    private View lineView;
    /**
     * 按钮和布局
     */
    private LinearLayout leftButtonLayout, rightButtonLayout;


    private View splitView;
    private LinearLayout bottomLayout;

    private TextView leftButton, rightButton;

    private IDialogsCallBack m_listener = null;

    /**
     * 按钮事件回调
     *
     * @author 李小龙
     */
    public interface IDialogsCallBack {
        void DialogsCallBack(ButtonType buttonType, CustomizeDialogs thisDialogs);
    }

    /**
     * 按钮类型
     *
     * @author 李小龙
     */
    public enum ButtonType {
        leftButton, rightButton,
    }


    public CustomizeDialogs(Context context) {
        super(context, R.style.MyDialogStyleBottom);
        m_context = context;
        initView();
    }

    private void initView() {
        m_dialogLayout = (LinearLayout) LayoutInflater.from(m_context).inflate(R.layout.customize_dialogs, null);
        m_switchView = (LinearLayout) m_dialogLayout.findViewById(R.id.control_customize_dialog_view_switch);
        dialogMsgText = (TextView) m_dialogLayout.findViewById(R.id.control_customize_dialog_text);
        dialogTitleText = (TextView) m_dialogLayout.findViewById(R.id.control_customize_title);

        lineView = m_dialogLayout.findViewById(R.id.line_view);
        leftButtonLayout = (LinearLayout) m_dialogLayout.findViewById(R.id.dialog_left_button_layout);
        rightButtonLayout = (LinearLayout) m_dialogLayout.findViewById(R.id.dialog_right_button_layout);

        splitView = (View) m_dialogLayout.findViewById(R.id.split_view);
        bottomLayout = (LinearLayout) m_dialogLayout.findViewById(R.id.bottom_layout);

        leftButton = (TextView) m_dialogLayout.findViewById(R.id.dialog_left_button_text);
        rightButton = (TextView) m_dialogLayout.findViewById(R.id.dialog_right_button_text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(m_dialogLayout);  //设置默认的加载布局
    }

    /**
     * 设置自定义视图对像
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        setContentView(layoutResID, null);
    }

    /**
     * 设置自定义视图对像
     *
     * @param layoutResID
     * @param params
     */
    public void setContentView(int layoutResID, ViewGroup.LayoutParams params) {
        View view_load = LayoutInflater.from(m_context).inflate(layoutResID, null);
        setContentView(view_load, params);
    }

    /**
     * 设置自定义视图对像
     *
     * @param view   自定义内嵌视图
     * @param params 要设的参数
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (m_switchView == null)
            return;
        if (m_switchView.getChildCount() > 0)
            m_switchView.removeAllViews();
        if (view == null)
            return;
        if (params == null) {
            m_switchView.addView(view);
        } else {
            m_switchView.addView(view, params);
        }
    }

    /**
     * 设置自定义视图对像
     *
     * @param view 自定义内嵌视图
     */
    @Override
    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 设置标题
     *
     * @param titleText
     */
    public void setTitleText(String titleText) {
        if (dialogTitleText != null) {
            if (!StringUtils.isEmpty(titleText)) {
                splitView.setVisibility(View.VISIBLE);
                dialogTitleText.setText(titleText);
            } else {
                splitView.setVisibility(View.GONE);
                dialogTitleText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置右边按钮颜色
     * @param color
     */
    public void setRightBtnTextColor(int color){
        rightButton.setTextColor(color);
    }

    /**
     * 设置左边按钮颜色
     * @param color
     */
    public void setLeftBtnTextColor(int color){
        leftButton.setTextColor(color);
    }


    /**
     * 设置提示内容
     *
     * @param messageID
     */
    public void setMessage(int messageID) {
        setMessage(StringUtil.getResourceStr(m_context,messageID));
    }

    /**
     * 设置提示内容
     *
     * @param messageStr
     */
    public void setMessage(String messageStr) {
        if (dialogMsgText != null) {
            dialogMsgText.setText(messageStr);
        }
    }

    /**
     * 设置左对齐(默认居中对齐)
     */
    public void setMessageAlignCenter() {
        if (m_switchView != null) {
            LinearLayout.LayoutParams lpLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogMsgText.setGravity(Gravity.CENTER);
            m_switchView.setLayoutParams(lpLayoutParams);
        }
    }

    /**
     * 设置按钮文字
     *
     * @param leftButtonText  左边按钮文字
     * @param rightButtonText 右边按钮文字
     */
    public void setButtonText(int leftButtonText, int rightButtonText) {
        if (leftButtonText > 0 && rightButtonText > 0) {
            setButtonText(StringUtil.getResourceStr(m_context,leftButtonText), StringUtil.getResourceStr(m_context,rightButtonText));
        }
    }


    /**
     * 设置按钮文字
     *
     * @param leftButtonText  左边按钮文字
     * @param rightButtonText 右边按钮文字
     */
    public void setButtonText(String leftButtonText, String rightButtonText) {
        if (leftButtonText != null) {
            leftButton.setText(leftButtonText);
        } else {
            leftButtonLayout.setVisibility(View.GONE);
        }
        if (rightButtonText != null) {
            rightButton.setText(rightButtonText);
        } else {
            rightButtonLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 按钮事件绑定
     *
     * @param button_Listener
     */
    public void setButtonProperty(IDialogsCallBack button_Listener) {
        bottomLayout.setVisibility(View.VISIBLE);
        m_listener = button_Listener;
        leftButtonLayout.setOnClickListener(m_buttonOnClick);
        rightButtonLayout.setOnClickListener(m_buttonOnClick);
    }


    /**
     * 单个按钮
     *
     * @param button_Listener
     */
    public void setSingleButton(String buttonText,IDialogsCallBack button_Listener) {
        rightButtonLayout.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);
        splitView.setVisibility(View.VISIBLE);

        bottomLayout.setVisibility(View.VISIBLE);
        leftButton.setText(buttonText);
        m_listener = button_Listener;
        leftButtonLayout.setOnClickListener(m_buttonOnClick);
    }



    /**
     * 按钮事件回调
     */
    private View.OnClickListener m_buttonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (m_listener != null) {
                int id = v.getId();
                if (id == R.id.dialog_left_button_layout) {
                    m_listener.DialogsCallBack(ButtonType.leftButton, CustomizeDialogs.this);
                } else if (id == R.id.dialog_right_button_layout) {
                    m_listener.DialogsCallBack(ButtonType.rightButton, CustomizeDialogs.this);
                }
            }
        }
    };


    /**
     * 显示
     */
    public void show() {
        CustomizeDialogs.super.show();
        setProperty();
    }

    /**
     * 使用默认WINDOW层属性
     */
    public void setProperty() {
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.width = (ScreenUtil.getScreenWidth(m_context) * 3) / 4;
        getWindow().setAttributes(wl);
    }

}
