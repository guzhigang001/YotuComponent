package com.example.ggxiaozhi.yotucomponent.manager;

import com.example.ggxiaozhi.yotucomponent.module.user.User;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.manager
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/19
 * 功能   ：单例管理用户信息
 */

public class UserManager {
    private static UserManager mUserManager = null;

    private User mUser;

    private UserManager() {

    }

    public static UserManager getInstance() {
        if (mUserManager == null) {
            synchronized (UserManager.class) {
                mUserManager = new UserManager();
            }
        }
        return mUserManager;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    /**
     * 判断是否登录
     *
     * @return
     */
    public boolean hasLogin() {
        return mUser == null ? false : true;
    }

    /**
     * 用户退出
     */
    public void removeUser() {
        mUser = null;
        mUserManager = null;
    }
}
