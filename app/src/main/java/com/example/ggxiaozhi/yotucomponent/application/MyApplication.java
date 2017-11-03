package com.example.ggxiaozhi.yotucomponent.application;

import android.app.Application;

import com.example.ggxiaozhi.yotucomponent.share.ShareManager;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;


/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.application
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：1.整个程序的入口 2初始化的工作(比如一些第三方库和工具的初始化) 3.为整个应用的其他模块提供上下文
 */

public class MyApplication extends Application {
    private static MyApplication myApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        initShareSDK();
        initJPush();
        initUMeng();
    }

    public static MyApplication getInstance() {
        return myApplication;
    }

    public void initShareSDK() {
        ShareManager.init(this);
    }

    public void initJPush() {
        JPushInterface.setDebugMode(true);//如果上线的话改成false
        JPushInterface.init(this);
    }

    public void initUMeng() {
        MobclickAgent.setDebugMode(true);//未上线调试
        MobclickAgent.openActivityDurationTrack(false);//不需要跟踪应用的执行轨迹
    }
}
