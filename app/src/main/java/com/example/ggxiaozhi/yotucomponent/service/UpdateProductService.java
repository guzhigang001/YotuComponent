package com.example.ggxiaozhi.yotucomponent.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.yotucomponent.db.SPManager;
import com.example.ggxiaozhi.yotucomponent.db.database.DBDataHelper;
import com.example.ggxiaozhi.yotucomponent.db.database.DBHelper;
import com.example.ggxiaozhi.yotucomponent.module.search.BaseSearchModel;
import com.example.ggxiaozhi.yotucomponent.module.search.ProductModel;
import com.example.ggxiaozhi.yotucomponent.network.HttpConstants;
import com.example.ggxiaozhi.yotucomponent.network.RequestCenter;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.service
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/18
 * 功能   ：
 */

public class UpdateProductService extends Service {
    /**
     * 常量
     */
    private static final int UPDATE_FLAG = 0x01;
    private static final int PRODUCT_FLAG = 0x02;

    private SPManager spManager;
    /**
     * 请求更新时间戳
     */
    private long updateTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_FLAG://
                    reqProductLatestUpdate();
                    break;
                case PRODUCT_FLAG:
                    reqProducList();
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        spManager = SPManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.sendEmptyMessage(UPDATE_FLAG);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void reqProductLatestUpdate() {
        RequestCenter.getData(HttpConstants.PRODUCT_LATESAT_UPDATE, null, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                //请求成功处理
                BaseSearchModel searchModel = (BaseSearchModel) responseObj;
                if (searchModel.data.uptime != -1) {
                    if (searchModel.data.uptime != spManager.getLong(SPManager.LAST_UPDATE_PRODUCT, -1)) {
                        updateTime = searchModel.data.uptime;
                        mHandler.sendEmptyMessage(PRODUCT_FLAG);
                    } else {
                        stopSelf();
                    }
                }
            }

            @Override
            public void onError(Object errorObj) {
                //请求失败处理
                if (DBDataHelper.getInstance().select(DBHelper.FUND_LIST_TABLE, null, null, null, ProductModel.class).size() > 0) {
                    stopSelf();
                } else {
                    mHandler.sendEmptyMessageDelayed(UPDATE_FLAG, 1000);
                }
            }

        }, BaseSearchModel.class);
    }

    //真正的去请求产品列表
    private void reqProducList() {
        RequestCenter.getData(HttpConstants.PRODUCT_LIST, null, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseSearchModel searchModel = (BaseSearchModel) responseObj;
                if (searchModel.data.list != null) {
                    DBDataHelper.getInstance().delete(DBHelper.FUND_LIST_TABLE, null, null);
                    if (DBDataHelper.getInstance().insert(DBHelper.FUND_LIST_TABLE, searchModel.data.list)) {
                        /**
                         * 更新成功,此时可以发送一个notification通知用户
                         */
                        spManager.putLong(SPManager.LAST_UPDATE_PRODUCT, updateTime);
                        stopSelf();
                    } else {
                        mHandler.sendEmptyMessageDelayed(PRODUCT_FLAG, 5000);
                    }
                }
            }

            @Override
            public void onError(Object reasonObj) {
                if (DBDataHelper.getInstance().select(DBHelper.FUND_LIST_TABLE, null, null, null, null).size() > 0) {
                    // 有数据，这次不更细了，提高效率
                    stopSelf();
                } else {
                    mHandler.sendEmptyMessageDelayed(PRODUCT_FLAG, 5000);
                }
            }
        }, BaseSearchModel.class);
    }
}
