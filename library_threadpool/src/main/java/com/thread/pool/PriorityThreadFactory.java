package com.thread.pool;

import android.os.Process;

import java.util.concurrent.ThreadFactory;
import static com.thread.pool.LogUtil.logE;
public abstract class PriorityThreadFactory  implements ThreadFactory {
    private static final String TAG = "PriorityThreadFactory";

    /**
     * 返回线程优先级，该优先级将设置给每次需要实例化的thread
     * @return
     */
    abstract protected int getThreadPriority();

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable){
            @Override
            public void run() {
                final int expectPriority = getThreadPriority();
                Process.setThreadPriority(expectPriority);
                super.run();
                final int curPriority = Process.getThreadPriority(Process.myTid());
                if (curPriority != expectPriority){
                    logE(TAG, "[ expectPriority: %d, curPriority: %d ] Thread priority is changed, be careful don't change the thread priority in #run() !", expectPriority, curPriority);
                }
            }
        };
    }
}
