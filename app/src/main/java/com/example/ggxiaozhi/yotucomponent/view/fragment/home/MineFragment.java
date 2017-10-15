package com.example.ggxiaozhi.yotucomponent.view.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.view.fragment.BaseFragment;

public class MineFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        return view;
    }

}
