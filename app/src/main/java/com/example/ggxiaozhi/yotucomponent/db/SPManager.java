package com.example.ggxiaozhi.yotucomponent.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ggxiaozhi.yotucomponent.application.MyApplication;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.db
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/18
 * 功能   ：sharePrences工具类
 */

public class SPManager {

    private static SPManager mInstance;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private static final String SHARE_PREFERENCES_NAME = "record_netweork";//配置文件名称

    public static final String VIDEO_TYPE = "video_setting";//存储网络状态的key

    /**
     * 上次更新时间
     */
    public static final String LAST_UPDATE_PRODUCT = "last_update_product";
    public static final String VIDEO_PLAY_SETTING = "video_play_setting";
    public static final String IS_SHOW_GUIDE = "is_show_guide";
    public static SPManager getInstance() {
        if (mInstance == null) {
            synchronized (SPManager.class) {
                if (mInstance == null) {
                    mInstance = new SPManager();
                }
            }
        }
        return mInstance;
    }

    private SPManager() {
        mSharedPreferences = MyApplication.getInstance().getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void putLong(String key, Long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public Long getLong(String key, int defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }
}
