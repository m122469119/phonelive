package com.bolema.phonelive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.bolema.phonelive.bean.LocalMusicBean;
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
    public void add(List<LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean> musics) {
        db.beginTransaction();  //开始事务
        try {
            for (LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean music : musics) {
                db.execSQL("INSERT INTO music VALUES(null, ?, ?, ?,?,?)", new Object[]{music.getSongname(), music.getSongid(), music.getSingername(),music.getM4a(),music.getDownUrl()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void delete(LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean music){
        db.delete("music","songid=?",new String[]{String.valueOf(music.getSongid())});
    }

    /**
     * update music's songid
     * @param music
     */
    public void updateSongId(LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean music) {
        ContentValues cv = new ContentValues();
        cv.put("songid", music.getSongid());
        db.update("music", cv, "songname = ?", new String[]{music.getSongname()});
    }

    /**
     * query all music, return list
     * @return List<music>
     */
    public List<LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean> query() {
        ArrayList<LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean> musics = new ArrayList<LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean music = new LocalMusicBean.ShowapiResBodyBean.PagebeanBean.MusiclistBean();
            music.setSongid(Integer.parseInt(c.getString(c.getColumnIndex("songid"))));
            music.setSongname( c.getString(c.getColumnIndex("songname")));
            music.setSingername( c.getString(c.getColumnIndex("singername")));
            try {
                music.setDownUrl( c.getString(c.getColumnIndex("downurl")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            music.setM4a(c.getString(c.getColumnIndex("m4a")));
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
    public Cursor queryFromEncryptedDownloadUrl(String url) {
            Cursor c = db.rawQuery("SELECT * FROM music where downurl = ?", new String[]{url});
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
