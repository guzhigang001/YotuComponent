package com.example.ggxiaozhi.yotucomponent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.minesdk.video.VideoAdContext;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.CourseDetailActivity;
import com.example.ggxiaozhi.yotucomponent.activity.PhotoViewActivity;
import com.example.ggxiaozhi.yotucomponent.module.recommend.RecommandValue;
import com.example.ggxiaozhi.yotucomponent.share.ShareDialog;
import com.example.ggxiaozhi.yotucomponent.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.adapter
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/12
 * 功能   ：首页条目适配器
 */

public class RecommendAdapter extends BaseAdapter {

    /**
     * 设置条目类型
     */
    private static final int CARD_COUNT = 4;//ListView题目类型数
    private static final int VIDOE_TYPE = 0x00;//视频类型
    private static final int CARD_MULIT_PIC = 0x01;//多图片
    private static final int CARD_SIGNAL_PIC = 0x02;//单个图片
    private static final int CARD_VIEW_PAGER = 0x03;//viewPager条目

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<RecommandValue> mValueList;
    private ImageLoaderManager mLoaderManager;


    private VideoAdContext mAdContext;

    private ListView mListView;

    public RecommendAdapter(Context context, List<RecommandValue> data, ListView listView) {
        this.mContext = context;
        this.mValueList = data;
        this.mListView = listView;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mLoaderManager = ImageLoaderManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return mValueList.size();
    }

    @Override
    public Object getItem(int position) {
        return mValueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override/*返回所有item的类型*/
    public int getViewTypeCount() {
        return CARD_COUNT;
    }

    /**
     * 通知Adapter使用那种item去加载数据
     */
    @Override
    public int getItemViewType(int position) {
        RecommandValue value = (RecommandValue) getItem(position);
        return value.type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RecommandValue value = (RecommandValue) getItem(position);
        int type = getItemViewType(position);
        ViewHolder holder = null;
        if (convertView == null) {
            switch (type) {
                case CARD_SIGNAL_PIC:
                    //单图数据
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_product_card_two_layout, parent, false);
                    holder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    holder.mLogoView.setTag(value.logo);
                    holder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    holder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    holder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    holder.mProductView = (ImageView) convertView.findViewById(R.id.product_photo_view);
                    holder.mProductView.setTag(value.url.get(0));
                    holder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    holder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    holder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    break;
                case CARD_MULIT_PIC:
                    //多图数据
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_product_card_one_layout, parent, false);
                    holder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    holder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    holder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    holder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    holder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    holder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    holder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    holder.mProductLayout = (LinearLayout) convertView.findViewById(R.id.product_photo_layout);

                    break;
                case VIDOE_TYPE:
                    //显示video卡片
                    holder = new ViewHolder();
                    convertView = mLayoutInflater.inflate(R.layout.item_video_layout, parent, false);
                    holder.mVieoContentLayout = (RelativeLayout)
                            convertView.findViewById(R.id.video_ad_layout);
                    holder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
                    holder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
                    holder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
                    holder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
                    holder.mShareView = (ImageView) convertView.findViewById(R.id.item_share_view);
                    String json = new Gson().toJson(value);
                    mAdContext = new VideoAdContext(holder.mVieoContentLayout, json);
                    mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            mAdContext.updateVideoScollView();
                        }
                    });

                    holder.mShareView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareDialog dialog = new ShareDialog(mContext, false);
                            dialog.setShareType(Platform.SHARE_VIDEO);
                            dialog.setShareTitle(value.title);
                            dialog.setShareTitleUrl(value.site);
                            dialog.setShareText(value.text);
                            dialog.setShareSite(value.title);
                            dialog.setShareTitle(value.site);
                            dialog.setUrl(value.resource);
                            dialog.show();

                        }
                    });
                    break;
                case CARD_VIEW_PAGER:
                    holder = new ViewHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_product_view_pager_layout, parent, false);
                    holder.mViewPager = (ViewPager) convertView.findViewById(R.id.pager);
                    ArrayList<RecommandValue> recommandValues = Util.handleData(value);
                    holder.mViewPager.setPageMargin(Utils.dip2px(mContext, 2));
                    ViewPagerApapter adaper = new ViewPagerApapter(recommandValues, mContext);
                    holder.mViewPager.setAdapter(adaper);
                    //一开始处于500的位置
                    holder.mViewPager.setCurrentItem(recommandValues.size() * 100);

                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        switch (type) {
            case CARD_SIGNAL_PIC:
                //单图数据 填充
                holder.mTitleView.setText(value.title);
                holder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                holder.mFooterView.setText(value.text);
                holder.mPriceView.setText(value.price);
                holder.mFromView.setText(value.from);
                holder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));
                //为单个ImageView加载图片
                if (holder.mLogoView != null && value.logo.equals(holder.mLogoView.getTag()))
                    mLoaderManager.displayImage(holder.mLogoView, value.logo);
                if (holder.mProductView != null && value.url.get(0).equals(holder.mProductView.getTag()))
                    mLoaderManager.displayImage(holder.mProductView, value.url.get(0));

                break;
            case CARD_MULIT_PIC:
                mLoaderManager.displayImage(holder.mLogoView, value.logo);
                holder.mTitleView.setText(value.title);
                holder.mInfoView.setText(value.info.concat(mContext.getString(R.string.tian_qian)));
                holder.mFooterView.setText(value.text);
                holder.mPriceView.setText(value.price);
                holder.mFromView.setText(value.from);
                holder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(value.zan));
                holder.mProductLayout.removeAllViews();//删除已有的View 否则在复用时会多余添加
                holder.mProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        intent.putStringArrayListExtra(PhotoViewActivity.PHOTO_LIST, value.url);
                        mContext.startActivity(intent);
                    }
                });
                for (int i = 0; i < value.url.size(); i++) {
                    View view = getImageView(value.url.get(i));
                    if (view != null && value.url.get(i).equals(view.getTag()))
                        holder.mProductLayout.addView(view);
                }
                break;
        }
        return convertView;

    }

    public void destoryVideoView() {
        Log.d("TAG", "destoryVideoView: ");
        if (mAdContext != null)
            mAdContext.destroy();
    }

    private View getImageView(String url) {
        ImageView image = new ImageView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (Utils.dip2px(mContext, 100), ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = Utils.dip2px(mContext, 5);
        image.setLayoutParams(params);
        mLoaderManager.displayImage(image, url);
        image.setTag(url);
        return image;
    }

    private static class ViewHolder {
        //所有Card共有属性
        private CircleImageView mLogoView;
        private TextView mTitleView;
        private TextView mInfoView;
        private TextView mFooterView;
        //Video Card特有属性
        private RelativeLayout mVieoContentLayout;
        private ImageView mShareView;

        //Video Card外所有Card具有属性
        private TextView mPriceView;
        private TextView mFromView;
        private TextView mZanView;
        //Card 多图特有属性
        private LinearLayout mProductLayout;
        //Card 单图特有属性
        private ImageView mProductView;
        //Card Three特有属性
        private ViewPager mViewPager;
    }
}
