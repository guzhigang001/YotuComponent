package com.example.ggxiaozhi.yotucomponent.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.activity
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：主要是为我们所有的Activity提供公共的行为或事件
 */

public class BaseAvtivity extends AppCompatActivity {

    protected String TAG = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getComponentName().getShortClassName();
    }
}
