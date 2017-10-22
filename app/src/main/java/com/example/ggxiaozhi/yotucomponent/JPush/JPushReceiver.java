package com.example.ggxiaozhi.yotucomponent.JPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.ggxiaozhi.minesdk.utils.ResponseEntityToModule;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.yotucomponent.activity.HomeActivity;
import com.example.ggxiaozhi.yotucomponent.activity.LoginActivity;
import com.example.ggxiaozhi.yotucomponent.activity.PushMessageActivity;
import com.example.ggxiaozhi.yotucomponent.manager.UserManager;
import com.example.ggxiaozhi.yotucomponent.module.PushMessage;
import com.example.ggxiaozhi.yotucomponent.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.ui.PushActivity;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.JPush
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：用来接收极光SDK推送给APP的消息
 */

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");

            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

//            openNotification(context,bundle);

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }

        if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            //普通的通知 不需要跳转

        } else if (intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            //通知需要跳转的Action
            PushMessage message = (PushMessage) ResponseEntityToModule.parseJsonToModule
                    (bundle.getString(JPushInterface.EXTRA_EXTRA), PushMessage.class);
            Log.d(TAG, "onReceive: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            if (Util.getCurrentTask(context)) {
                //应用已启动
                Intent pushIntent = new Intent();
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//创建一个任务栈有则复用，没有则创建新的
                pushIntent.putExtra("pushMessage", message);
                if (message != null && message.messageType.equals("2")
                        && !UserManager.getInstance().hasLogin()) {
                    //需要登录且没有登录
                    pushIntent.setClass(context, LoginActivity.class);
                    pushIntent.putExtra("formPush", true);
                } else {
                    //用户已经登录不需要再登录，直接跳转消息展示页面
                    pushIntent.setClass(context, PushMessageActivity.class);
                }
                context.startActivity(pushIntent);
            } else {
                //应用为启动
                Intent mainIntent = new Intent(context, HomeActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (message.messageType.equals("2") && !UserManager.getInstance().hasLogin() &&
                        message != null) {
                    //需要登录
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    loginIntent.putExtra("formPush", true);
                    loginIntent.putExtra("pushMessage", message);
                    context.startActivities(new Intent[]{mainIntent, loginIntent});
                } else {
                    //不需要登录
                    Intent pushIntent = new Intent(context, PushMessageActivity.class);
                    pushIntent.putExtra("pushMessage", message );
                    context.startActivities(new Intent[]{mainIntent, pushIntent});
                }
            }
        }
    }
    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {

                    /**
                     * 先将JSON字符串转化为对象，再取其中的字段
                     */
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }


    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }
}
