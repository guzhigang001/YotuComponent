package com.example.ggxiaozhi.yotucomponent.module;

import com.example.ggxiaozhi.yotucomponent.user.BaseModel;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.module
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：极光推送消息实体，包含所有的数据字段。
 */

public class PushMessage extends BaseModel{

    // 消息类型 2表示需要登录
    public String messageType = null;
    // 连接
    public String messageUrl = null;
    // 详情内容
    public String messageContent = null;
}
