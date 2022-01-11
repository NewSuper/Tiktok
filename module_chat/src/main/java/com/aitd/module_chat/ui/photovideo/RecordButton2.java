package com.aitd.module_chat.ui.photovideo;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aitd.module_chat.R;
import com.aitd.module_chat.lib.boundary.QXConfigManager;
import com.aitd.module_chat.view.LanguageDialog;
import com.aitd.module_chat.view.RecordWaveView;
import com.tencent.aai.AAIClient;
import com.tencent.aai.audio.data.AudioRecordDataSource;
import com.tencent.aai.audio.utils.WavCache;
import com.tencent.aai.auth.AbsCredentialProvider;
import com.tencent.aai.auth.LocalCredentialProvider;
import com.tencent.aai.config.ClientConfiguration;
import com.tencent.aai.exception.ClientException;
import com.tencent.aai.listener.AudioRecognizeResultListener;
import com.tencent.aai.listener.AudioRecognizeStateListener;
import com.tencent.aai.listener.AudioRecognizeTimeoutListener;
import com.tencent.aai.log.AAILogger;
import com.tencent.aai.model.AudioRecognizeRequest;
import com.tencent.aai.model.AudioRecognizeResult;
import com.tencent.aai.model.type.AudioRecognizeConfiguration;
import com.tencent.aai.model.type.AudioRecognizeTemplate;
import com.tencent.aai.model.type.EngineModelType;
import com.tencent.iot.speech.asr.listener.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

public class RecordButton2 extends AppCompatButton implements MessageListener {
    private static final String TAG = "RecordButton2";
    private OnFinishedRecordListener2 finishedListener;
    private int MIN_INTERVAL_TIME = 1000;//最短录音时间
    private int TIPS_INTERVAL_TIME = 50000;//最短录音时间
    private float MAX_INTERVAL_TIME = 1000 * QXConfigManager.getQxFileConfig().getVoiceMessageMaxDuration(TimeUnit.SECONDS);//最长录音时间60m
    private static View view;

    private static long startTime;
    private Dialog recordDialog;

    LinearLayout llFirst;  //第1个总布局
    LinearLayout llRecordFirst;  //开始录音----滑动拖拽按钮
    LinearLayout llWave;//  音频波段
    RecordWaveView ivWave;// 自定义音频波段
    TextView mStateTV;//  10s 倒计时提示
    ImageView ivDelFirst;//  滑动到取消
    ImageView ivWenFirst;//  滑动到文字
    LinearLayout llNotUse;// 隐藏切换语言
    LinearLayout llSecond; //第2个总布局
    LinearLayout llBubble;//气泡点击可修改文字
    LinearLayout llLanguage;//语言切换
    EditText etWordSencond;//  识别到的文字
    ImageView ivDelSecond;// 滑动到取消
    ImageView ivSendVoice;// 发送语音
    ImageView ivSendWord;//发送文字
    TextView tvword;//  未识别到文字

    AbsCredentialProvider credentialProvider;
    int currentRequestId = 0;
    AAIClient aaiClient;
    boolean isSaveAudioRecordFiles = true;

    String apppId = "1304418020";
    String secretId = "AKIDVntgGaJPwDCvNZILQtEI5aAizyfMqf26";
    String secretKey = "4thEUVFiTnreAOMZZIkvpRouiFVVHmRH";

    private static final Logger logger = LoggerFactory.getLogger(RecordButton2.class);
    private final String PERFORMANCE_TAG = "PerformanceTag";

    public RecordButton2(Context context) {
        super(context);
    }

    public RecordButton2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RecordButton2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void initDialogAndStartRecord() {
        startTime = System.currentTimeMillis();
        recordDialog = new Dialog(getContext(), R.style.like_toast_dialog_style);
        view = View.inflate(getContext(), R.layout.imui_dialog_record2, null);
        llFirst = view.findViewById(R.id.llFirst);
        llRecordFirst = view.findViewById(R.id.llRecordFirst);
        mStateTV = view.findViewById(R.id.tvWordFirst);
        ivDelFirst = view.findViewById(R.id.ivDelFirst);
        ivWenFirst = view.findViewById(R.id.ivWenFirst);
        llWave = view.findViewById(R.id.llWave);
        ivWave = view.findViewById(R.id.ivWave);
        tvword = view.findViewById(R.id.tvword);

        llNotUse = view.findViewById(R.id.llNotUse);
        llSecond = view.findViewById(R.id.llSecond);
        llBubble = view.findViewById(R.id.llBubble);
        llLanguage = view.findViewById(R.id.llLanguage);
        etWordSencond = view.findViewById(R.id.etWordSencond);
        ivDelSecond = view.findViewById(R.id.ivDelSecond);
        ivSendVoice = view.findViewById(R.id.ivSendVoice);
        ivSendWord = view.findViewById(R.id.ivSendWord);

        recordDialog.setContentView(view, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        recordDialog.setOnDismissListener(onDismiss);
        WindowManager.LayoutParams lp = recordDialog.getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (getContext() instanceof Activity) {
                Activity activity = (Activity) getContext();
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }
        } else {
            init(1);
            recordDialog.show();
        }

        llLanguage.setOnClickListener(v -> showLanguageDialog());
        llBubble.setOnClickListener(v -> llNotUse.setVisibility(GONE));
        ivDelFirst.setOnClickListener(v -> cancelRecord());
        ivDelSecond.setOnClickListener(v -> {
            isDirectSendVoice = false;
            cancelRecord();
        });
        ivSendWord.setOnClickListener(v -> {
            if (finishedListener != null && !TextUtils.isEmpty(etWordSencond.getText().toString())) {
                finishedListener.onFinishedRecord2(null, 0, etWordSencond.getText().toString(), 2);
            }
            isDirectSendVoice = false;
            cancelRecord();
        });
        ivSendVoice.setOnClickListener(v -> {
            startCallBackAndSendVoice();
            cancelRecord();
        });
        //处理有时滑动未成功的兼容方法，再执行一遍  handler 1004
        ivWenFirst.setOnClickListener(v -> {
            llRecordFirst.setBackgroundResource(R.mipmap.voice_half_bg2);
            NotAndYesForUI();
        });
    }

    String wavPath = null;
    volatile boolean isDirectSendVoice = false;//录制结束后，是否直接发送语音出去
    private DialogInterface.OnDismissListener onDismiss = dialog -> stopRecording();

    public interface OnFinishedRecordListener2 {
        void onFinishedRecord2(String audioPath, int time, String word, int type);
    }

    private void stopRecording() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        if (mCountDownTimer2 != null) {
            mCountDownTimer2.cancel();
            mCountDownTimer2 = null;
        }
        if (aaiClient != null) {
            aaiClient.release();
        }
        if (recordDialog != null) {
            if (recordDialog.isShowing()) {
                recordDialog.dismiss();
                recordDialog = null;
            }
        }
    }

    @Override
    public void onMessage(String s) {
        myHandler.sendEmptyMessage(1001);
        AAILogger.info(logger, "onMessage-----send---1001-.");
    }


    private void showLanguageDialog() {
        LanguageDialog languageDialog = new LanguageDialog(this.getContext());
        languageDialog.setOnButtonClickListener(new LanguageDialog.OnButtonClickListener() {
            @Override
            public void chinese() {
                cancelAudioRecognize();
                Message message = Message.obtain();
                message.what = 1006;
                myHandler.sendMessageDelayed(message, 1000);
            }

            @Override
            public void english() {
                cancelAudioRecognize();
                Message message = Message.obtain();
                message.what = 1007;
                myHandler.sendMessageDelayed(message, 1000);
            }
        });
        languageDialog.show();
    }

    float posX;
    float posY;
    float curPosX;
    float curPosY;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        y = event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //单点触摸按下动作
                posX = event.getX();
                posY = event.getY();

                initDialogAndStartRecord();
                myHandler = new MyHandler();
                myRunnable = new MyRunnable();
                myHandler.postDelayed(myRunnable, 0);
                AAILogger.info(logger, "onTouchEvent: -------ACTION_DOWN----单点触摸按下动作---");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                myHandler.removeCallbacks(myRunnable);
                long time = System.currentTimeMillis() - startTime;
                Log.e(TAG, "onTouchEvent: --------------->>>>" + time);
                if (y >= 0 && (time < MAX_INTERVAL_TIME)) {
                    finishRecord();
                    Log.e(TAG, "onTouchEvent: ------cancelRecord---11111------>>>>");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isDirectSendVoice = false;
                //触摸点移动动作
                curPosX = event.getX();
                curPosY = event.getY();
                if ((curPosX - posX > 0) && (Math.abs(curPosX - posX) > 25)
                        && ((curPosY - posY < 0) && (Math.abs(curPosY - posY) > 25))) {
                    AAILogger.info(logger, "AAAAAAAAAAAAAAA向右滑动-----向上滑动");
                    Log.e(TAG, "onTouchEvent: AAAAAAAAAAAAAAA向右滑动-----向上滑动");
                    myHandler.sendEmptyMessage(1004);
                }
                if ((curPosX - posX < 0) && (Math.abs(curPosX - posX) > 25) && ((curPosY - posY < 0) && (Math.abs(curPosY - posY) > 25))) {
                    AAILogger.info(logger, "AAAAAAAAAAAAAA向左滑动-----向上滑动");
                    Log.e(TAG, "onTouchEvent: AAAAAAAAAAAAAA向左滑动-----向上滑动");
                    myHandler.sendEmptyMessage(1005);
                }
                break;
        }
        return true;
    }

    private MyHandler myHandler;
    private MyRunnable myRunnable;
    private float y;

    public void setOnFinishedRecordListener2(OnFinishedRecordListener2 listener) {
        finishedListener = listener;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:  //如果未能成功解析时，展示的UI 界面
                    llFirst.setVisibility(GONE);
                    llSecond.setVisibility(VISIBLE);
                    llNotUse.setVisibility(GONE);
                    etWordSencond.setVisibility(GONE);
                    tvword.setVisibility(VISIBLE);
                    tvword.setText(R.string.voice_not_use);
                    ivSendWord.setBackgroundResource(R.mipmap.voice_up_gray);
                    ivSendWord.setOnClickListener(null);
                    break;
                case 1002: //解析成功，展示第2个布局，发送文字按钮更换
                    words = msg.obj.toString();
                    Log.e(TAG, "AAAAAA---run:--------words---->>>----- " + words);
                    etWordSencond.setText(words);
                    break;
                case 1003:
                    int value = (int) msg.obj;
                    ivWave.setAmpListener(() -> value);
                    break;
                case 1004:
                    ivWenFirst.setImageResource(R.mipmap.voice_blue_wen);
                    llRecordFirst.setBackgroundResource(R.mipmap.voice_half_bg2);
                    NotAndYesForUI();
                    break;
                case 1005:
                    ivDelFirst.setImageResource(R.mipmap.voice_blue_del);

                    mCountDownTimer2 = new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            cancelRecord();
                        }
                    }.start();
                    break;
                case 1006:
                    init(1);
                    AAILogger.info(logger, "BBBBBB--------689行---- init(1);-----");
                    break;
                case 1007:
                    init(2);
                    AAILogger.info(logger, "BBBBBB--------689行---- init(2);-----");
                    break;
                case 1008://开始将音频路径回调并发送
                    startCallBackAndSendVoice();
                    break;
            }
        }
    }

    //识别出文字  《------》  未识别出文字
    private void NotAndYesForUI(){
        if (!TextUtils.isEmpty(words)) {
            stopAudioRecognize();
            llFirst.setVisibility(GONE);
            llSecond.setVisibility(VISIBLE);
            etWordSencond.setText(words);
            ivSendWord.setBackgroundResource(R.mipmap.voice_up_blue);
        }else {
            llFirst.setVisibility(GONE);
            llSecond.setVisibility(VISIBLE);
            llNotUse.setVisibility(GONE);
            etWordSencond.setVisibility(GONE);
            tvword.setVisibility(VISIBLE);
            tvword.setText(R.string.voice_not_use);
            ivSendWord.setBackgroundResource(R.mipmap.voice_up_gray);
            ivSendWord.setOnClickListener(null);
        }
    }
    String words = null;

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            long time = System.currentTimeMillis() - startTime;
            Log.e(TAG, "run:--------time---->>>----- " + time);
            if (time > TIPS_INTERVAL_TIME) {
                startCountDownTimer();     //已经录音50s时，启动 10s 倒计时
            }

            if (time > MAX_INTERVAL_TIME) {
                isDirectSendVoice = true;
                finishRecord();           //录音达到60s 后，停止录音,页面destory
                myHandler.removeCallbacks(myRunnable);
            } else {
                myHandler.postDelayed(myRunnable, 200);
            }
        }
    }

    private CountDownTimer mCountDownTimer;
    private CountDownTimer mCountDownTimer2;

    private void startCountDownTimer() {
        mCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                llWave.setVisibility(GONE);
                mStateTV.setVisibility(VISIBLE);
                mStateTV.setText(String.format(getResources().getString(R.string.voice_timer), millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                cancelRecord();
            }
        }.start();
    }

    //放开手指，结束录音处理
    private void finishRecord() {
        AAILogger.info(logger, "finishRecord--结束录音---.");

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast toast = Toast.makeText(getContext(), R.string.qx_record_time_short, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            isDirectSendVoice = false;
            AAILogger.info(logger, "finishRecord-----send---1001-.");
            cancelRecord();
            Log.e("语音发送", "finishRecord: **************************  取消语音发送  *************************");
            return;
        } else {
            isDirectSendVoice = true;
            stopAudioRecognize();
            Log.e("语音发送", "finishRecord: **************************  停止语音发送  *************************");
            stopRecording();
        }
    }

    private void startCallBackAndSendVoice(){
        // new  add---start-----默认松开，就直接发送原语音
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(wavPath);
            player.prepare();
            if (finishedListener != null && !TextUtils.isEmpty(wavPath)) {
                finishedListener.onFinishedRecord2(wavPath, player.getDuration() / 1000, null, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // new  add---end-----默认松开，就直接发送原语音
//        cancelRecord();
    }

    private void stopAudioRecognize() {
        new Thread(() -> {
            boolean taskExist = false;
            if (aaiClient != null) {
                taskExist = aaiClient.stopAudioRecognize(currentRequestId);
                AAILogger.info(logger, "AAAAAA---stopAudioRecognize---currentRequestId-->>>" + currentRequestId + "---taskExist-->>>" + taskExist);
            }
            if (!taskExist) {
                AAILogger.info(logger, "stop button is clicked..识别状态：不存在该任务，无法停止");
            }
        }).start();
    }

    private void cancelAudioRecognize() {
        new Thread(() -> {
            boolean taskExist = false;
            if (aaiClient != null) {
                taskExist = aaiClient.cancelAudioRecognize(currentRequestId);
                AAILogger.info(logger, "BBBBBB--------689行------cancelAudioRecognize---currentRequestId-->>>" + currentRequestId + "---taskExist-->>>" + taskExist);
            }
            if (!taskExist) {
                AAILogger.info(logger, "cancel button is clicked..识别状态：不存在该任务，无法取消");
            }
        }).start();
    }

    //取消录音对话框和停止录音
    private void cancelRecord() {
        AAILogger.info(logger, "cancelRecord---取消录音对话框和停止录音---.");
        cancelAudioRecognize();
        stopRecording();
    }

    LinkedHashMap<String, String> resMap = new LinkedHashMap<>();

    private String buildMessage(Map<String, String> msg) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter = msg.entrySet().iterator();
        while (iter.hasNext()) {
            String value = iter.next().getValue();
            stringBuffer.append(value + "\r\n");
        }
        return stringBuffer.toString();
    }

    private void init(int type) {
        credentialProvider = new LocalCredentialProvider(secretKey);
        ClientConfiguration.setMaxAudioRecognizeConcurrentNumber(1); // 语音识别的请求的最大并发数        // 用户配置
        ClientConfiguration.setMaxRecognizeSliceConcurrentNumber(1); // 单个请求的分片最大并发数
        final AudioRecognizeResultListener audioRecognizeResultlistener = new AudioRecognizeResultListener() {

            boolean dontHaveResult = true;

            /**
             * 返回分片的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             * @param seq 该分片所在语音流的序号 (0, 1, 2...)
             */
            @Override
            public void onSliceSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {
                if (dontHaveResult && !TextUtils.isEmpty(result.getText())) {
                    dontHaveResult = false;
                    Date date = new Date();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    String time = format.format(date);
                    String message = String.format("voice flow order = %d, receive first response in %s, result is = %s", seq, time, result.getText());
                    Log.i(PERFORMANCE_TAG, message);
                }
                AAILogger.info(logger, "分片slice seq = {}, voiceid = {}, result = {},startTime = {},endTime = {}", seq, result.getVoiceId(), result.getText(), result.getStartTime(), result.getEndTime());
                resMap.put(String.valueOf(seq), result.getText());
                final String msg = buildMessage(resMap);
                AAILogger.info(logger, "分片slice msg=" + msg);

                Message message = Message.obtain();
                message.what = 1002;
                message.obj = msg;
                myHandler.sendMessage(message);
            }

            /**
             * 返回语音流的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             * @param seq 该语音流的序号 (1, 2, 3...)
             */
            @Override
            public void onSegmentSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {
                dontHaveResult = true;
                AAILogger.info(logger, "语音流segment seq = {}, voiceid = {}, result = {},startTime = {},endTime = {}", seq, result.getVoiceId(), result.getText(), result.getStartTime(), result.getEndTime());
                resMap.put(String.valueOf(seq), result.getText());
                final String msg = buildMessage(resMap);
                AAILogger.info(logger, "语音流segment msg=" + msg);
                Message message = Message.obtain();
                message.what = 1002;
                message.obj = msg;
                myHandler.sendMessage(message);
            }

            /**
             * 识别结束回调，返回所有的识别结果
             * @param request 相应的请求
             * @param result 识别结果
             */
            @Override
            public void onSuccess(AudioRecognizeRequest request, String result) {
                AAILogger.info(logger, "识别结束, result = {}", result);

                Message message = Message.obtain();
                message.what = 1002;
                message.obj = result;
                myHandler.sendMessage(message);
            }

            /**
             * 识别失败
             * @param request 相应的请求
             * @param clientException 客户端异常
             * @param serverException 服务端异常
             */
            @Override
            public void onFailure(AudioRecognizeRequest request, ClientException clientException, com.tencent.aai.exception.ServerException serverException) {
                if (clientException != null) {
                    AAILogger.info(logger, "onFailure.识别状态：失败.clientException:" + clientException.toString());
                }
                if (serverException != null) {
                    AAILogger.info(logger, "onFailure..clientException:serverException" + serverException.toString());
                }
            }
        };
        // 识别结果回调监听器
        final AudioRecognizeStateListener audioRecognizeStateListener = new AudioRecognizeStateListener() {
            DataOutputStream dataOutputStream;
            String fileName = null;
            String filePath = null;
            ExecutorService mExecutorService;

            /**
             * 开始录音
             * @param request
             */
            @Override
            public void onStartRecord(AudioRecognizeRequest request) {
                currentRequestId = request.getRequestId();
                AAILogger.info(logger, "onStartRecord..");
                //为本次录音创建缓存一个文件
                if (isSaveAudioRecordFiles) {
                    if (mExecutorService == null) {
                        mExecutorService = Executors.newSingleThreadExecutor();
                    }
                    filePath = getContext().getFilesDir() + "/" + "voice_";
                    fileName = System.currentTimeMillis() + ".pcm";
                    dataOutputStream = WavCache.creatPmcFileByPath(filePath, fileName);
                }
            }

            /**
             * 结束录音
             * @param request
             */
            @Override
            public void onStopRecord(AudioRecognizeRequest request) {
                AAILogger.info(logger, "onStopRecord..");
                Log.e("语音发送", "onStopRecord: ======================结束录音》》:" + isSaveAudioRecordFiles + "  isDirectSendVoice：" + isDirectSendVoice);
                if (isSaveAudioRecordFiles) {
                    mExecutorService.execute(() -> {
                        WavCache.closeDataOutputStream(dataOutputStream);
                        WavCache.makePCMFileToWAVFile(filePath, fileName);

                        String waveName = fileName.replace(".pcm", ".wav");
                        wavPath = filePath + "/" + waveName;
                        Log.e("语音发送", "onStopRecord: 音频路径为：" + wavPath);
                        if (isDirectSendVoice) {
                            myHandler.sendEmptyMessage(1008);
                        }
                    });
                }
            }

            /**
             * 返回音频流，
             * 用于返回宿主层做录音缓存业务。
             * 由于方法跑在sdk线程上，这里多用于文件操作，宿主需要新开一条线程专门用于实现业务逻辑
             * @param audioDatas
             */
            @Override
            public void onNextAudioData(final short[] audioDatas, final int readBufferLength) {
                if (isSaveAudioRecordFiles) {
                    mExecutorService.execute(() -> WavCache.savePcmData(dataOutputStream, audioDatas, readBufferLength));
                }
            }

            /**
             * 第seq个语音流开始识别
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowStartRecognize(AudioRecognizeRequest request, int seq) {
                AAILogger.info(logger, "onVoiceFlowStartRecognize.. seq = {}", seq);
            }

            /**
             * 第seq个语音流结束识别
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowFinishRecognize(AudioRecognizeRequest request, int seq) {
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time = format.format(date);
                String message = String.format("voice flow order = %d, recognize finish in %s", seq, time);
                Log.i(PERFORMANCE_TAG, message);
            }

            /**
             * 第seq个语音流开始
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowStart(AudioRecognizeRequest request, int seq) {
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time = format.format(date);
                String message = String.format("voice flow order = %d, start in %s", seq, time);
                Log.i(PERFORMANCE_TAG, message);
            }

            /**
             * 第seq个语音流结束
             * @param request
             * @param seq
             */
            @Override
            public void onVoiceFlowFinish(AudioRecognizeRequest request, int seq) {
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                String time = format.format(date);
                String message = String.format("voice flow order = %d, stop in %s", seq, time);
                Log.i(PERFORMANCE_TAG, message);
            }

            /**
             * 语音音量回调
             * @param request
             * @param volume
             */
            @Override
            public void onVoiceVolume(AudioRecognizeRequest request, final int volume) {
                AAILogger.info(logger, "onVoiceVolume..--->" + volume);
                Message message = Message.obtain();
                message.what = 1003;
                message.obj = volume;
                myHandler.sendMessage(message);
            }
        };
        //识别超时监听器
        final AudioRecognizeTimeoutListener audioRecognizeTimeoutListener = new AudioRecognizeTimeoutListener() {

            @Override
            public void onFirstVoiceFlowTimeout(AudioRecognizeRequest request) {   //检测第一个语音流超时
            }

            @Override
            public void onNextVoiceFlowTimeout(AudioRecognizeRequest request) {//检测下一个语音流超时
            }
        };
        try {
            aaiClient = new AAIClient(this.getContext(), Integer.parseInt(apppId), 0, secretId, secretKey, credentialProvider);
            AAILogger.info(logger, "BBBBBB--------689行------aaiClient = new AAIClient--------");
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (aaiClient != null) {
            boolean taskExist = aaiClient.cancelAudioRecognize(currentRequestId);
            AAILogger.info(logger, "taskExist=" + taskExist);
        }
        resMap.clear();
        AudioRecognizeRequest.Builder builder = new AudioRecognizeRequest.Builder();

        final AudioRecognizeConfiguration audioRecognizeConfiguration = new AudioRecognizeConfiguration.Builder()
                .setSilentDetectTimeOut(true)// 是否使能静音检测，true表示不检查静音部分
                .audioFlowSilenceTimeOut(5000) // 静音检测超时停止录音
                .minAudioFlowSilenceTime(2000) // 语音流识别时的间隔时间
                .minVolumeCallbackTime(80) // 音量回调时间
                .build();
        if (type == 1) {
            AudioRecognizeRequest audioRecognizeRequest = builder
                    .pcmAudioDataSource(new AudioRecordDataSource(true)) // 设置数据源
                    .template(new AudioRecognizeTemplate(EngineModelType.EngineModelType16K.getType(), 0)) // 设置自定义模板
                    .setFilterDirty(0)  // 0 ：默认状态 不过滤脏话 1：过滤脏话
                    .setFilterModal(0) // 0 ：默认状态 不过滤语气词  1：过滤部分语气词 2:严格过滤
                    .setFilterPunc(0) // 0 ：默认状态 不过滤句末的句号 1：滤句末的句号
                    .setConvert_num_mode(1) //1：默认状态 根据场景智能转换为阿拉伯数字；0：全部转为中文数字。
                    .setNeedvad(1) //0：关闭 vad，1：默认状态 开启 vad。
                    .build();
            new Thread(() -> aaiClient.startAudioRecognize(audioRecognizeRequest, audioRecognizeResultlistener,
                    audioRecognizeStateListener, audioRecognizeTimeoutListener, audioRecognizeConfiguration)).start();
        } else if (type == 2) {
            AudioRecognizeRequest audioRecognizeRequest = builder
                    .pcmAudioDataSource(new AudioRecordDataSource(true)) // 设置数据源
                    .template(new AudioRecognizeTemplate(EngineModelType.EngineModelType16KEN.getType(), 0)) // 设置自定义模板
                    .setFilterDirty(0)  // 0 ：默认状态 不过滤脏话 1：过滤脏话
                    .setFilterModal(0) // 0 ：默认状态 不过滤语气词  1：过滤部分语气词 2:严格过滤
                    .setFilterPunc(0) // 0 ：默认状态 不过滤句末的句号 1：滤句末的句号
                    .setConvert_num_mode(1) //1：默认状态 根据场景智能转换为阿拉伯数字；0：全部转为中文数字。
                    .setNeedvad(1) //0：关闭 vad，1：默认状态 开启 vad。
                    .build();
            new Thread(() -> aaiClient.startAudioRecognize(audioRecognizeRequest, audioRecognizeResultlistener,
                    audioRecognizeStateListener, audioRecognizeTimeoutListener, audioRecognizeConfiguration)).start();
        }
    }
}
