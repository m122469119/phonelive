package com.bolema.phonelive.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 操作数据库
 * Created by Administrator on 2016/5/5.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "phonelive2.db";
    private static final int DATABASE_VERSION = 2;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS music" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, songname VARCHAR, songid INTEGER, singername VARCHAR,m4a VARCHAR, downurl VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
