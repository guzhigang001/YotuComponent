package com.example.ggxiaozhi.minesdk.video;

import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import com.example.ggxiaozhi.minesdk.activity.AdBrowserActivity;
import com.example.ggxiaozhi.minesdk.module.AdValue;
import com.example.ggxiaozhi.minesdk.okhttp3.HttpConstant;
import com.example.ggxiaozhi.minesdk.report.ReportManager;
import com.example.ggxiaozhi.minesdk.utils.ResponseEntityToModule;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.minesdk.video.listener.AdContextInterface;
import com.google.gson.Gson;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/16
 * 功能   ：对外提供视频SDK的入口(利用外观模式 统一包装方便调用)
 */

public class VideoAdContext implements VideoAdSlot.AdSDKSlotListener {
    private static final String TAG = "VideoAdContext";
    //the ad container
    private ViewGroup mParentView;

    private VideoAdSlot mAdSlot;
    private AdValue mInstance = null;
    //the listener to the app layer
    private AdContextInterface mListener;
    private CustomVideoView.ADFrameImageLoadListener mFrameLoadListener;

    public VideoAdContext(ViewGroup parentView, String instance) {
        mParentView = parentView;
        mInstance = (AdValue) ResponseEntityToModule.parseJsonToModule(instance, AdValue.class);
        Log.d(TAG, "VideoAdContext: " + mInstance);
        load();
    }

    /**
     * 创建业务罗积累ViewAdSolt 不调用则不会创建最底层的CustomVideoView
     */
    public void load() {
        if (mInstance != null && mInstance.resource != null) {
            mAdSlot = new VideoAdSlot(mInstance, this);
        }
    }

    /**
     * 根据滑动距离来判断是否可以自动播放, 出现超过50%自动播放，离开超过50%,自动暂停
     */
    public void updateVideoScollView() {
        if (mAdSlot != null) {
            mAdSlot.updataVideoInScrollView();
        }
    }

    public void setListener(AdContextInterface listener) {
        this.mListener = listener;
    }

    /**
     * release the ad
     */
    public void destroy() {
        if (mAdSlot != null)
            mAdSlot.destroy();
    }

    public void setAdResultListener(AdContextInterface listener) {
        this.mListener = listener;
    }


    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mListener != null) {
            mListener.onAdSuccess();
        }
//        sendAnalizeReport(HttpConstant.Params.ad_load, HttpConstant.AD_PLAY_SUCCESS);
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mListener != null) {
            mListener.onAdFailed();
        }
//        sendAnalizeReport(HttpConstant.Params.ad_load, HttpConstant.AD_PLAY_FAILED);
    }

    @Override
    public void onAdVideoLoadComplete() {
    }

    @Override
    public void onClickVideo(String url) {
        if (mListener != null) {
            mListener.onClickVideo(url);
        } else {
            Intent intent = new Intent(mParentView.getContext(), AdBrowserActivity.class);
            intent.putExtra(AdBrowserActivity.KEY_URL, url);
            mParentView.getContext().startActivity(intent);
        }
    }

    /**
     * 发送广告数据解析成功监测
     */
    private void sendAnalizeReport(HttpConstant.Params step, String result) {
        try {
            ReportManager.sendAdMonitor(Utils.isPad(mParentView.getContext().
                            getApplicationContext()), mInstance == null ? "" : mInstance.resourceID,
                    (mInstance == null ? null : mInstance.adid), Utils.getAppVersion(mParentView.getContext()
                            .getApplicationContext()), step, result);
        } catch (Exception e) {

        }
    }
}
