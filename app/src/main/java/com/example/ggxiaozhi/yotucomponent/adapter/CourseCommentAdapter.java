package com.example.ggxiaozhi.yotucomponent.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ggxiaozhi.minesdk.imageloader.ImageLoaderManager;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.module.course.CourseCommentValue;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.adapter
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：详情页适配器
 */

public class CourseCommentAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CourseCommentValue> mValues;
    private LayoutInflater mInflater;
    private ImageLoaderManager mLoaderManager;

    public CourseCommentAdapter(Context context, ArrayList<CourseCommentValue> values) {
        this.mContext = context;
        this.mValues = values;
        mInflater = LayoutInflater.from(context);
        mLoaderManager = ImageLoaderManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CourseCommentValue value = mValues.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_comment_layout, parent, false);
            holder.mImageView = (CircleImageView) convertView.findViewById(R.id.photo_view);
            holder.mNameView = (TextView) convertView.findViewById(R.id.name_view);
            holder.mCommentView = (TextView) convertView.findViewById(R.id.text_view);
            holder.mOwnerView = (TextView) convertView.findViewById(R.id.owner_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //填充数据
        if (value.type == 0) {
            holder.mOwnerView.setVisibility(View.VISIBLE);
        } else {
            holder.mOwnerView.setVisibility(View.GONE);
        }
        mLoaderManager.displayImage(holder.mImageView, value.logo);
        holder.mNameView.setText(value.name);
        holder.mCommentView.setText(value.text);
        return convertView;
    }

    class ViewHolder {
        CircleImageView mImageView;
        TextView mNameView;
        TextView mCommentView;
        TextView mOwnerView;
    }

}
