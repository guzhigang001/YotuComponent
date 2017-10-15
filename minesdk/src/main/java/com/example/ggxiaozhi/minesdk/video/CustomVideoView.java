package com.example.ggxiaozhi.minesdk.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ggxiaozhi.minesdk.R;
import com.example.ggxiaozhi.minesdk.constant.SDKConstant;
import com.example.ggxiaozhi.minesdk.utils.Utils;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/15
 * 功能   ：负责视频播放，暂停及各类时间的触发
 */

public class CustomVideoView extends RelativeLayout implements
        View.OnClickListener,//点击时间的监听
        MediaPlayer.OnBufferingUpdateListener,//mediaPlayer的缓存监听
        MediaPlayer.OnCompletionListener,//mediaPlayer的完成监听
        MediaPlayer.OnErrorListener,//mediaPlayer发生错误的监听
        MediaPlayer.OnPreparedListener,//mediaPlayer准备好了的监听
        TextureView.SurfaceTextureListener//TextureView生命周期的监听
{

    /**
     * Constant
     */
    private static final String TAG = "CustomVideoView";
    private static final int TIME_MSG = 0x01;//事件类型 供handle提供
    private static final int TIME_INVAL = 1000;//时间间隔 没1s发送一次handle msg
    /*视频播放的生命状态*/
    private static final int STATE_ERROR = -1;//错误
    private static final int STATE_IDLE = 0;//空闲
    private static final int STATE_PLAYING = 1;//播放中
    private static final int STATE_PAUSING = 2;//暂停状态

    private static final int LOAD_TOTAL_COUNT = 3;//开始加载时出现异常，重新加载的次数3次

    /**
     * UI
     */
    private ViewGroup mParentContainer;//视频播放的父容器
    private RelativeLayout mPlayerView;//本控件
    private TextureView mVideoView;//播放视频的View
    private Button mMiniPlayBtn;
    private ImageView mFullBtn;
    private ImageView mLoadingBar;
    private ImageView mFrameView;
    private AudioManager audioManager;//音量控制器
    private Surface videoSurface;//真正显示帧数据的类
    /**
     * Data
     */
    private String mUrl;//要加载的地址
    private String mFrameURI;
    private boolean isMute;//是否静音
    private int mScreenWidth, mDestationHeight;//屏幕宽度(屏幕宽度)和高度(16:9)高度

    /**
     * 播放过程中的Status状态
     */
    private boolean canPlay = true;
    private boolean mIsRealPause;//是否真正的暂停
    private boolean mIsComplete;//是否播放完成
    private int mCurrentCount;//当前的状态
    private int playerState = STATE_IDLE;//默认空闲状态

    private MediaPlayer mediaPlayer;
    private ADVideoPlayerListener listener;//事件监听回调
    private ScreenEventReceiver mScreenReceiver;//监听屏幕是否锁频广播

    private Handler handle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        /*向服务器放松监测(尽量不要使用Timer 容易导致内存泄漏)*/
                        listener.onBufferUpdate(getCurrentPosition());
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INVAL);
                    }
                    break;
            }
        }
    };


    public CustomVideoView(Context context, ViewGroup parentContainer) {
        super(context);
        mParentContainer = parentContainer;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initData();
        initView();
        registerBroadcastReceiver();
    }

    private void initData() {
        DisplayMetrics metrics = new DisplayMetrics();//获取屏幕大小
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        //将屏幕获取到的大小是指成默认的值
        manager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mDestationHeight = (int) (mScreenWidth * SDKConstant.VIDEO_HEIGHT_PERCENT);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = (TextureView) mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        mVideoView.setOnClickListener(this);
        mVideoView.setKeepScreenOn(true);//是屏幕常亮
        mVideoView.setSurfaceTextureListener(this);
        initSmallLayoutMode(); //init the small mode
    }

    /**
     * 初始化小平状态
     */
    private void initSmallLayoutMode() {
        LayoutParams params = new LayoutParams(mScreenWidth, mDestationHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);//设置Gralley
        mPlayerView.setLayoutParams(params);

        mMiniPlayBtn = (Button) mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
        mFullBtn = (ImageView) mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        mLoadingBar = (ImageView) mPlayerView.findViewById(R.id.loading_bar);
        mFrameView = (ImageView) mPlayerView.findViewById(R.id.framing_view);
        mMiniPlayBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
    }

    /**
     * 时间拦截 当时间传递到我们自定已的View中时我们就把事件消费掉
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 在View的显示发生改变时，回调此方法
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged: " + visibility);
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 在播放完成时的回调
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    /**
     * 播放产生异常时回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    /**
     * 视频已经准备好了 可以准备播放(调用start方法)
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (listener != null) {
                listener.onAdVideoLoadSuccess();
            }
            decideCanPlay();
        }

    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 表明我们的TextureView处于就绪状态
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private boolean isPlaying() {
        return false;
    }


    /**
     * 加载视频url
     */
    public void load() {
        if (this.playerState != STATE_IDLE) {
            return;
        }
        try {
            showLoadingView();
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();//完成播放器的创建工作
            mediaPlayer.setDataSource(mUrl);//播放地址
            mediaPlayer.prepareAsync();//异步加载视频
        } catch (Exception e) {
            reload();
        }
    }

    /**
     * 视频暂停
     */
    public void pause() {

    }

    /**
     * 恢复我们的视频
     */
    public void resume() {

    }

    /**
     * 播放完回到初始状态
     */
    public void playBack() {

    }

    /**
     * 停止状态
     */
    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnCompletionListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        handle.removeCallbacksAndMessages(null);//移除消息发送
        setCurrentPlayState(STATE_IDLE);
    }

    public void reload() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnCompletionListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        handle.removeCallbacksAndMessages(null);//移除消息发送
        setCurrentPlayState(STATE_IDLE);
        /**
         * 重试3次
         */
        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount += 1;
            load();
        } else {
            //停止重试 显示暂停状态
            showPauseView(false);
        }
    }

    /**
     * 销毁我们当前的自定义View(一些占用资源监听，事件都释放掉)
     */
    public void destory() {

    }

    /**
     * 跳到指定点播放视频(小屏播放全屏)
     *
     * @param position
     */
    public void seekAndResume(int position) {

    }

    /**
     * 跳到指定点暂停视频
     */
    public void seekAndPause() {

    }

    public void setListener(ADVideoPlayerListener listener) {
        this.listener = listener;
    }

    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();//每次都创建一个新的播放器
        }
    }

    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置流媒体类型(媒体音量)
        if (videoSurface != null && videoSurface.isValid()) {//videoSurface.isValid()当前videoSurface是有效的
            mediaPlayer.setSurface(videoSurface);
        } else {
            stop();
        }
        return mediaPlayer;
    }


    /**
     * 注册广播监听锁屏事件
     */
    private void registerBroadcastReceiver() {

    }

    /**
     * 解绑广播
     */
    private void unRegisterBroadcastReceiver() {
        if (mScreenReceiver != null) {
            getContext().unregisterReceiver(mScreenReceiver);
        }
    }

    /**
     * 决定是播放还是暂停
     */
    private void decideCanPlay() {

        //来回切换页面时 只有大于50%才播放
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT)
            resume();
        else
            pause();
    }

    /**
     * 是否显示全屏按钮
     */
    public void isShowFullBtn() {

    }

    /**
     * 判断现在是否是暂停状态
     *
     * @return
     */
    public boolean isPauseBtnClicked() {
        return mIsRealPause;
    }

    /**
     * 判断视频是否准备就绪
     *
     * @return
     */
    public boolean isComplete() {
        return mIsComplete;
    }


    /**
     * 暂停时的View
     */
    private void showPauseView(boolean show) {
        mFullBtn.setVisibility(show ? View.VISIBLE : View.GONE);
        mMiniPlayBtn.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        if (!show) {
            mFrameView.setVisibility(View.VISIBLE);
            loadFrameImage();
        } else {
            mFrameView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载时的View
     */
    private void showLoadingView() {
        mFullBtn.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingBar.getBackground();
        anim.start();
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
        loadFrameImage();
    }

    private void showPlayView() {
        mLoadingBar.clearAnimation();
        mLoadingBar.setVisibility(View.GONE);
        mMiniPlayBtn.setVisibility(View.GONE);
        mFrameView.setVisibility(View.GONE);
    }

    /**
     * 异步加载定帧图
     */
    private void loadFrameImage() {

    }

    /**
     * 设置当前的状态
     */
    private void setCurrentPlayState(int state) {

    }

    /**
     * 获取当前播放的时间
     */
    public void getDuration() {

    }

    /**
     * 获取当前播放的位置
     */
    private int getCurrentPosition() {
        return 0;
    }

    /**
     * 供View层来实现具体点击逻辑,具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface ADVideoPlayerListener {

        public void onBufferUpdate(int time);

        public void onClickFullScreenBtn();

        public void onClickVideo();

        public void onClickBackBtn();

        public void onClickPlay();

        public void onAdVideoLoadSuccess();

        public void onAdVideoLoadFailed();

        public void onAdVideoLoadComplete();
    }

    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    public interface ImageLoaderListener {
        /**
         * 如果图片下载不成功，传null
         *
         * @param loadedImage
         */
        void onLoadingComplete(Bitmap loadedImage);
    }

    public class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
