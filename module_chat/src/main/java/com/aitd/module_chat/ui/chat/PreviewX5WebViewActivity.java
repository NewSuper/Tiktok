package com.aitd.module_chat.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.RelativeLayout;

import com.aitd.module_chat.R;
import com.aitd.module_chat.lib.boundary.FileSizeUtil;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class PreviewX5WebViewActivity extends AppCompatActivity {

    private File mFile;
    private RelativeLayout mFlRoot;
    private TbsReaderView mTbsReaderView;

    public static void start(Context context, String filePath) {
        Intent starter = new Intent(context, PreviewX5WebViewActivity.class);
        starter.putExtra("filePath", filePath);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imui_activity_tbs_webview);
        mFlRoot = findViewById(R.id.fl_container);
        String filePath = getIntent().getStringExtra("filePath");
        mFile = new File(filePath);
        addTbsReaderView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rot = getWindowManager()
                .getDefaultDisplay()
                .getRotation();
        Log.d("TAG", "onConfigurationChanged : " + newConfig + ", rot : " + rot);
        if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270) {
            mFlRoot.post(() -> {
                int height = mFlRoot.getHeight();
                int width = mFlRoot.getWidth();
                mTbsReaderView.onSizeChanged(width, height);
            });
        } else if (rot == Surface.ROTATION_0) {
            mFlRoot.post(() -> {
                int height = mFlRoot.getHeight();
                int width = mFlRoot.getWidth();
                mTbsReaderView.onSizeChanged(width, height);
            });
        }
    }

    @Override
    public void onDestroy() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
        super.onDestroy();
    }

    private void addTbsReaderView() {
        mTbsReaderView = new TbsReaderView(this, readerCallback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //不使用黑暗模式
            mTbsReaderView.setForceDarkAllowed(false);
        }
        mFlRoot.addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        String extensionName = FileSizeUtil.getFileType(mFile.getPath());
        Bundle bundle = new Bundle();
        bundle.putString(TbsReaderView.KEY_FILE_PATH, mFile.getPath());
        bundle.putString(TbsReaderView.KEY_TEMP_PATH, FileSizeUtil.createCachePath(this));
        boolean result = mTbsReaderView.preOpen(extensionName, false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private TbsReaderView.ReaderCallback readerCallback = new TbsReaderView.ReaderCallback() {
        @Override
        public void onCallBackAction(
                Integer integer, Object o, Object o1) {
        }
    };
}
