package com.example.ggxiaozhi.minesdk.okhttp3.listener;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.okhttp3.listener
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：网络请求结果的自定义监听接口
 */

public interface DisposeDataListener {

    /**
     * 请求成功回调事件
     */
    void onSuccess(Object responseObj);
    /**
     * 请求失败回调时间
     */
    void onError(Object errorObj);
}
