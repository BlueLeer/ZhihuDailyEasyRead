package com.leer.ZhihuDailyEasyRead.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.leer.ZhihuDailyEasyRead.utils.UIUtils;

/**
 * Created by Leer on 2017/7/1.
 */

public class ZhihuDao {
    private static MySqliteHelper sMySqliteHelper;
    private static ZhihuDao sZhihuDao;
    private static SQLiteDatabase db;

    private ZhihuDao() {
        if (sMySqliteHelper == null) {
            sMySqliteHelper = new MySqliteHelper(UIUtils.getContext());
        }
    }

    public static ZhihuDao getInstance() {
        if (sZhihuDao == null) {
            synchronized (ZhihuDao.class) {
                sZhihuDao = new ZhihuDao();
            }
        }

        return sZhihuDao;
    }

    //增
    public void insert(int date, String json) {
        if (db == null || !db.isOpen()) {
            db = sMySqliteHelper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("json", json);
        db.insert(MySqliteHelper.TABLE_NAME, null, values);
        values.clear();
        db.close();
    }

    //查
    public String query(int date) {
        if (db == null || !db.isOpen()) {
            db = sMySqliteHelper.getWritableDatabase();
        }

        String result = null;
        Cursor cursor = db.query(MySqliteHelper.TABLE_NAME, null, "date = ?", new String[]{"" + date}, null, null, null);
        if (cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("json"));
        }
        db.close();

        return result;
    }

    //更新
    public void update(int date, String json) {
        if (db == null || !db.isOpen()) {
            db = sMySqliteHelper.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("json", json);
        db.update(MySqliteHelper.TABLE_NAME, values, "date = ?", new String[]{"" + date});

        values.clear();
        db.close();
    }

    //更新和插入(如果有这条记录就更新,如果没有就插入一条)
    public void replace(int date, String json) {
        if (db == null || !db.isOpen()) {
            db = sMySqliteHelper.getWritableDatabase();
        }
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("json", json);
        db.replace(MySqliteHelper.TABLE_NAME, null, values);

        db.close();
    }

}
