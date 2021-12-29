package com.thread.pool;

/**
 * 线程分发,统一管理和维护并发线程
 */
public final class ThreadDispatcher {
    public static IDispatcher getDispatcher() {
        return DispatcherImpl.getInstance();
    }

//   //示例  调用代码
//    ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
//        @Override
//        public void run() {
//            doWorkInternal(mHead, param);
//        }
//    });

    //   ThreadDispatcher.getDispatcher().postDelayed(runnable, delay, work.getPriority());
    //   ThreadDispatcher.getDispatcher().postOnMainDelayed(runnable, delay);
}