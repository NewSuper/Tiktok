package com.thread.pool;

import android.os.Process;

/**
 * 后台线程优先级的
 */
public class BgThreadFactory extends PriorityThreadFactory{
    @Override
    protected int getThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }
}
