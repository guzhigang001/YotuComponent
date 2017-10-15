package com.example.ggxiaozhi.yotucomponent.constant;

import java.security.Permission;
import java.util.jar.Manifest;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent.constant
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/13
 * 功能   ：常量类
 */

public class Constant {

    public final static int SCARD_CODE = 0x01;//Scard权限请求码
    public final static String[] SCARD_PERMISSION =
            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE};//使用内存卡的权限

    public final static int CARMER_CODE = 0x02;//相机请求码
    public final static String[] CAMERA_PERMISSION =
            new String[]{android.Manifest.permission.CAMERA};//使用相机的权限


}
