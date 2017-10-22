package com.example.ggxiaozhi.minesdk.module;


import com.example.ggxiaozhi.minesdk.module.monitor.Monitor;
import com.example.ggxiaozhi.minesdk.module.monitor.emevent.EMEvent;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.video
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/16
 * 功能   ：视频信息实体类
 */
public class AdValue implements Serializable{

    public String resourceID;
    public String adid;
    public String resource;
    public String thumb;
    public ArrayList<Monitor> startMonitor;
    public ArrayList<Monitor> middleMonitor;
    public ArrayList<Monitor> endMonitor;
    public String clickUrl;
    public ArrayList<Monitor> clickMonitor;
    public EMEvent event;
}
