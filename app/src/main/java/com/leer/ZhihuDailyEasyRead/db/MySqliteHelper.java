package com.leer.ZhihuDailyEasyRead.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leer on 2017/7/1.
 */

public class MySqliteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "zhihu.db";
    public static final int DB_VERSION = 1;

    public static final int LATEST_COLUMN = Integer.MAX_VALUE;

    public static final String TABLE_NAME = "zhihu_news";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(date Integer primary key,json text)";


    public MySqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
