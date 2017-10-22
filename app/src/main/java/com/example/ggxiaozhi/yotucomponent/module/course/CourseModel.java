package com.example.ggxiaozhi.yotucomponent.module.course;

import com.example.ggxiaozhi.yotucomponent.module.course.footer.CourseFooterValue;
import com.example.ggxiaozhi.yotucomponent.user.BaseModel;

import java.util.ArrayList;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.module.course
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/21
 * 功能   ：
 */

public class CourseModel extends BaseModel {
    public CourseHeaderValue head;
    public CourseFooterValue footer;
    public ArrayList<CourseCommentValue> body;
}
