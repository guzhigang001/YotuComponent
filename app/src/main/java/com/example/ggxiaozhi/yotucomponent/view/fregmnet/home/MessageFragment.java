package com.example.ggxiaozhi.yotucomponent.view.fregmnet.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.view.fregmnet.BaseFragment;


public class MessageFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_layout, container, false);
    }

}
