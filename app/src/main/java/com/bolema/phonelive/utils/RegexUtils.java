package com.bolema.phonelive.utils;

/**
 * 正则表达式
 * Created by yuanshuo on 2017/2/16.
 */

public class RegexUtils {

//     提取日期并且将-替换成日或月

    public static String getData(String data) {
        return data.substring(5).replace("-", "月") + "日";
    }

    //    提取播放時長
    public static String getPlayTime(String time) {
        String temp =  time.replace("小时",":").replace("时",":").replace("分",":").replace("秒","");
        String temps[] = temp.split(":");
        for (int i=0; i<temps.length; i++) {
            if (temps[i].length() == 1) {
                temps[i] = "0" + temps[i];
            }
        }
        if (temps.length == 1) {
            return "00:00:" + temps[0];
        } else if (temps.length == 2) {
            return "00:" + temps[0] + ":" + temps[1];
        } else if (temps.length == 3) {
            return temps[0] + ":" + temps[1] + ":" + temps[2];
        } else {
            return null;
        }
    }
}
