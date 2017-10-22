package com.example.ggxiaozhi.yotucomponent.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.base.BaseAvtivity;
import com.example.ggxiaozhi.yotucomponent.manager.UserManager;
import com.example.ggxiaozhi.yotucomponent.module.user.User;
import com.example.ggxiaozhi.yotucomponent.network.RequestCenter;
import com.example.ggxiaozhi.yotucomponent.view.weigth.MailBoxAssociateTokenizer;
import com.example.ggxiaozhi.yotucomponent.view.weigth.MailBoxAssociateView;

public class LoginActivity extends BaseAvtivity implements View.OnClickListener {


    /**
     * 自定义本地广播action
     */
    public final static String LOCAL_ACTION = "com.example.ggxiaozhi.yotucomponent.LOCAL_ACTION";
    /**
     * UI
     */
    private MailBoxAssociateView mUserNameAssociateView;
    private EditText mPasswordView;
    private TextView mLoginView;
    private ImageView mQQLoginView; //用来实现QQ登陆

    /**
     * data
     */
//    private PushMessage mPushMessage; // 推送过来的消息
//    private boolean fromPush; // 是否从推送到此页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hiddenStatusBar();
        setContentView(R.layout.activity_login);
        initData();
        initView();
    }

    private void initData() {
//        Intent intent = getIntent();
//        if (intent.hasExtra("pushMessage")) {
//            mPushMessage = (PushMessage) intent.getSerializableExtra("pushMessage");
//        }
//        fromPush = intent.getBooleanExtra("fromPush", false);
    }

    private void initView() {
        changeStatusBarColor(R.color.white);
        mUserNameAssociateView = (MailBoxAssociateView) findViewById(R.id.associate_email_input);
        mPasswordView = (EditText) findViewById(R.id.login_input_password);
        mLoginView = (TextView) findViewById(R.id.login_button);
        mLoginView.setOnClickListener(this);

        mUserNameAssociateView = (MailBoxAssociateView) findViewById(R.id.associate_email_input);
        String[] recommendMailBox = getResources().getStringArray(R.array.recommend_mailbox);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_associate_mail_list,
                R.id.tv_recommend_mail, recommendMailBox);
        mUserNameAssociateView.setAdapter(adapter);
//        mUserNameAssociateView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mUserNameAssociateView.setTokenizer(new MailBoxAssociateTokenizer());
        mQQLoginView = (ImageView) findViewById(R.id.iv_login_logo);
        mQQLoginView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_button:
                login();
                break;
        }
    }

    /**
     * 用户登录
     */
    private void login() {
        String userName = mUserNameAssociateView.getText().toString().trim();
        String userPwd = mPasswordView.getText().toString().trim();

        //登录请求
        RequestCenter.login(userName, userPwd, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                //获取用户信息
                final User user = (User) responseObj;
                UserManager.getInstance().setUser(user);

                if (user != null) {
                    Log.d(TAG, "onSuccess: ");
                    //登录成功通知应用其他模块 发送局部广播
                    sendLoginLocalBroadCast();
                    finish();
                }

            }

            @Override
            public void onError(Object errorObj) {

            }
        });
    }

    /**
     * 用户信息存入数据库，以使让用户一打开应用就是一个登陆过的状态
     */
    private void insertUserInfoIntoDB() {
    }



    /**
     * 发送本地广播
     */
    private void sendLoginLocalBroadCast() {
        Log.d(TAG, "sendLoginLocalBroadCast: ");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(LOCAL_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }
}
