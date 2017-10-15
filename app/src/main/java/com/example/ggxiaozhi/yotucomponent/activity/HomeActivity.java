package com.example.ggxiaozhi.yotucomponent.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.activity.base.BaseAvtivity;
import com.example.ggxiaozhi.yotucomponent.view.fragment.home.HomeFragment;
import com.example.ggxiaozhi.yotucomponent.view.fragment.home.MessageFragment;
import com.example.ggxiaozhi.yotucomponent.view.fragment.home.MineFragment;

public class HomeActivity extends BaseAvtivity implements View.OnClickListener {

    private FragmentManager fm;
    private HomeFragment mHomeFragment;
    private Fragment mCommonFragmentOne;
    private MessageFragment mMessageFragment;
    private MineFragment mMineFragment;
    private Fragment mCurrent;

    private RelativeLayout mHomeLayout;
    private RelativeLayout mMessageLayout;
    private RelativeLayout mMineLayout;
    private TextView mHomeView;
    private TextView mPondView;
    private TextView mMessageView;
    private TextView mMineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);


        initView();
        //初始化fragment
        initFragment();
    }

    private void initFragment() {
        mHomeFragment = new HomeFragment();
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        /*replace()将所有栈中的Fragment移除在添加 保证栈中只存在这一个Fragmnet实例*/
        fragmentTransaction.replace(R.id.content_layout, mHomeFragment);//将mHomeFragment加入到content_layout布局中
        fragmentTransaction.commit();
    }


    /**
     * 这里为什么不使用注解工具
     * 在一些大型应用中 使用注解工具1、对性能有些消耗 2导致错误后我们很难排查
     * 但一般应用(小应用还是比较建议使用注解框架的)
     */
    private void initView() {
        mHomeLayout = (RelativeLayout) findViewById(R.id.home_layout_view);
        mHomeLayout.setOnClickListener(this);
        mMessageLayout = (RelativeLayout) findViewById(R.id.message_layout_view);
        mMessageLayout.setOnClickListener(this);
        mMineLayout = (RelativeLayout) findViewById(R.id.mine_layout_view);
        mMineLayout.setOnClickListener(this);

        mHomeView = (TextView) findViewById(R.id.home_image_view);
        mMessageView = (TextView) findViewById(R.id.message_image_view);
        mMineView = (TextView) findViewById(R.id.mine_image_view);
        mHomeView.setBackgroundResource(R.drawable.comui_tab_home_selected);
    }

    /**
     * 隐藏 Fragment
     *
     * @param ft
     * @param fragment
     */
    private void hideFragment(FragmentTransaction ft, Fragment fragment) {
        if (fragment != null) {
            ft.hide(fragment);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.home_layout_view:
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home_selected);
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message);
                mMineView.setBackgroundResource(R.drawable.comui_tab_person);

                hideFragment(transaction, mMessageFragment);
                hideFragment(transaction, mMineFragment);
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.content_layout,mHomeFragment);
                } else {
                    transaction.show(mHomeFragment);
                }
                break;
            case R.id.message_layout_view:
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home);
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message_selected);
                mMineView.setBackgroundResource(R.drawable.comui_tab_person);
                hideFragment(transaction, mHomeFragment);
                hideFragment(transaction, mMineFragment);
                if (mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                    transaction.add(R.id.content_layout,mMessageFragment);
                } else {
                    transaction.show(mMessageFragment);
                }
                break;
            case R.id.mine_layout_view:
                mHomeView.setBackgroundResource(R.drawable.comui_tab_home);
                mMessageView.setBackgroundResource(R.drawable.comui_tab_message);
                mMineView.setBackgroundResource(R.drawable.comui_tab_person_selected);
                hideFragment(transaction, mMessageFragment);
                hideFragment(transaction, mHomeFragment);
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    transaction.add(R.id.content_layout,mMineFragment);
                } else {
                    transaction.show(mMineFragment);
                }
                break;
        }
        transaction.commit();
    }
}
