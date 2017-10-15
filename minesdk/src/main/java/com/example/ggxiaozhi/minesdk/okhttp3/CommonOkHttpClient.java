package com.example.ggxiaozhi.minesdk.okhttp3;

import com.example.ggxiaozhi.minesdk.okhttp3.https.HttpsUtils;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataHandle;
import com.example.ggxiaozhi.minesdk.okhttp3.request.RequestParams;
import com.example.ggxiaozhi.minesdk.okhttp3.response.CommonJsonCallback;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：请求的发送，请求参数的配置，https的支持
 */

public class CommonOkHttpClient {

    public static final int TIME_OUT = 30;//请求超时的时间
    public static OkHttpClient mHttpClient;

    /*为我们的client设置参数 此处也可以使用单例模式*/
    static {
        //创建我们client对象的构建者
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置client超时参数
        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

        //允许请求重定向 方便服务器灵活处理请求(默认为true)
        builder.followRedirects(true);

        //https的支持 (验证主机名字 返回true支持所有主机)
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        builder.sslSocketFactory(HttpsUtils.getSslSocketFactory());//支持所有证书

        //初始化client
        mHttpClient = builder.build();

    }

    /**
     * 发送具体的http/https请求
     *
     * @param request
     * @param handle  请求成功后的回调 和数据转化
     * @return
     */
    public static Call sendRequest(Request request, DisposeDataHandle handle) {

        Call call = mHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

}
