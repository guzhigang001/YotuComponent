package com.example.ggxiaozhi.minesdk.okhttp3.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.okhttp3.request
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：封装Okhttp3 Request对象
 */

public class CommonRequest {
    /**
     * @param url
     * @param params
     * @return 返回一个创建好的Post请求的Request
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params!=null){
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //请求参数集合遍历 添加到我们的请求构建类中
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        //添加请求头
        Headers.Builder mHeaderBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeaderBuild.add(entry.getKey(), entry.getValue());
            }
        }
        //通过请求构建类的build方法获取到真正的请求体对象
        FormBody formBody = builder.build();
        Headers mHeader = mHeaderBuild.build();
        return new Request.Builder().url(url).post(formBody).headers(mHeader).build();
    }

    /**
     *
     * @param url
     * @param params
     * @return 返回一个创建好的Get请求的Request
     */
    public static Request createGetRequestHeaders(String url, RequestParams params,RequestParams headers) {
        StringBuilder builder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        //添加请求头
        Headers.Builder mHeaderBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeaderBuild.add(entry.getKey(), entry.getValue());
            }
        }
        Headers mHeader = mHeaderBuild.build();
        return new Request.Builder().url(builder.substring(0, builder.length() - 1)).headers(mHeader).get().build();
    }
    /**
     * ressemble the params to the url
     *
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params) {

        return createGetRequestHeaders(url, params, null);
    }
    /**
     * @param url
     * @param params
     * @return
     */
    public static Request createMonitorRequest(String url, RequestParams params) {
        StringBuilder urlBuilder = new StringBuilder(url).append("&");
        if (params != null && params.hasParams()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return new Request.Builder().url(urlBuilder.substring(0, urlBuilder.length() - 1)).get().build();
    }
}
