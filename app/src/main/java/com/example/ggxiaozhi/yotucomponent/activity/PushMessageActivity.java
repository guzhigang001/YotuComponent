package com.example.ggxiaozhi.yotucomponent.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ggxiaozhi.yotucomponent.R;

import java.util.ArrayList;

public class PushMessageActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String DATA_LIST = "data_list";
    public static final String TYPE = "type";
    public static final int UNREAD = 1;
    public static final int ZAN = 2;
    public static final int IMOOC = 3;
    /**
     * UI
     */
    private TextView mTitleView;
    private ImageView mBackView;
    private ListView mListView;

    /**
     * Data
     */
    private int msgType;
//    private ArrayList<MinaMessage> mLists;
//    private SystemMsgAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_message);
        initData();
        initView();
    }
    private void initView() {
        mBackView = (ImageView) findViewById(R.id.back_view);
        mBackView.setOnClickListener(this);
        mTitleView = (TextView) findViewById(R.id.title_view);
        switch (msgType) {
            case UNREAD:
                mTitleView.setText(getString(R.string.liuyan_message));
                break;
            case ZAN:
                mTitleView.setText(getString(R.string.receive_zan_message));
                break;
            case IMOOC:
                mTitleView.setText(getString(R.string.xitong_message));
                break;
        }
        mListView = (ListView) findViewById(R.id.list_view);

    }
    private void initData() {
        Intent intent = getIntent();
        msgType = intent.getIntExtra(TYPE, 3);
    }
    @Override
    public void onClick(View v) {

    }
}
