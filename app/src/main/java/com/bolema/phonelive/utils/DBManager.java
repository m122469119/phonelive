package com.bolema.phonelive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bolema.phonelive.bean.LocalMusicBean.DataBean.MusicBean;
import com.bolema.phonelive.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/5.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add music
     * @param musics
     */
    public void add(List<MusicBean> musics) {
        db.beginTransaction();  //开始事务
        try {
            for (MusicBean music : musics) {
                db.execSQL("INSERT INTO music VALUES(null, ?, ?, ?,?)", new Object[]{music.getMc_name(), music.getId(), music.getSinger(),music.getSort()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void delete(MusicBean music){
        db.delete("music","songid=?",new String[]{music.getId()});
    }

    /**
     * update music's songid
     * @param music
     */
    public void updateSongId(MusicBean music) {
        ContentValues cv = new ContentValues();
        cv.put("songid", music.getId());
        db.update("music", cv, "songname = ?", new String[]{music.getMc_name()});
    }

    /**
     * query all music, return list
     * @return List<music>
     */
    public List<MusicBean> query() {
        ArrayList<MusicBean> musics = new ArrayList<MusicBean>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MusicBean music = new MusicBean();
            music.setId(c.getString(c.getColumnIndex("id")));
            music.setMc_name( c.getString(c.getColumnIndex("mc_name")));
            music.setSinger( c.getString(c.getColumnIndex("singer")));
            music.setSort( c.getString(c.getColumnIndex("sort")));
            musics.add(music);
        }
        c.close();
        return musics;
    }

    /**
     * query all music, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM music", null);
        return c;
    }
    /**
     * query all music, return cursor
     * @return  Cursor
     */
    public Cursor queryFromEncryptedSongId(String id) {
        Cursor c = db.rawQuery("SELECT * FROM music where id = ?", new String[]{id});
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
