package com.example.ggxiaozhi.yotucomponent.view.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ggxiaozhi.minesdk.okhttp3.listener.DisposeDataListener;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.PhotoViewActivity;
import com.example.ggxiaozhi.yotucomponent.activity.WebViewActivity;
import com.example.ggxiaozhi.yotucomponent.adapter.RecommendAdapter;
import com.example.ggxiaozhi.yotucomponent.constant.Constant;
import com.example.ggxiaozhi.yotucomponent.module.BaseRecommandModel;
import com.example.ggxiaozhi.yotucomponent.module.recommend.RecommandValue;
import com.example.ggxiaozhi.yotucomponent.network.RequestCenter;
import com.example.ggxiaozhi.yotucomponent.util.PagerController;
import com.example.ggxiaozhi.yotucomponent.view.fragment.BaseFragment;
import com.example.ggxiaozhi.yotucomponent.zxing.app.CaptureActivity;

import java.util.ArrayList;


public class HomeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "HomeFragment";
    /**
     * UI
     */
    private View mContentView;
    private ListView mListView;
    private TextView mQRCodeView;
    private TextView mCategoryView;
    private TextView mSearchView;
    private ImageView mLoadingView;

    /**
     * data
     */
    private RecommendAdapter mAdapter;
    private BaseRecommandModel mRecommandData;

    private PagerController mController;

    private static final int REQUEST_QRCODE = 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestRecommendData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mController = new PagerController(mContext);
        mContentView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        initView();
        return mContentView;
    }

    /**
     * 发送首页数据请求
     */
    private void requestRecommendData() {
        Log.d(TAG, "requestRecommendData: ");
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommandData = (BaseRecommandModel) responseObj;
                showSuccessView(mRecommandData);
            }

            @Override
            public void onError(Object errorObj) {
                Log.d(TAG, "onError: ");
            }
        });
    }

    private void showSuccessView(BaseRecommandModel recommandData) {
        Log.d(TAG, "showSuccessView: " + recommandData);
        if (recommandData.data.list.size() > 0 && recommandData != null) {
            mLoadingView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            ArrayList<RecommandValue> recommandValues = new ArrayList<>();
            recommandValues.addAll(recommandData.data.list);
            recommandValues.addAll(recommandData.data.list);
            mAdapter = new RecommendAdapter(mContext, recommandValues, mListView);
            mController.setData(recommandData.data.head.ads);
            mListView.addHeaderView(mController.getContentView());
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }

    }


    private void initView() {

        mQRCodeView = (TextView) mContentView.findViewById(R.id.qrcode_view);
        mQRCodeView.setOnClickListener(this);
        mCategoryView = (TextView) mContentView.findViewById(R.id.category_view);
        mCategoryView.setOnClickListener(this);
        mSearchView = (TextView) mContentView.findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mListView = (ListView) mContentView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mLoadingView = (ImageView) mContentView.findViewById(R.id.loading_view);

        //启动动画
        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
        anim.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qrcode_view:
                if (hasPermission(Constant.CAMERA_PERMISSION)) {
                    doOpenCamera();
                } else {
                    requestPermission(Constant.CARMER_CODE, Constant.CAMERA_PERMISSION);
                }
                break;
        }
    }

    @Override
    protected void doOpenCamera() {
        Intent intent = new Intent(mContext, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_QRCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_QRCODE:
                if (resultCode == Activity.RESULT_OK) {
                    String code = data.getStringExtra("SCAN_RESULT");
                    Log.d(TAG, "onActivityResult:----> " + code);
                    if (code.contains("http") || code.contains("https")) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("url", code);
                        startActivity(intent);
                    } else if ((!code.contains("http") || !code.contains("https")) && code != null && TextUtils.isEmpty(code)) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra("url", code);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "无法解析", Toast.LENGTH_SHORT).show();

                    }
                }
                if (resultCode == 300) {
                    String code = data.getStringExtra("result");
                    Log.d(TAG, "onActivityResult:---->result " + code);
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("url", code);
                    startActivity(intent);
                }
                if (requestCode == 200) {
                    String code = data.getStringExtra("result");
                    Log.d(TAG, "onActivityResult:---->result " + code);
                }
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecommandValue value = (RecommandValue) mAdapter.getItem(position - mListView.getHeaderViewsCount());
        if (value.type != 0) {
            Intent intent = new Intent(mContext, PhotoViewActivity.class);
            intent.putStringArrayListExtra(PhotoViewActivity.PHOTO_LIST, value.url);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mAdapter != null)
//            mAdapter.destoryVideoView();
    }
}
