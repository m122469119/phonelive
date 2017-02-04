package com.bolema.phonelive.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * Created by weipeng on 16/8/16.
 */
public class LiveUtils {
    /**
     * @param fileName 歌词文件目录
     * @dw 获取歌词字符串
     */
    public static String getFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader inputReader = new InputStreamReader(fileInputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                Result += line + "\r\n";
            }
            fileInputStream.close();
            inputReader.close();
            bufReader.close();
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将歌词字符串存储到本地
     */
    public static void saveInFile(String filename, String res) {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            out.write(res.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Subscription startInterval(final Runnable runnable) {
        Subscription subscription = Observable.interval(1, 6 * 10000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        TLog.log("" + aLong);
                        runnable.run();
                    }
                });
        return subscription;
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
