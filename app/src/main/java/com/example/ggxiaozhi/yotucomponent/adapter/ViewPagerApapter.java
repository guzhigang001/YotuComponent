package com.example.ggxiaozhi.yotucomponent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.CourseDetailActivity;
import com.example.ggxiaozhi.yotucomponent.module.recommend.RecommandValue;

import java.util.List;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.adapter
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/13
 * 功能   ：无限ViewPager适配器
 */

public class ViewPagerApapter extends PagerAdapter {

    private List<RecommandValue> mValues;
    private Context mContext;
    private LayoutInflater mInflater;

    private ImageLoaderManager mLoaderManager;

    public ViewPagerApapter(List<RecommandValue> values, Context context) {
        this.mContext = context;
        this.mValues = values;
        this.mInflater = LayoutInflater.from(context);
        this.mLoaderManager = ImageLoaderManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final RecommandValue value = mValues.get(position % mValues.size());
        View rootView = mInflater.inflate(R.layout.item_hot_product_pager_layout, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_view);
        TextView infoView = (TextView) rootView.findViewById(R.id.info_view);
        TextView gonggaoView = (TextView) rootView.findViewById(R.id.gonggao_view);
        TextView saleView = (TextView) rootView.findViewById(R.id.sale_num_view);
        ImageView[] imageViews = new ImageView[3];
        imageViews[0] = (ImageView) rootView.findViewById(R.id.image_one);
        imageViews[1] = (ImageView) rootView.findViewById(R.id.image_two);
        imageViews[2] = (ImageView) rootView.findViewById(R.id.image_three);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CourseDetailActivity.class);
                intent.putExtra(CourseDetailActivity.COURSE_ID, value.adid);
                mContext.startActivity(intent);
            }
        });

        titleView.setText(value.title);
        infoView.setText(value.price);
        gonggaoView.setText(value.info);
        saleView.setText(value.text);
        for (int i = 0; i < imageViews.length; i++) {
            mLoaderManager.displayImage(imageViews[i], value.url.get(i));
        }
        container.addView(rootView);
        return rootView;
    }


}
