package com.example.ggxiaozhi.yotucomponent.network;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.network
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/11
 * 功能   ：所有请求相关的地址
 */

public class HttpConstants {
    private static final String ROOT_URL = "http://yuexibo.top/yxbApp";
    /**
     * 请求本地产品列表
     */
    public static String PRODUCT_LIST = ROOT_URL + "/fund/search.php";

    /**
     * 本地产品列表更新时间措请求
     */
    public static String PRODUCT_LATESAT_UPDATE = ROOT_URL + "/fund/upsearch.php";

    /**
     * 登陆接口
     */
    public static String LOGIN = ROOT_URL + "/user_info.json";

    /**
     * 检查更新接口
     */
    public static String CHECK_UPDATE = ROOT_URL + "/update.json";

    /**
     * 首页产品请求接口
     */
    public static String HOME_RECOMMAND = ROOT_URL + "/home_data.json";

    /**
     * 课程详情接口
     */
    public static String COURSE_DETAIL = ROOT_URL + "/course_detail.json";
}
