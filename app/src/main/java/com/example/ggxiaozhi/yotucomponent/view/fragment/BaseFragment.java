package com.example.ggxiaozhi.yotucomponent.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.ggxiaozhi.yotucomponent.constant.Constant;
import com.example.ggxiaozhi.yotucomponent.zxing.app.CaptureActivity;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.yotucomponent
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：主要是为我们所有的Fragment提供公共的行为或事件
 */

public class BaseFragment extends Fragment {

    protected Activity mContext;

    protected void requestPermission(int code, String... permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, code);
        }
    }

    /**
     * 判断是否有指定的权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permisson : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permisson)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.CARMER_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功
                    Toast.makeText(mContext, "授权相机 权限成功", Toast.LENGTH_SHORT).show();
                    doOpenCamera();
                } else {
                    //申请失败
                    Toast.makeText(mContext, "授权相机权限失败 可能会影响应用的使用", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.SCARD_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doWriteSDCard();
                }
                break;
        }
    }

    protected void doWriteSDCard() {
    }

    protected void doOpenCamera() {

    }
}
