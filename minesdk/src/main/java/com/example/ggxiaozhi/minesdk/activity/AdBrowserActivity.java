package com.example.ggxiaozhi.minesdk.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ggxiaozhi.minesdk.R;

public class AdBrowserActivity extends AppCompatActivity {
    private static final String LOG_TAG = AdBrowserActivity.class.getSimpleName();

    public static final String KEY_URL = "url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_browser);
    }
}
