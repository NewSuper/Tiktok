package com.thread.pool.http;

public interface BaseCallback<T> {
    void onStart();

    void onSuccess(Response<T> response);

    void onError(Response<T> response);

    void onFinish();
}
