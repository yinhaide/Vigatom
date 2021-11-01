package com.tencent.vigatom.callback;

/**
 * 传统的回调
 */
public abstract class CostomCallback<T> {

    /**
     * 成功的回调
     * @param result 成功字段
     */
    public abstract void onResult(T result);
}
