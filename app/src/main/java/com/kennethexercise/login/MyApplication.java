package com.kennethexercise.login;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kennethyeh on 2016/10/26.
 */

public class MyApplication extends Application {

    // Universal-Image-Loader
    public static DisplayImageOptions options;
    public static ImageLoader imageLoader ;
    public static ImageLoadingListener animateFirstListener ;

    private static final String FLURRY_APIKEY = "2WT3XV9QQGJRWVQ44DJ7";
    public static final String FLURRY_ADSPACE = "Kenneth Test";

    @Override
    public void onCreate() {
        super.onCreate();

        initialImageLoader();
    }

    private void initialImageLoader(){
        // ImageLoader 初始設定
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
//				.displayer(new RoundedBitmapDisplayer(20))
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//				.memoryCacheExtraOptions(480, 800)
                .memoryCacheExtraOptions(720, 1028)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                //.memoryCacheSizePercentage(13) // default
                //.diskCacheSize(50 * 1024 * 1024) // 缓冲大小
                .diskCacheFileCount(100) // 缓冲文件数目
                .defaultDisplayImageOptions(options)
                //.writeDebugLogs()
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        animateFirstListener = new AnimateFirstDisplayListener();
    }

    class AnimateFirstDisplayListener  extends SimpleImageLoadingListener {

        List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                } else {
                    imageView.setImageBitmap(loadedImage);
                }
                displayedImages.add(imageUri);
            }
        }
    }
}
