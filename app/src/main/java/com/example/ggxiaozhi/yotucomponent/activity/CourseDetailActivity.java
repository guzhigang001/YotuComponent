package com.example.ggxiaozhi.yotucomponent.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.base.BaseAvtivity;
import com.example.ggxiaozhi.yotucomponent.adapter.CourseCommentAdapter;
import com.example.ggxiaozhi.yotucomponent.module.course.BaseCourseModel;
import com.example.ggxiaozhi.yotucomponent.network.RequestCenter;
import com.example.ggxiaozhi.yotucomponent.util.Util;
import com.example.ggxiaozhi.yotucomponent.view.course.CourseDetailFooterView;
import com.example.ggxiaozhi.yotucomponent.view.course.CourseDetailHeaderView;

/**
 * 课程详情Activity, 展示课程详情,这个页面要用signalTop模式
 */
public class CourseDetailActivity extends BaseAvtivity implements View.OnClickListener {

    public static String COURSE_ID = "courseID";

    /**
     * UI
     */
    private ImageView mBackView;
    private ListView mListView;
    private ImageView mLoadingView;
    private RelativeLayout mBottomLayout;
    private ImageView mJianPanView;
    private EditText mInputEditView;
    private TextView mSendView;
    private CourseDetailHeaderView headerView;
    private CourseDetailFooterView footerView;
    private CourseCommentAdapter mAdapter;
    /**
     * Data
     */
    private String mCourseID;
    private BaseCourseModel mData;
    private String tempHint = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ourse_detail);
        initData();
        initView();
        requestDeatil();
        Log.d(TAG, "onCreate: ");
    }

    /**
     * 当使用singleTask和singleTop再次启动相同的Avtivity时会走这个方法
     *
     * @param intent 这个是由跳转的Intent在这里可以得到
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        setIntent(intent);//将传来的intent设置到当前的活动中 在调用getIntent会得到最新的intent
        initData();
        initView();
        requestDeatil();
    }

    /**
     * 发送请求
     */
    private void requestDeatil() {
        RequestCenter.requestCourseDetail(mCourseID, new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mData = (BaseCourseModel) responseObj;
                Log.d(TAG, "onSuccess: " + mData.toString());
                updateUI();
            }

            @Override
            public void onError(Object errorObj) {

            }
        });
    }

    /**
     * 根据返回的数据更新UI
     */
    private void updateUI() {
        mLoadingView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mAdapter = new CourseCommentAdapter(this, mData.data.body);
        mListView.setAdapter(mAdapter);

        if (headerView != null) {
            mListView.removeHeaderView(headerView);
        }
        headerView = new CourseDetailHeaderView(this, mData.data.head);
        mListView.addHeaderView(headerView);


        if (footerView != null) {
            mListView.removeFooterView(footerView);
        }
        footerView = new CourseDetailFooterView(this, mData.data.footer);
        mListView.addFooterView(footerView);
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        mCourseID = intent.getStringExtra(COURSE_ID);
    }

    //初始化数据
    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mBackView.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.comment_list_view);
        //mListView.setOnItemClickListener(this);
        mListView.setVisibility(View.GONE);
        mLoadingView = (ImageView) findViewById(R.id.loading_view);
        mLoadingView.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();

        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mJianPanView = (ImageView) findViewById(R.id.jianpan_view);
        mJianPanView.setOnClickListener(this);
        mInputEditView = (EditText) findViewById(R.id.comment_edit_view);
        mSendView = (TextView) findViewById(R.id.send_view);
        mSendView.setOnClickListener(this);
        mBottomLayout.setVisibility(View.GONE);

        intoEmptyState();
    }

    private void intoEmptyState() {
        tempHint = "";
        mInputEditView.setText("");
        mInputEditView.setHint(getString(R.string.input_comment));
        Util.hideSoftInputMethod(this, mInputEditView);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        headerView.destoryVideoView();
    }
}
