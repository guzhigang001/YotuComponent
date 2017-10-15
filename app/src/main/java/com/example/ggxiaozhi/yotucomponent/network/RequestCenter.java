package com.example.ggxiaozhi.yotucomponent.network;

import com.example.ggxiaozhi.minesdk.okhttp3.CommonOkHttpClient;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataHandle;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.minesdk.okhttp3.request.CommonRequest;
import com.example.ggxiaozhi.minesdk.okhttp3.request.RequestParams;
import com.example.ggxiaozhi.yotucomponent.module.BaseRecommandModel;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.network
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/11
 * 功能   ：整个应用发送所的网络请求类
 */

public class RequestCenter {

    /**
     * 根据参数发送所有请求 get,post请类型是由Request决定
     *
     * @param url      数据请求地址
     * @param params   请求参数
     * @param listener 数据返回的监听
     * @param clazz    实体类
     */
    public static void getData(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.sendRequest(CommonRequest.
                createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }

    public static void requestRecommandData(DisposeDataListener listener) {
        getData(HttpConstants.HOME_RECOMMAND, null, listener, BaseRecommandModel.class);
    }
}
