package com.bolema.phonelive.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bolema.phonelive.R;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.bitmap;
import static android.R.attr.bottomMedium;
import static android.R.attr.factor;

/**
 * Created by user on 2016/9/8.
 */
public class MyBitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private boolean haveDefaultPicture = true;

    public MyBitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public MyBitmapUtils(boolean haveDefaultPicture) {
        this.haveDefaultPicture = haveDefaultPicture;
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public Bitmap display(ImageView imageView, String url) {
        if (haveDefaultPicture) {
            // 设置默认图片
            imageView.setImageResource(R.drawable.default_pic);
        }


        Bitmap bitmap = null;
//        if ((bitmap=mMemoryCacheUtils.getMemoryCache(url))!=null){
//            imageView.setImageBitmap(bitmap);
//        } else if ((bitmap = mLocalCacheUtils.getLocalCache(url)) != null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            mNetCacheUtils.getBitmapFromNet(imageView, url);
//        }
        // 优先从内存中加载图片, 速度最快, 不浪费流量

//        bitmap = mMemoryCacheUtils.getMemoryCache(url);
//        if (bitmap != null) {
//
//            imageView.setImageBitmap(bitmap);
////            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
////            params.height = bitmap.getHeight();
////            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
////            imageView.setLayoutParams(params);
//            return bitmap;
//        }
//
//        // 其次从本地(sdcard)加载图片, 速度快, 不浪费流量
//        bitmap = mLocalCacheUtils.getLocalCache(url);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//
//            // 写内存缓存
//            mMemoryCacheUtils.setMemoryCache(url, bitmap);
//            return bitmap;
//        }

        // 最后从网络下载图片, 速度慢, 浪费流量
        mNetCacheUtils.getBitmapFromNet(imageView, url);
        return bitmap;
    }
//    public Bitmap display(ImageView imageView, String url) {
//        if (haveDefaultPicture) {
//            // 设置默认图片
//            imageView.setImageResource(R.mipmap.loading_cart);
//        }
//
//
//        Bitmap bitmap = null;
////        if ((bitmap=mMemoryCacheUtils.getMemoryCache(url))!=null){
////            imageView.setImageBitmap(bitmap);
////        } else if ((bitmap = mLocalCacheUtils.getLocalCache(url)) != null) {
////            imageView.setImageBitmap(bitmap);
////        } else {
////            mNetCacheUtils.getBitmapFromNet(imageView, url);
////        }
//        // 优先从内存中加载图片, 速度最快, 不浪费流量
//
//        bitmap = mMemoryCacheUtils.getMemoryCache(url);
//        if (bitmap != null) {
//
//            imageView.setImageBitmap(bitmap);
////            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
////            params.height = bitmap.getHeight();
////            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
////            imageView.setLayoutParams(params);
//            return bitmap;
//        }
//
//        // 其次从本地(sdcard)加载图片, 速度快, 不浪费流量
//        bitmap = mLocalCacheUtils.getLocalCache(url);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//
//            // 写内存缓存
//            mMemoryCacheUtils.setMemoryCache(url, bitmap);
//            return bitmap;
//        }
//
//        // 最后从网络下载图片, 速度慢, 浪费流量
//        mNetCacheUtils.getBitmapFromNet(imageView, url);
//        return bitmap;
//    }
    public Bitmap backBitmap(String url) {
        Bitmap bitmap = null;
        if ((bitmap = mMemoryCacheUtils.getMemoryCache(url)) != null) {
            return bitmap;
        } else if ((bitmap = mLocalCacheUtils.getLocalCache(url)) != null) {
            return bitmap;
        } else {
            return mNetCacheUtils.download(url);
        }
    }




    }
