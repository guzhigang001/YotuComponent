package com.example.ggxiaozhi.yotucomponent.share;


import android.content.Context;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.share
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/20
 * 功能   ：分享功能统一入口，负责发送数据到指定平台,可以优化为一个单例模式
 */

public class ShareManager {

    private static ShareManager mShareManager = null;
    /**
     * 要分享到的平台
     */
    private Platform mCurrentPlatform;

    /**
     * 线程安全的单例模式
     */
    public static ShareManager getInstance() {
        if (mShareManager == null) {
            synchronized (ShareManager.class) {
                if (mShareManager == null) {
                    mShareManager = new ShareManager();
                }
            }
        }
        return mShareManager;
    }

    public static void init(Context context){
        ShareSDK.initSDK(context);
    }
    private ShareManager() {

    }

    public void shareData(ShareData data, PlatformActionListener listener) {
        switch (data.mType) {
            case QQ:
                mCurrentPlatform = ShareSDK.getPlatform(QQ.NAME);
                break;
            case QZone:
                mCurrentPlatform = ShareSDK.getPlatform(QZone.NAME);
                break;
            case WeChat:
                mCurrentPlatform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case WechatMoments:
                mCurrentPlatform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
            default:
                break;
        }
        mCurrentPlatform.setPlatformActionListener(listener);
        mCurrentPlatform.share(data.mParams);
    }

    /**
     * 应用程序需要的平台
     */
    public enum PlatofrmType {
        QQ, QZone, WeChat, WechatMoments;
    }
}
