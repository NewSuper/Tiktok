package com.aitd.module_chat.netty;


import com.aitd.module_chat.pojo.event.HeartBeatEvent;
import com.aitd.module_chat.utils.qlog.QLog;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatHolder {

    public static HeartBeatHolder getInstance() {
        return Holder.instance;
    }

    /**
     * 超出指定时间无tcp通信则认定需要发送心跳包
     * TODO 先按60s检测一次，后续再优化
     */
   // public final long EXP_TIME = 60000;
    public final long EXP_TIME = 20000;  //修改心跳时长
    /**
     * 记录tcp通信的最新一次时间
     */
    private long latestTime;

    private boolean isSending;

    static final int INTERVAL_CHECK = 1000;
    Timer timer;


    Timer replenishTimer;

    public void updateLatestTime() {
        latestTime = System.currentTimeMillis();
    }

    public boolean isNeedSendHeatBeat() {
        long idleTime = System.currentTimeMillis() - latestTime;
        boolean value = !isSending && idleTime > EXP_TIME;
        //LogUtil.info(HeartBeatHolder.class, "isNeedSendHeatBeat value="+value);
        return value;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    static class Holder {
        static final HeartBeatHolder instance = new HeartBeatHolder();
    }

    public void startHeartBeatService() {
//        Context context = GlobalContextManager.getInstance().getContext();
//        Intent intent = new Intent(context, HeartBeatService.class);
//        if (Build.VERSION.SDK_INT >=  android.os.Build.VERSION_CODES.O) {
//            context.startForegroundService(intent);
//        } else {
//            context.startService(intent);
//        }
//        context.startService(new Intent(context, HeartBeatService.class));
        startHeartBeat();
    }

    public void stopHeartBeatService() {
//        Context context = GlobalContextManager.getInstance().getContext();
//        context.stopService(new Intent(context, HeartBeatService.class));
        stopHeartBeat();
    }

    private synchronized void startHeartBeat() {
        if (timer == null) {
            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    //发送心跳检测
                    EventBus.getDefault().post(HeartBeatEvent.EVENT_CHECK_NORMALLY);
                    //       LogUtil.info(HeartBeatHolder.class, "发送心跳检测事件");
                }
            };
            timer.schedule(task,0, INTERVAL_CHECK);
        }
    }

    private synchronized void stopHeartBeat() {
        if(timer!=null) {
            timer.cancel();
            timer = null;
            QLog.i("HeartBeatHolder",  "取消心跳检测定时器");
        }
    }

    public synchronized void replenishPing(boolean isForeground) {
        if (isForeground) {
            startReplenishHeartBeat();
        } else {
            stopReplenishHeartBeat();
        }
    }

    public synchronized void startReplenishHeartBeat() {
        if (replenishTimer == null) {
            replenishTimer = new Timer();
            TimerTask task  = new TimerTask() {
                @Override
                public void run() {
                    QLog.i("HeartBeatHolder",  "发送补充心跳");
                    EventBus.getDefault().post(HeartBeatEvent.EVENT_CHECK_IMMEDIATELY);
                }
            };
            this.replenishTimer.schedule(task,2000L,15000L);
        }
    }

    public synchronized void stopReplenishHeartBeat() {
        if (replenishTimer != null) {
            QLog.i("HeartBeatHolder",  "结束补充心跳");
            replenishTimer.cancel();
            replenishTimer = null;
        }
    }


}
