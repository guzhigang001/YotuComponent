package com.example.ggxiaozhi.yotucomponent.view.weigth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ggxiaozhi.minesdk.utils.Utils;
import com.example.ggxiaozhi.yotucomponent.R;
import com.example.ggxiaozhi.yotucomponent.manager.UserManager;
import com.example.ggxiaozhi.yotucomponent.zxing.util.Util;

import java.io.File;
import java.io.FileOutputStream;


/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.store.the_basket.view.widget
 * 作者名 ： 志先生_
 * 日期   ： 2017/110/19
 * 功能   ：生成二维码图片Dialog
 */
public class MyQrCodeDialog extends Dialog {

    private Context mContext;
    Bitmap mBitmap;
    /**
     * UI
     */
    private ImageView mQrCodeView;
    private TextView mTickView;
    private TextView mCloseView;
    String fileName="";
    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/QNews/Image/";


    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case -1:
                    Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "无SD卡", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "已保存", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                    break;

            }
            return true;
        }
    });

    public MyQrCodeDialog(Context context) {
        super(context, 0);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mycode_layout);
        initView();
    }

    private void initView() {

        mQrCodeView = (ImageView) findViewById(R.id.qrcode_view);
        mTickView = (TextView) findViewById(R.id.tick_view);
        mCloseView = (TextView) findViewById(R.id.close_view);
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String name = UserManager.getInstance().getUser().data.name;
        mQrCodeView.setImageBitmap(Util.createQRCode(
                Utils.dip2px(mContext, 200),
                Utils.dip2px(mContext, 200),
                name));
        mQrCodeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int num=svaeImage();
                        handler.sendEmptyMessage(num);
                    }
                }).start();
                return true;
            }
        });
        mTickView.setText(name + mContext.getString(R.string.personal_info));
    }

    private int svaeImage() {
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {

            return 1;
        }
        try {
            File file = new File(dir);
            if(!file.exists())
                file.mkdirs();
//            if (FileUtils.isFileExists(file.getPath()+"/"+fileName + ".jpg"))
//                return 2;

            FileOutputStream out = new FileOutputStream(file.getPath()+"/"+fileName + ".jpg");
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();
            // MediaScannerReceiver是一个广播接收者，当它接收到特定的广播请求后，就会去扫描指定的文件，并根据文件信息将其添加到数据库中。
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(file.getPath()+"/"+fileName + ".jpg"));
            intent.setData(uri);
            mContext.sendBroadcast(intent);
            return 3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
