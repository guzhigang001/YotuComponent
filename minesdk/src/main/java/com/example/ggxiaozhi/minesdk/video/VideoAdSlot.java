package com.example.ggxiaozhi.minesdk.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.ggxiaozhi.minesdk.activity.AdBrowserActivity;
import com.example.ggxiaozhi.minesdk.constant.SDKConstant;
import com.example.ggxiaozhi.minesdk.module.AdValue;
import com.example.ggxiaozhi.minesdk.report.ReportManager;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.minesdk.video.constant.AdParameters;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/16
 * 功能   ：业务逻辑层 (小屏时)
 */

public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {

    private Context mContext;
    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;//要添加的父容器
    /**
     * Data
     */
    private AdValue mVideoInfo;
    private AdSDKSlotListener mSlotListener;
    private boolean canPause = false; //是否可自动暂停标志位
    private int lastArea = 0; //防止将要滑入滑出时播放器的状态改变

    public VideoAdSlot(AdValue adInstance, AdSDKSlotListener slotLitener) {
        mVideoInfo = adInstance;
        mSlotListener = slotLitener;
        mParentView = slotLitener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mVideoView = new CustomVideoView(mContext, mParentView);
        if (mVideoInfo != null) {
            mVideoView.setDataSource(mVideoInfo.resource);
            mVideoView.setListener(this);
            mVideoView.setFrameURI(mVideoInfo.thumb);
        }

        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);

        mParentView.addView(mVideoView);
    }

    /**
     * 实现划入播放 画出暂停  需要防在onScroll中
     */
    public void updataVideoInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //还未出现在屏幕上，不做任何处理
        if (currentArea <= SDKConstant.VIDEO_ERROR_NUM) {
            return;
        }

        //刚要滑入或者滑出时的异常处理
        if (Math.abs(currentArea - lastArea) >= SDKConstant.VIDEO_FULL_METRICS) {
            return;
        }

        //滑动没有超过50%
        if (currentArea <= SDKConstant.VIDEO_SCREEN_PERCENT) {
            if (canPause) {
                pauseVideo(true);
                canPause = false;//过滤时间 防止在滑动过程中多次调用pasueVideo();
            }
            lastArea = 0;
            mVideoView.setIsRealPause(false);
            mVideoView.setIsComplete(false);
            return;
        }

        //当视频真正的暂停(播放完成暂停)走此case
        if (isComplete() || isRealPasue()) {
            pauseVideo(false);
            canPause = false;
            return;
        }

        //满足用户的视频播放设置的条件
        if (Utils.canAutoPlay(mContext, AdParameters.getCurrentSetting()) || isPlaying()) {
            //真正的去播放视频
            resumeVideo();
            lastArea = currentArea;
            canPause = true;
            mVideoView.setIsRealPause(false);
        } else {
            pauseVideo(false);
            mVideoView.setIsRealPause(true);
        }
    }

    public void resumeVideo() {
        if (mVideoView != null) {
            mVideoView.resume();
            if (isPlaying()) {
                sendSUSReport(true); //发自动播放监测
            }
        }
    }


    /**
     * 实现从小屏到全屏播放的功能接口
     */
    @Override
    public void onClickFullScreenBtn() {


        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        //将播放器从View中移除
        mParentView.removeView(mVideoView);
        //创建全屏dialog
        VideoFullDialog dialog = new VideoFullDialog(mContext, mVideoView, mVideoInfo, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //全屏播放的时候点击了返回
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                //全屏状态下播放完成的时间回调
                bigPlayComplete();
            }
        });
        dialog.setViewBundle(bundle); //为Dialog设置播放器数据Bundle对象
        dialog.show();//显示全屏dialog
    }

    /**
     * 全屏状态下播放完成的时间回调
     */
    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.isShowFullBtn(true);
        mVideoView.seekAndResume(0);
        canPause = false;
    }

    /**
     * 返回小屏的时候
     */
    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.isShowFullBtn(true);//显示全屏按钮
        mVideoView.mute(true);//小屏静音
        mVideoView.setListener(this);//重新设置我们的监听为我们的业务逻辑层
        mVideoView.seekAndResume(position);//跳到制定位置播放

    }

    @Override
    public void onClickVideo() {
        String desationUrl = "http://blog.csdn.net/gg199402?viewmode=contents";
        if (mSlotListener != null) {
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                mSlotListener.onClickVideo(desationUrl);
                try {
                    ReportManager.pauseVideoReport(mVideoInfo.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //走默认样式
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL, mVideoInfo.clickUrl);
                mContext.startActivity(intent);
                try {
                    ReportManager.pauseVideoReport(mVideoInfo.clickMonitor, mVideoView.getCurrentPosition()
                            / SDKConstant.MILLION_UNIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {
        sendSUSReport(false);
    }

    @Override
    public void onAdVideoLoadSuccess() {
        if (mSlotListener != null)
            mSlotListener.onAdVideoLoadSuccess();
    }

    @Override
    public void onAdVideoLoadFailed() {
        if (mSlotListener != null)
            mSlotListener.onAdVideoLoadFailed();
        //加载失败全部回到初始状态
        canPause = false;
    }

    /**
     * 发送视频缓冲改变监测
     */
    @Override
    public void onBufferUpdate(int time) {
        try {
            ReportManager.suReport(mVideoInfo.middleMonitor, time / SDKConstant.MILLION_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送视频播放完成监测
     */
    @Override
    public void onAdVideoLoadComplete() {
        try {
            ReportManager.sueReport(mVideoInfo.endMonitor, false, getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSlotListener != null) {
            mSlotListener.onAdVideoLoadComplete();
        }
        mVideoView.setIsRealPause(true);
    }


    /**
     * 发送视频开始播放监测
     *
     * @param isAuto
     */
    private void sendSUSReport(boolean isAuto) {
        try {
            ReportManager.susReport(mVideoInfo.startMonitor, isAuto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPlaying() {
        if (mVideoView != null)
            return mVideoView.isPlaying();
        return false;
    }

    private boolean isRealPasue() {
        if (mVideoView != null)
            return mVideoView.isRealPause();
        return false;
    }

    private boolean isComplete() {
        if (mVideoView != null)
            return mVideoView.isComplete();
        return false;

    }

    private void pauseVideo(boolean isAuto) {
        if (mVideoView != null) {
            if (isAuto) {
                //发自动暂停监测
                if (!isRealPause() && isPlaying()) {
                    try {
                        ReportManager.pauseVideoReport(mVideoInfo.event.pause.content, getPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            mVideoView.seekAndPause(0);
        }
    }

    public void destroy() {
        if (mVideoView != null) {
            mVideoView.destroy();
            mVideoView = null;
            mContext = null;
            mVideoInfo = null;
        }
    }

    private boolean isRealPause() {
        if (mVideoView != null) {
            return mVideoView.isRealPause();
        }
        return false;
    }

    /**
     * 获取当前播放的秒数
     *
     * @return
     */
    private int getPosition() {
        return mVideoView.getCurrentPosition() / SDKConstant.MILLION_UNIT;
    }

    /**
     * 获取播放的总时长
     *
     * @return
     */
    private int getDuration() {
        return mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
    }

    //传递消息到appcontext层
    public interface AdSDKSlotListener {

        ViewGroup getAdParent();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();

        void onClickVideo(String url);
    }
}
