package com.example.ggxiaozhi.yotucomponent.view.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.minesdk.video.VideoAdContext;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.PhotoViewActivity;
import com.example.ggxiaozhi.yotucomponent.module.course.CourseHeaderValue;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.view.course
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：详情页头部布局
 */

public class CourseDetailHeaderView extends RelativeLayout{
    private Context mContext;
    /**
     * UI
     */
    private RelativeLayout mRootView;
    private CircleImageView mPhotoView;
    private TextView mNameView;
    private TextView mDayView;
    private TextView mOldValueView;
    private TextView mNewValueView;
    private TextView mIntroductView;
    private TextView mFromView;
    private TextView mZanView;
    private TextView mScanView;
    private LinearLayout mContentLayout;
    private RelativeLayout mVideoLayout;
    private TextView mHotCommentView;
    private VideoAdContext mAdContext;
    /**
     * data
     */
    private CourseHeaderValue mData;
    private ImageLoaderManager mImageLoader;


    public CourseDetailHeaderView(Context context, CourseHeaderValue value) {
        this(context, null, value);
    }

    public CourseDetailHeaderView(Context context, AttributeSet attrs, CourseHeaderValue value) {
        super(context, attrs);
        mContext = context;
        mData = value;
        mImageLoader = ImageLoaderManager.getInstance(mContext);
        initView();
    }
    private void initView() {
        mRootView = (RelativeLayout) LayoutInflater.from(mContext).
                inflate(R.layout.listview_course_comment_head_layout, this);
        mPhotoView = (CircleImageView) mRootView.findViewById(R.id.photo_view);
        mNameView = (TextView) mRootView.findViewById(R.id.name_view);
        mDayView = (TextView) mRootView.findViewById(R.id.day_view);
        mOldValueView = (TextView) mRootView.findViewById(R.id.old_value_view);
        mNewValueView = (TextView) mRootView.findViewById(R.id.new_value_view);
        mIntroductView = (TextView) mRootView.findViewById(R.id.introduct_view);
        mFromView = (TextView) mRootView.findViewById(R.id.from_view);
        mContentLayout = (LinearLayout) mRootView.findViewById(R.id.picture_layout);
        mContentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putStringArrayListExtra(PhotoViewActivity.PHOTO_LIST, mData.photoUrls);
                mContext.startActivity(intent);
            }
        });
        mVideoLayout = (RelativeLayout) mRootView.findViewById(R.id.video_view);
        mZanView = (TextView) mRootView.findViewById(R.id.zan_view);
        mScanView = (TextView) mRootView.findViewById(R.id.scan_view);
        mHotCommentView = (TextView) mRootView.findViewById(R.id.hot_comment_view);

        mImageLoader.displayImage(mPhotoView, mData.logo);
        mNameView.setText(mData.name);
        mDayView.setText(mData.dayTime);
        mOldValueView.setText(mData.oldPrice);
        mOldValueView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mNewValueView.setText(mData.newPrice);
        mIntroductView.setText(mData.text);
        mFromView.setText(mData.from);
        mZanView.setText(mData.zan);
        mScanView.setText(mData.scan);
        mHotCommentView.setText(mData.hotComment);
        for (String url : mData.photoUrls) {
            mContentLayout.addView(createItem(url));
        }
        if (!TextUtils.isEmpty(mData.video.resource)) {
//            mRootView.setOnScrollChangeListener();
            mAdContext= new VideoAdContext(mVideoLayout,
                    new Gson().toJson(mData.video));
        }
    }

    private ImageView createItem(String url) {
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 150));
        params.topMargin = Utils.dip2px(mContext, 10);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageLoader.displayImage(imageView, url);
        return imageView;
    }

    public void destoryVideoView() {
        Log.d("TAG", "destoryVideoView: ");
        if (mAdContext != null)
            mAdContext.destroy();
    }
}
