package com.example.ggxiaozhi.yotucomponent.module.recommend;

import com.example.ggxiaozhi.minesdk.module.monitor.Monitor;
import com.example.ggxiaozhi.minesdk.module.monitor.emevent.EMEvent;
import com.example.ggxiaozhi.yotucomponent.user.BaseModel;

import java.util.ArrayList;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.module
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/11
 * 功能   ：搜索实体类
 */
public class RecommandValue extends BaseModel {

    public int type;
    public String logo;
    public String title;
    public String info;
    public String price;
    public String text;
    public String site;
    public String from;
    public String zan;
    public ArrayList<String> url;

    //视频专用
    public String thumb;
    public String resource;
    public String resourceID;
    public String adid;
    public ArrayList<Monitor> startMonitor;
    public ArrayList<Monitor> middleMonitor;
    public ArrayList<Monitor> endMonitor;
    public String clickUrl;
    public ArrayList<Monitor> clickMonitor;
    public EMEvent event;

}
