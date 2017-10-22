package com.example.ggxiaozhi.yotucomponent.module.course;

import com.example.ggxiaozhi.minesdk.module.AdValue;
import com.example.ggxiaozhi.yotucomponent.user.BaseModel;

import java.util.ArrayList;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.module.course
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：详情页头部数据源
 */

public class CourseHeaderValue extends BaseModel {

    public ArrayList<String> photoUrls;
    public String text;
    public String name;
    public String logo;
    public String oldPrice;
    public String newPrice;
    public String zan;
    public String scan;
    public String hotComment;
    public String from;
    public String dayTime;
    public AdValue video;
}
