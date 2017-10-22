package com.example.ggxiaozhi.minesdk.video.listener;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video.listener
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/16
 * 功能   ：最终通知应用层视频播放是否成功
 */

public interface AdContextInterface {
    void onAdSuccess();

    void onAdFailed();

    void onClickVideo(String url);
}
