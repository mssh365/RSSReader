package com.myrss;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "rss.db";
    public final static int VERSION = 1;
    private static DBHelper instance = null;
    private SQLiteDatabase db;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    private void openDatabase() {
        if (db == null) {
            db = getWritableDatabase();
        }
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /** 第一次安装程序后创建数据库 */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table rss (_id integer primary key autoincrement,name text,url text,collect integer)");
        db.execSQL("INSERT INTO rss VALUES(0,'defaultRss1','http://www.rediff.com/rss/moviesreviewsrss.xml',0);");
        db.execSQL("INSERT INTO rss VALUES(1,'defaultRss2','https://www.backpackers.com.tw/forum/external.php',0);");
    }

    /** 版本升级时，先删除原有的数据库，再重新创建数据库 */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table rss if exist ");
        onCreate(db);
    }

    /** 添加一条数据 */
    public long saveLamp(RSS pro) {
        ContentValues value = new ContentValues();
        value.put("name", pro.getName());
        value.put("url", pro.getURL());
        value.put("collect", pro.getCollect());
        return db.insert("rss", null, value);
    }

    /** 根据id删除数据 */
    public int deleteLamp(int id) {
        return db.delete("rss", "_id=?", new String[] { String.valueOf(id) });
    }

    /** 根据id更新数据 */
    public int updateLamp(RSS pro, int id) {
        ContentValues value = new ContentValues();
        value.put("name", pro.getName());
        value.put("url", pro.getURL());
        value.put("collect", pro.getCollect());
        return db.update("rss", value, "_id=?", new String[] { String.valueOf(id) });
    }

    /** 查询所有数据 */
    public ArrayList<HashMap<String, Object>> getLampList() {
        openDatabase();
        Cursor cursor = db.query("rss", null, null, null, null, null, null);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", cursor.getInt(cursor.getColumnIndex("_id")));
            map.put("name", cursor.getString(cursor.getColumnIndex("name")));
            map.put("url",cursor.getString(cursor.getColumnIndex("url")));
            map.put("collect", cursor.getInt(cursor.getColumnIndex("collect")));
            list.add(map);
        }
        return list;
    }

    /** 查询有多少条记录 */
    public int getLampCount() {
        openDatabase();
        Cursor cursor = db.query("rss", null, null, null, null, null, null);
        return cursor.getCount();
    }

}