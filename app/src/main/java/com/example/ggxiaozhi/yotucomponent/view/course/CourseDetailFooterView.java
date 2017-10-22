package com.example.ggxiaozhi.yotucomponent.view.course;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.CourseDetailActivity;
import com.example.ggxiaozhi.yotucomponent.module.course.footer.CourseFooterDateValue;
import com.example.ggxiaozhi.yotucomponent.module.course.footer.CourseFooterRecommandValue;
import com.example.ggxiaozhi.yotucomponent.module.course.footer.CourseFooterValue;
import com.example.ggxiaozhi.yotucomponent.util.DateFormatHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.module.course
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：详情页底部布局
 */

public class CourseDetailFooterView extends RelativeLayout{
    private Context mContext;
    /**
     * UI
     */
    private RelativeLayout mRootView;
    /**
     * 图表相关UI
     */
    private LinearLayout contentLayout;
    private LineChart lineView;
    private Resources resource;
    private float yAxisMax = -1.0f;
    private float yAxisMin = 100.0f;
    private float yAxisGap = 10f;
    private int yAxislabelNum = 5;
    /**
     * 推荐相关UI
     */
    private ImageView[] mImageViews = new ImageView[2];
    private TextView[] mNameViews = new TextView[2];
    private TextView[] mPriceViews = new TextView[2];
    private TextView[] mZanViews = new TextView[2];
    /**
     * data
     */
    private ImageLoaderManager mImageLoader;
    private CourseFooterValue mFooterValue;

    public CourseDetailFooterView(Context context, CourseFooterValue footerValue) {
        this(context, null, footerValue);
    }

    public CourseDetailFooterView(Context context, AttributeSet attrs, CourseFooterValue footerValue) {
        super(context, attrs);
        mContext = context;
        resource = mContext.getResources();
        mImageLoader = ImageLoaderManager.getInstance(mContext);
        mFooterValue = footerValue;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mRootView = (RelativeLayout) inflater.inflate(R.layout.listview_course_comment_footer_layout, this);
        contentLayout = (LinearLayout) mRootView.findViewById(R.id.line_Layout);
        mImageViews[0] = (ImageView) mRootView.findViewById(R.id.image_one_view);
        mImageViews[1] = (ImageView) mRootView.findViewById(R.id.image_two_view);
        mNameViews[0] = (TextView) mRootView.findViewById(R.id.name_one_view);
        mNameViews[1] = (TextView) mRootView.findViewById(R.id.name_two_view);
        mPriceViews[0] = (TextView) mRootView.findViewById(R.id.price_one_view);
        mPriceViews[1] = (TextView) mRootView.findViewById(R.id.price_two_view);
        mZanViews[0] = (TextView) mRootView.findViewById(R.id.zan_one_view);
        mZanViews[1] = (TextView) mRootView.findViewById(R.id.zan_two_view);

        for (int i = 0; i < mFooterValue.recommand.size(); i++) {
            final CourseFooterRecommandValue value = mFooterValue.recommand.get(i);
            mImageLoader.displayImage(mImageViews[i], value.imageUrl);
            mImageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CourseDetailActivity.class);
                    intent.putExtra(CourseDetailActivity.COURSE_ID, value.courseId);
                    mContext.startActivity(intent);
                }
            });
            mNameViews[i].setText(value.name);
            mPriceViews[i].setText(value.price);
            mZanViews[i].setText(value.zan);
        }
        contentLayout.setVisibility(GONE);
//        addLineChartView();
    }

    /**
     * 获取最大最小值
     *
     * @param currentNum
     */
    private void initMaxMin(float currentNum) {
        if (currentNum >= yAxisMax) {
            yAxisMax = currentNum;
        }
        if (currentNum < yAxisMin) {
            yAxisMin = currentNum;
        }
    }

    /**
     * 绘制图表
     *学习博客 http://blog.csdn.net/hejjunlin/article/details/51774964
     */
    private void addLineChartView() {
        lineView = new LineChart(mContext);
        lineView.setDescription("");
        lineView.setScaleEnabled(false);
        lineView.getAxisRight().setEnabled(true);
        lineView.setDrawGridBackground(false);
        lineView.setTouchEnabled(false);
        lineView.getLegend().setEnabled(false);
        lineView.setHardwareAccelerationEnabled(true);

        ArrayList<Entry> yRawData = new ArrayList<>();
        ArrayList<String> xRawDatas = new ArrayList<>();
        int index = 0;
        for (int i = mFooterValue.time.size() - 1; i >= 0; i--) {
            if (mFooterValue.time.get(i) != null) {
                CourseFooterDateValue value = mFooterValue.time.get(i);
                yRawData.add(new Entry(Float.parseFloat(value.count), index));
                xRawDatas.add(DateFormatHelper.formatDate(value.dt
                        .concat("000"), DateFormatHelper.DateFormat.DATE_1));
                index++;
                initMaxMin(Float.parseFloat(value.count));
            }
        }
        /**
         * x轴样式设置
         */
        XAxis xAxis = lineView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);// 设置x轴在底部显示
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setXOffset(0); // x轴间距
        xAxis.setTextColor(resource.getColor(R.color.color_999999));
        xAxis.setAxisLineColor(resource.getColor(R.color.color_dddddd));
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(resource.getColor(R.color.color_dddddd));
        /**
         * y轴样式设置
         */
        YAxis leftAxis = lineView.getAxisLeft();
        leftAxis.resetAxisMinValue();
        leftAxis.setLabelCount(yAxislabelNum, true);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setTextColor(resource.getColor(R.color.color_999999));
        leftAxis.setAxisLineColor(resource.getColor(R.color.color_dddddd));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(resource.getColor(R.color.color_dddddd));
        leftAxis.setAxisMaxValue(yAxisMax + yAxisGap); // 设置Y轴最大值
        leftAxis.setAxisMinValue(yAxisMin - yAxisGap);// 设置Y轴最小值

        YAxis rightAxis = lineView.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisLineColor(resource.getColor(R.color.color_dddddd));


        /**
         * 曲线样式设置
         */
        LineDataSet set = new LineDataSet(yRawData, "");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setLineWidth(2.0f);
        set.setColor(resource.getColor(R.color.color_fd4634));
        set.setCircleSize(3.0f);
        set.setCircleColor(resource.getColor(R.color.color_fd4634));
        set.setFillColor(resource.getColor(R.color.color_fd4634));
        set.setFillAlpha(128);
        set.setDrawFilled(true);
        set.setDrawValues(false);

        LineData data = new LineData(xRawDatas, set);
        lineView.setData(data);
        lineView.invalidate();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        contentLayout.addView(lineView, params);
    }
}
