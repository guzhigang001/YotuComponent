package com.example.ggxiaozhi.minesdk.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.ggxiaozhi.minesdk.video.constant.AdParameters;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/15
 * 功能   ：负责视频播放，暂停及各类事件的触发
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


    private ADFrameImageLoadListener mFrameLoadListener;
    private Handler handle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        //还可以在这里更新progressbar
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

    public void setFrameURI(String url) {
        mFrameURI = url;
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
     * 是否显示全屏按钮
     */
    public void isShowFullBtn(boolean isShow) {
        mFullBtn.setImageResource(isShow ? R.drawable.xadsdk_ad_mini : R.drawable.xadsdk_ad_mini_null);
        mFullBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public boolean isRealPause() {
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
     * 事件拦截 当时间传递到我们自定已的View中时我们就把事件消费掉
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
        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
            if (isRealPause() || isComplete()) {
                //表示播放完成 后的暂停状态
                pause();
            } else {
                decideCanPlay();
            }
        } else {
            pause();
        }
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

        if (listener != null) {
            listener.onAdVideoLoadComplete();
        }
        playBack();
        setIsRealPause(true);
        setIsComplete(true);

    }

    /**
     * 播放产生异常时回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return true 表示自己处理 默认false 表示android默认处理
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        this.playerState = STATE_ERROR;
        if (mCurrentCount >= LOAD_TOTAL_COUNT) {
            if (listener != null) {
                listener.onAdVideoLoadFailed();
            }
            showPauseOrPlayView(false);
        }
        stop();
        return true;
    }

    /**
     * 视频已经准备好了 可以准备播放(调用start方法)
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        showPlayView();
        mediaPlayer = mp;
        if (mediaPlayer != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            mCurrentCount = 0;
            if (listener != null) {
                listener.onAdVideoLoadSuccess();
            }
            //满足自动播放条件，则直接播放
            if (Utils.canAutoPlay(getContext(),
                    AdParameters.getCurrentSetting()) &&
                    Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
                setCurrentPlayState(STATE_PAUSING);
                resume();
            } else {
                setCurrentPlayState(STATE_PLAYING);
                pause();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v == this.mMiniPlayBtn) {
            if (this.playerState == STATE_PAUSING) {
                if (Utils.getVisiblePercent(mParentContainer)
                        > SDKConstant.VIDEO_SCREEN_PERCENT) {
                    resume();
                    this.listener.onClickPlay();
                }
            } else {
                load();
            }
        } else if (v == this.mFullBtn) {
            this.listener.onClickFullScreenBtn();
        } else if (v == mVideoView) {
            this.listener.onClickVideo();
        }
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
        Log.d(TAG, "onSurfaceTextureAvailable: ");
        videoSurface = new Surface(surface);
        checkMediaPlayer();
        mediaPlayer.setSurface(videoSurface);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: ");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed: ");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated: ");
    }

    protected boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }


    /**
     * 加载视频url
     */
    public void load() {
        Log.d(TAG, "load: mUrl" + mUrl);
        if (this.playerState != STATE_IDLE) {
            return;
        }
        try {
            showLoadingView();
            setCurrentPlayState(STATE_IDLE);
            checkMediaPlayer();//完成播放器的创建工作
            mediaPlayer.setDataSource(mUrl);//播放地址
            mute(true);
            mediaPlayer.prepareAsync();//异步加载视频
        } catch (Exception e) {
            reload();
        }
    }

    /**
     * 视频暂停
     */
    public void pause() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                this.mediaPlayer.seekTo(0);
            }
        }
        showPauseOrPlayView(false);
        handle.removeCallbacksAndMessages(null);
    }
    //全屏不显示暂停状态,后续可以整合，不必单独出一个方法
    public void pauseForFullScreen() {
        if (playerState != STATE_PLAYING) {
            return;
        }
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.pause();
            if (!this.canPlay) {
                mediaPlayer.seekTo(0);
            }
        }
        handle.removeCallbacksAndMessages(null);
    }
    /**
     * 恢复我们的视频
     */
    public void resume() {

        if (playerState != STATE_PAUSING) {
            Log.d(TAG, "resume: " + playerState);
            return;
        }
        if (!isPlaying()) {
            entryResumeState();//置为播放中状态的值
            mediaPlayer.start();
            handle.sendEmptyMessageAtTime(TIME_MSG, TIME_INVAL);
            showPauseOrPlayView(true);
        } else {
            showPauseOrPlayView(false);
        }
    }

    /**
     * 进入播放状态的状态更新
     */
    public void entryResumeState() {
        canPlay = true;
        setCurrentPlayState(STATE_PLAYING);
        setIsComplete(false);
        setIsRealPause(false);
    }

    /**
     * 播放完回到初始状态 （不销毁mediaPlayer 再播放时可以直接播放）
     */
    public void playBack() {
        setCurrentPlayState(STATE_PAUSING);
        handle.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        showPauseOrPlayView(false);
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
     /*   if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.setOnCompletionListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        handle.removeCallbacksAndMessages(null);//移除消息发送
        setCurrentPlayState(STATE_IDLE);*/
        /**
         * 重试3次
         */
        if (mCurrentCount < LOAD_TOTAL_COUNT) {
            mCurrentCount += 1;
            load();
        } else {
            //停止重试 显示暂停状态
            showPauseOrPlayView(false);
        }
    }




    /**
     * 跳到指定点播放视频(小屏播放全屏)
     *
     * @param position
     */
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseOrPlayView(true);
            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.d(TAG, "do seek and resume");
                    mediaPlayer.start();
                    handle.sendEmptyMessage(TIME_MSG);
                }
            });
        }
    }

    /**
     * 跳到指定点暂停视频
     */
    public void seekAndPause(int position) {
        Log.d(TAG, "seekAndPause: ");
        if (this.playerState != STATE_PLAYING) {
            return;
        }
        showPauseOrPlayView(false);
        setCurrentPlayState(STATE_PAUSING);
        if (isPlaying()) {
            mediaPlayer.seekTo(position);
            /**
             * 先seekTo 调用后会执行监听的方法 保证先后顺序
             */
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    Log.d(TAG, "do seek and pause");
                    /**
                     * 移动到指定位置后暂停
                     */
                    mediaPlayer.pause();
                    handle.removeCallbacksAndMessages(null);
                }
            });
        }
    }

    public void setListener(ADVideoPlayerListener listener) {
        this.listener = listener;
    }

    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();//每次都创建一个新的播放器
        }
    }

    public void setFrameLoadListener(ADFrameImageLoadListener frameLoadListener) {
        this.mFrameLoadListener = frameLoadListener;
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
        if (mScreenReceiver == null) {
            mScreenReceiver = new ScreenEventReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getContext().registerReceiver(mScreenReceiver, filter);
        }
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
     * 判断现在是否是暂停状态
     *
     * @return
     */
    public boolean isPauseBtnClicked() {
        return mIsRealPause;
    }


    /**
     * 设置默认图片
     *
     * @return
     */
    public boolean isFrameHidden() {
        return mFrameView.getVisibility() == View.VISIBLE ? false : true;
    }

    public void setIsComplete(boolean isComplete) {
        mIsComplete = isComplete;
    }

    public void setIsRealPause(boolean isRealPause) {
        this.mIsRealPause = isRealPause;
    }

    /**
     * 暂停或是播放时时的View true为播放时 false为暂停
     */
    private void showPauseOrPlayView(boolean show) {
        Log.d(TAG, "showPauseOrPlayView: ");
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
        if (mFrameLoadListener != null) {
            mFrameLoadListener.onStartFrameLoad(mFrameURI, new ImageLoaderListener() {
                @Override
                public void onLoadingComplete(Bitmap loadedImage) {
                    if (loadedImage != null) {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_XY);
                        mFrameView.setImageBitmap(loadedImage);
                    } else {
                        mFrameView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        mFrameView.setImageResource(R.drawable.xadsdk_img_error);
                    }
                }
            });
        }
    }

    /**
     * 释放资源
     */
    public void destroy() {
        Log.d(TAG, " do destroy");
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setOnSeekCompleteListener(null);
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        setCurrentPlayState(STATE_IDLE);
        mCurrentCount = 0;
        setIsComplete(false);
        setIsRealPause(false);
        unRegisterBroadcastReceiver();
        handle.removeCallbacksAndMessages(null); //release all message and runnable
        showPauseOrPlayView(false); //除了播放和loading外其余任何状态都显示pause
    }

    /**
     * 设置当前的状态
     */
    public void setCurrentPlayState(int state) {
        playerState = state;
    }

    /**
     * true 表示没有声音
     *
     * @param mute
     */
    public void mute(boolean mute) {
        Log.d(TAG, "mute");
        isMute = mute;
        if (mediaPlayer != null && this.audioManager != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 获取当前播放的时间
     */
    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    public int getCurrentPosition() {
        if (this.mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 供View层来实现具体点击逻辑,具体逻辑还会变，
     * 如果对UI的点击没有具体监测的话可以不回调
     */
    public interface ADVideoPlayerListener {

        void onBufferUpdate(int time);//视频播放到了第几秒

        void onClickFullScreenBtn();//点击全名按钮跳转全屏

        void onClickVideo();

        void onClickBackBtn();

        void onClickPlay();

        void onAdVideoLoadSuccess();

        void onAdVideoLoadFailed();

        void onAdVideoLoadComplete();
    }

    public interface ADFrameImageLoadListener {

        void onStartFrameLoad(String url, ImageLoaderListener listener);
    }

    /**
     * 播放地址
     *
     * @param url
     */
    public void setDataSource(String url) {
        this.mUrl = url;
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

            switch (intent.getAction()) {
                case Intent.ACTION_USER_PRESENT://解锁发送的广播
                    if (playerState == STATE_PAUSING) {
                        if (mIsRealPause) {
                            //播放结束 依旧暂停
                            pause();
                        } else {
                            decideCanPlay();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF://锁屏发送的广播
                    if (playerState == STATE_PLAYING)
                        pause();
                    break;
            }
        }
    }
}
