package com.example.ggxiaozhi.minesdk.okhttp3.response;

import android.os.Handler;
import android.os.Looper;

import com.example.ggxiaozhi.minesdk.okhttp3.exception.OkHttpException;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataHandle;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.minesdk.utils.ResponseEntityToModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.okhttp3.response
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：封装网络请求回调接口：1.请求异常处理 2.请求成功回调转实体对象
 */

public class CommonJsonCallback implements Callback {
    /**
     * 逻辑层异常，可能在不同的应用程序中发生变化
     * 定义一些常见的请求错误
     */
    protected final String RESULT_CODE = "ecode"; // 有返回则对于http请求来说是成功的，但还有可能是业务逻辑上的错误
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";//具体的错误信息
    protected final String EMPTY_MSG = "";//请求数据为空的信息
    protected final String COOKIE_STORE = "Set-Cookie"; // decide the server it(cookie错误 这是服务器的决定的)
    // can has the value of
    // set-cookie2

    /**
     * java层异常，与逻辑错误不一样
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Handler mDeliveryHandler;//消息转发更新UI
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mClass = handle.mClass;
        this.mListener = handle.mListener;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException e) {

        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onError(new OkHttpException(NETWORK_ERROR, e.getMessage()));
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(Object responseObj) {
        if (responseObj == null && ("").equals(responseObj.toString().trim())) {
            //服务器返回空数据 可能还是存在异常
            mListener.onError(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            //RESULT_CODE为服务器返回Json数据的中存在的字段0 表示数据返回成功
            if (result.has(RESULT_CODE)) {
                if (result.getInt(RESULT_CODE) == RESULT_CODE_VALUE) {//0 表示数据返回成功
                    if (mClass == null) {//表示业务层(ui)不需要将返回的数据转化成实体类
                        mListener.onSuccess(responseObj.toString());//直接返回数据
                    } else {//将返回的JSON转化成实体类
                        Object module = ResponseEntityToModule.parseJsonObjectToModule(result, mClass);
                        if (module != null) {//表明正确转化实体对象
                            mListener.onSuccess(module);
                        } else {//返回的不是合法的json
                            mListener.onError(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                        }
                    }
                } else {//0 表示数据返回成功 此时返还的不是0
                    mListener.onError(new OkHttpException(OTHER_ERROR, result.get(RESULT_CODE)));
                }
            }
        } catch (JSONException e) {
            mListener.onError(new OkHttpException(OTHER_ERROR, e.getMessage()));
        }
    }
}
