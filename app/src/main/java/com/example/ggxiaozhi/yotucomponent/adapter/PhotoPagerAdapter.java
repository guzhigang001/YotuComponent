package com.example.ggxiaozhi.yotucomponent.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.adapter
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/20
 * 功能   ：大图ViewPager适配器
 */

public class PhotoPagerAdapter extends PagerAdapter {

    private Context mContext;

    private ArrayList<String> mData;
    private ImageLoaderManager mLoader;

    public PhotoPagerAdapter(Context context, ArrayList<String> list) {
        mContext = context;
        mData = list;
        mLoader = ImageLoaderManager.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new PhotoView(mContext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        Log.d("TAG", "initData: "+mData.get(position));
        mLoader.displayImage(imageView, mData.get(position));
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
