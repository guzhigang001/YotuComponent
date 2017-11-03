package com.example.ggxiaozhi.minesdk.video;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ggxiaozhi.minesdk.R;
import com.example.ggxiaozhi.minesdk.constant.SDKConstant;
import com.example.ggxiaozhi.minesdk.module.AdValue;
import com.example.ggxiaozhi.minesdk.report.ReportManager;
import com.example.ggxiaozhi.minesdk.utils.Utils;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/16
 * 功能   ：全屏显示视频的Dialog
 */

public class VideoFullDialog extends Dialog implements CustomVideoView.ADVideoPlayerListener {
    private static final String TAG = VideoFullDialog.class.getSimpleName();


    private Context mContext;

    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private ImageView mBackButton;

    /**
     * Data
     */
    private AdValue mXAdInstance;
    private int mPosition;//从小屏到全屏时视频的播放位置
    private FullToSmallListener mListener;
    private boolean isFirst = true;

    //动画要执行的平移值
    private int deltaY;
    private VideoAdSlot.AdSDKSlotListener mSlotListener;
    private Bundle mStartBundle;
    private Bundle mEndBundle; //用于Dialog出入场动画


    public VideoFullDialog(Context context, CustomVideoView
            customVideoView, AdValue value, int position) {
        super(context, R.style.dialog_full_screen);//通过style的设置，保证我们的Dialog全屏
        mXAdInstance = value;
        mVideoView = customVideoView;
        mPosition = position;
        mContext = context;

    }

        /**
         * 初始化控件
         *
         * @param savedInstanceState
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.xadsdk_dialog_video_layout);
            initVideoView();
        }

    private void initVideoView() {

        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mBackButton = (ImageView) findViewById(R.id.xadsdk_player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        mRootView = (RelativeLayout) findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mRootView.setVisibility(View.INVISIBLE);

        mVideoView.setListener(this);
        mVideoView.mute(false);
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }

    /**
     * 全屏状态下返回(关闭)按钮的点击事件
     */
    @Override
    public void onClickBackBtn() {
        dismiss();
        if (mListener != null) {
            mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
        }
    }

    /**
     * back键的监听
     */
    @Override
    public void onBackPressed() {
        onClickBackBtn();
//super.onBackPressed(); 禁止掉返回键本身的关闭功能,转为自己的关闭效果
    }

    /**
     * 焦点状态改变时的回调
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG, "onWindowFocusChanged: " + hasFocus);
        if (!hasFocus) {
            //没有获取到焦点
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            mVideoView.isShowFullBtn(false);
            //isFirst=true 表示dialog首次创建且首次获得焦点
            if (isFirst) {
                mVideoView.seekAndResume(mPosition);
            } else {
                //获取到焦点
                mVideoView.resume();
            }
        }
        isFirst = false;
    }

    public void setSlotListener(VideoAdSlot.AdSDKSlotListener slotListener) {
        this.mSlotListener = slotListener;
    }

    @Override
    public void dismiss() {
        Log.d(TAG, "dismiss: ");
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    @Override
    public void onClickPlay() {

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
    }

    /**
     * 发送视频缓冲改变监测
     */
    @Override
    public void onBufferUpdate(int time) {
        try {
            ReportManager.suReport(mXAdInstance.middleMonitor, time / SDKConstant.MILLION_UNIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与小屏时的处理不同 单独处理
     */
    @Override
    public void onAdVideoLoadComplete() {
        try {
            int position = mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
            ReportManager.sueReport(mXAdInstance.endMonitor, true, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dismiss();
        if (mListener != null) {
            mListener.playComplete();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClickFullScreenBtn() {
        runExitAnimator();
    }

    @Override
    public void onClickVideo() {

    }

    //准备动画所需数据
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);
        /**
         * 将desationview移到originalview位置处
         */
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP)
                - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    //准备入场动画
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    //准备出场动画
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void runExitAnimator() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        try {
                            ReportManager.exitfullScreenReport(mXAdInstance.event.exitFull.content, mVideoView.getCurrentPosition()
                                    / SDKConstant.MILLION_UNIT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mListener != null) {
                            mListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                        }
                    }
                }).start();
    }

    public void setListener(FullToSmallListener listener) {
        this.mListener = listener;
    }

    /**
     * 与业务逻辑层（ViewAdSlot）进行通信
     */
    public interface FullToSmallListener {

        void getCurrentPlayPosition(int position);//全屏播放中点击关闭或返回键时的回调

        void playComplete();//全屏播放结束时回调
    }
}
