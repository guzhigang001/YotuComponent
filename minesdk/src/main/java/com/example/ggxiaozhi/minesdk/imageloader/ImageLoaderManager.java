package com.example.ggxiaozhi.minesdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.ggxiaozhi.minesdk.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.imageloader
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/11
 * 功能   ：初始化UniverImageLoader,在基础上根据自己的业务逻辑封装自己的默认的姐啊在图片配置
 */

public class ImageLoaderManager {

    private static final int THREAD_COUNT = 4;//UIL图片下载的最大线程数
    private static final int PROPERTY = 2;//表明图片下载的一个优先级(文本高于图片)
    private static final int DISK_CACHE_SIZE = 50 * 1024;//图片缓存大小50M
    private static final int CONNECTION_TIME_OUT = 5 * 1000;//下载图片网络连接超时时间
    private static final int READ_TIME_OUT = 30 * 1000;//下载图片网络读取超时时间

    private static ImageLoaderManager mInstance;
    private static ImageLoader mImageLoader;

    public static ImageLoaderManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ImageLoaderManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单例模式下 构造函数为私有的
     *
     * @param context
     */
    private ImageLoaderManager(Context context) {
        /**
         * 图片下载开始前的一些设置
         */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(THREAD_COUNT)//配置图片下载的最大线程数
                .threadPriority(Thread.NORM_PRIORITY - PROPERTY)//设置图片下载线程的优先级(数值越大优先级越高)使用将低于普通线程的优先级
                .denyCacheImageMultipleSizesInMemory()//方式缓存多套尺寸到我们的内存
                .memoryCache(new WeakMemoryCache())//使用若引用缓存
                .diskCacheSize(DISK_CACHE_SIZE)//分配硬盘缓存大小
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//适应MD5命名缓存文件
                .tasksProcessingOrder(QueueProcessingType.LIFO)//图片下载顺序
                .defaultDisplayImageOptions(getDefaulDisplay())//设置iOptions
                .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))//设置图片连接 读取超时时间
                .writeDebugLogs()//debug下输出日志
                .build();

        ImageLoader.getInstance().init(config);//初始化配置
        mImageLoader = ImageLoader.getInstance();

    }

    /**
     * 图片下载时(下载之后)的一些设置
     *
     * @return
     */
    private DisplayImageOptions getDefaulDisplay() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.xadsdk_img_error)//设置加载图片为空时的图片
                .showImageOnFail(R.drawable.xadsdk_img_error)//设置加载图片错误时的图片
                .cacheInMemory(true)//设置图片可以缓存到内存
                .cacheOnDisk(true)//设置图片可以缓存到硬盘
                .decodingOptions(new BitmapFactory.Options())//图片解码配置
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片解码类型(RGB_565减小内存)
                .build();
        return imageOptions;
    }

    /**
     * 加载图片的Api
     *
     * @param imageView
     * @param url
     * @param options
     * @param loadingListener
     * @param progressListener
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener loadingListener, ImageLoadingProgressListener progressListener) {
        if (mImageLoader != null) {
            mImageLoader.displayImage(url, imageView, options, loadingListener, progressListener);
        }
    }

    public void displayImage(ImageView imageView, String url, ImageLoadingListener loadingListener) {
        displayImage(imageView, url, null, loadingListener, null);
    }

    public void displayImage(ImageView imageView, String url) {
        displayImage(imageView, url, null);
    }
}
