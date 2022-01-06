package com.aitd.module_chat.lib.jobqueue

import com.aitd.module_chat.utils.EventBusUtil
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint

class UiJob constructor(private val data: Any?)  :  Job(Params(1)) {
    override fun onAdded() {

    }

    override fun onRun() {
        EventBusUtil.post(data)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {

    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        return RetryConstraint.CANCEL
    }

}