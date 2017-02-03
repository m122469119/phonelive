package com.bolema.phonelive.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * 封装Gson,返回解析后的数据
 * Created by user on 2016/10/13.
 */

public class GsonTools {

    public static <T> T instance( String json,Class<T> bean) {
        Gson gson = new Gson();
        return gson.fromJson(json, (Type) bean);
    }
}
