package com.example.ggxiaozhi.yotucomponent.view.fragment.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.LoginActivity;
import com.example.ggxiaozhi.yotucomponent.activity.SettingActivity;
import com.example.ggxiaozhi.yotucomponent.application.MyApplication;
import com.example.ggxiaozhi.yotucomponent.manager.UserManager;
import com.example.ggxiaozhi.yotucomponent.module.update.UpdateModel;
import com.example.ggxiaozhi.yotucomponent.network.RequestCenter;
import com.example.ggxiaozhi.yotucomponent.service.update.UpdateService;
import com.example.ggxiaozhi.yotucomponent.share.ShareDialog;
import com.example.ggxiaozhi.yotucomponent.util.Util;
import com.example.ggxiaozhi.yotucomponent.view.fragment.BaseFragment;
import com.example.ggxiaozhi.yotucomponent.view.weigth.CommonDialog;
import com.example.ggxiaozhi.yotucomponent.view.weigth.MyQrCodeDialog;

import cn.sharesdk.framework.Platform;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final String imageUrl = "http://upload-images.jianshu.io/upload_images/3983615-85be4b0bce780165.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
    private static final String TAG = "MineFragment";
    /**
     * UI
     */
    private View mContentView;
    private RelativeLayout mLoginLayout;
    private CircleImageView mPhotoView;
    private TextView mLoginInfoView;
    private TextView mLoginView;
    private RelativeLayout mLoginedLayout;
    private TextView mUserNameView;
    private TextView mTickView;
    private TextView mVideoPlayerView;
    private TextView mShareView;
    private TextView mQrCodeView;
    private TextView mUpdateView;

    //自定义了一个广播接收器
    private LoginBroadcastReceiver receiver = new LoginBroadcastReceiver();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        registerBroadCastReceiver();
        initView();
        return mContentView;
    }

    /**
     * 注册本地广播
     */
    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginActivity.LOCAL_ACTION);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, filter);
    }

    /**
     * 解绑本地广播
     */
    private void unregisterBroadCastReceiver() {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);
    }

    private void initView() {
        mLoginLayout = (RelativeLayout) mContentView.findViewById(R.id.login_layout);
        mLoginLayout.setOnClickListener(this);
        mLoginedLayout = (RelativeLayout) mContentView.findViewById(R.id.logined_layout);
        mLoginedLayout.setOnClickListener(this);

        mPhotoView = (CircleImageView) mContentView.findViewById(R.id.photo_view);
        mPhotoView.setOnClickListener(this);
        mLoginView = (TextView) mContentView.findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
        mVideoPlayerView = (TextView) mContentView.findViewById(R.id.video_setting_view);
        mVideoPlayerView.setOnClickListener(this);
        mShareView = (TextView) mContentView.findViewById(R.id.share_imooc_view);
        mShareView.setOnClickListener(this);
        mQrCodeView = (TextView) mContentView.findViewById(R.id.my_qrcode_view);
        mQrCodeView.setOnClickListener(this);
        mLoginInfoView = (TextView) mContentView.findViewById(R.id.login_info_view);
        mUserNameView = (TextView) mContentView.findViewById(R.id.username_view);
        mTickView = (TextView) mContentView.findViewById(R.id.tick_view);

        mUpdateView = (TextView) mContentView.findViewById(R.id.update_view);
        mUpdateView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_imooc_view:
                //未登陆，则跳轉到登陸页面
                if (!UserManager.getInstance().hasLogin()) {
                    toLogin();
                } else
                    shareFriend();
                break;
            case R.id.video_setting_view:
                Intent intent = new Intent(MyApplication.getInstance(), SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.update_view:
                checkVersion();
                break;
            case R.id.login_layout:
            case R.id.login_view:
                //未登陆，则跳轉到登陸页面
                if (!UserManager.getInstance().hasLogin()) {
                    toLogin();
                }
                break;
            case R.id.my_qrcode_view:
                if (!UserManager.getInstance().hasLogin()) {
                    //未登陆，去登陆。
                    toLogin();
                } else {
                    //已登陆根据用户ID生成二维码显示
                    MyQrCodeDialog dialog = new MyQrCodeDialog(mContext);
                    dialog.show();
                }
                break;
        }
    }

    /**
     * 去登陆页面
     */
    private void toLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    public void checkVersion() {
        RequestCenter.checkVersion(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.d(TAG, "onSuccess: ");
                UpdateModel model = (UpdateModel) responseObj;
                if (Util.getVersionCode(mContext) < model.data.currentVersion) {
                    //有新版本
                    CommonDialog dialog = new CommonDialog(mContext, getString(R.string.update_new_version),
                            getString(R.string.update_title), getString(R.string.update_install),
                            getString(R.string.cancel), new CommonDialog.DialogClickListener() {
                        @Override
                        public void onDialogClick() {
                            //确认按钮监听
                            Intent intent = new Intent(mContext, UpdateService.class);
                            mContext.startService(intent);
                        }
                    });
                    dialog.show();
                } else {
                    Toast.makeText(mContext, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Object errorObj) {
                Toast.makeText(mContext, errorObj.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 分享好友
     */
    private void shareFriend() {
        ShareDialog dialog = new ShareDialog(mContext, false);
        dialog.setShareType(Platform.SHARE_IMAGE);
        dialog.setShareTitle("慕课网");
        dialog.setShareTitleUrl("http://www.imooc.com");
        dialog.setShareText("慕课网");
        dialog.setShareSite("imooc");
        dialog.setShareSiteUrl("http://www.imooc.com");
        dialog.setImagePhoto(Environment.getExternalStorageDirectory() + "/test2.jpg");
        dialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadCastReceiver();

    }

    /**
     * 本地广播发送来的消息，并更新UI
     */
    class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "asdasd", Toast.LENGTH_SHORT).show();
            if (UserManager.getInstance().hasLogin()) {
                //更新我们的fragment
                if (mLoginedLayout.getVisibility() == View.GONE) {
                    mLoginLayout.setVisibility(View.GONE);
                    mLoginedLayout.setVisibility(View.VISIBLE);
                    mUserNameView.setText(UserManager.getInstance().getUser().data.name);
                    mTickView.setText(UserManager.getInstance().getUser().data.tick);
                    ImageLoaderManager.getInstance(mContext).displayImage(mPhotoView, imageUrl);
                }
            }
        }
    }
}
