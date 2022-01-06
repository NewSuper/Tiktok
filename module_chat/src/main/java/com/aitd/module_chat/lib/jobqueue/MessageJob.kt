package com.aitd.module_chat.lib.jobqueue

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import kotlin.jvm.Throws

class MessageJob constructor(private val jobTask: JobTask) : Job(Params(1)) {

    override fun onAdded() {
    }

    @Throws(Throwable::class)
    override fun onRun() {
        //执行工作----启动请求工作计时器
        jobTask.startTimer()
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        jobTask.cancelTimer()
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        jobTask.cancelTimer()
        return RetryConstraint.CANCEL
    }

}