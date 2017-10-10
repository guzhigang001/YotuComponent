package com.example.ggxiaozhi.minesdk.okhttp3.request;

import java.util.Map;

import okhttp3.FormBody;
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
    public static Request createPostRequest(String url, RequestParams params) {
        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
            //请求参数集合遍历 添加到我们的请求构建类中
            builder.add(entry.getKey(), entry.getValue());
        }
        //通过请求构建类的build方法获取到真正的请求体对象
        FormBody formBody = builder.build();
        return new Request.Builder().url(url).post(formBody).build();
    }

    /**
     *
     * @param url
     * @param params
     * @return 返回一个创建好的Get请求的Request
     */
    public static Request createGetRequest(String url, RequestParams params) {
        StringBuilder builder = new StringBuilder(url).append("?");
        for (Map.Entry<String, String> map : params.urlParams.entrySet()) {
            builder.append(map.getKey()).append("=").append(map.getValue()).append("&");
        }

        return new Request.Builder().url(builder.substring(0, builder.length() - 1)).get().build();
    }
}
