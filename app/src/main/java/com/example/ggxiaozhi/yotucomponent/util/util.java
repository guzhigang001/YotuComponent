package com.example.ggxiaozhi.yotucomponent.util;

import com.example.ggxiaozhi.yotucomponent.module.recommend.RecommandValue;

import java.util.ArrayList;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.util
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/13
 * 功能   ：工具类
 */

public class Util {
    //为ViewPager结构化数据
    public static ArrayList<RecommandValue> handleData(RecommandValue value) {
        ArrayList<RecommandValue> values = new ArrayList<>();
        String[] titles = value.title.split("@");
        String[] infos = value.info.split("@");
        String[] prices = value.price.split("@");
        String[] texts = value.text.split("@");
        ArrayList<String> urls = value.url;
        int start = 0;
        for (int i = 0; i < titles.length; i++) {
            RecommandValue tempValue = new RecommandValue();
            tempValue.title = titles[i];
            tempValue.info = infos[i];
            tempValue.price = prices[i];
            tempValue.text = texts[i];
            tempValue.url = extractData(urls, start, 3);
            start += 3;

            values.add(tempValue);
        }
        return values;
    }
    private static ArrayList<String> extractData(ArrayList<String> source, int start, int interval) {
        ArrayList<String> tempUrls = new ArrayList<>();
        for (int i = start; i < start + interval; i++) {
            tempUrls.add(source.get(i));
        }
        return tempUrls;
    }
}
