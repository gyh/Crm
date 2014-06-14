package com.gyh.crm.app.common;

/**
 * Created by guoyuehua on 14-5-31.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERTIME = "usertime";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONENUMBER = "phonenumber";
    public static final String KEY_USERLEVEL = "userlevel";
    public static final String KEY_USEREV = "userev";
    public static final String KEY_USERRECORD = "userrecord";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "userCRM";
    private static final String DATABASE_TABLE_USERLIST = "crmuserlist";
    private static final String DATABASE_TABLE_RECORDLIST = "crmrecordlist";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE_TABLE_USERLIST =
                     "CREATE TABLE " + DATABASE_TABLE_USERLIST + "(" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_USERTIME + " TEXT, " + KEY_USERNAME + " TEXT, " + KEY_PHONENUMBER + " TEXT, "
                    + KEY_USERLEVEL + " TEXT, " + KEY_USEREV + " TEXT, " + KEY_USERRECORD + " TEXT);";
    private static final String DATABASE_CREATE_TABLE_RECORDLIST =
                    "CREATE TABLE " + DATABASE_TABLE_RECORDLIST + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + KEY_USERTIME + " TEXT, " + KEY_USERNAME + " TEXT, " + KEY_PHONENUMBER + " TEXT, "
                    + KEY_USERRECORD + " TEXT);";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_TABLE_USERLIST);
            db.execSQL(DATABASE_CREATE_TABLE_RECORDLIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_USERLIST);
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_RECORDLIST);
            onCreate(db);
        }
    }
//---打开数据库---

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }
//---关闭数据库---

    public void close() {
        DBHelper.close();
    }
//---向数据库中插入一个标题---

    public boolean insertUser(String usertime, String username, String phonenumber,
                              String userlevel, String userev, String userrecord) {
        if (checkUser(phonenumber)) {
            return false;
        } else {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_USERTIME, usertime);
            initialValues.put(KEY_USERNAME, username);
            initialValues.put(KEY_PHONENUMBER, phonenumber);
            initialValues.put(KEY_USERLEVEL, userlevel);
            initialValues.put(KEY_USEREV, userev);
            initialValues.put(KEY_USERRECORD, userrecord);
            db.insert(DATABASE_TABLE_USERLIST, null, initialValues);
            initialValues.clear();
            return true;
        }
    }

    public long insertRecord(String usertime, String phonenumber, String userrecord) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERTIME, usertime);
        initialValues.put(KEY_PHONENUMBER, phonenumber);
        initialValues.put(KEY_USERRECORD, userrecord);
        return db.insert(DATABASE_TABLE_RECORDLIST, null, initialValues);
    }
//---删除一个指定标题---

    public boolean deleteUser(String phonenumber) {
        if (db.delete(DATABASE_TABLE_USERLIST, KEY_PHONENUMBER + "=" + phonenumber, null) > 0 ||
                db.delete(DATABASE_TABLE_RECORDLIST, KEY_PHONENUMBER + "=" + phonenumber, null) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteRecord(String phonenumber) {
        if (db.delete(DATABASE_TABLE_RECORDLIST, KEY_PHONENUMBER + "=" + phonenumber, null) > 0) {
            return true;
        } else {
            return false;
        }
    }


//---检索所有标题---

    public Cursor getUserList() {
        return db.query(DATABASE_TABLE_USERLIST, new String[]{
                        KEY_USERTIME,
                        KEY_USERNAME,
                        KEY_PHONENUMBER,
                        KEY_USERLEVEL,
                        KEY_USEREV,
                        KEY_USERRECORD},
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getRecordList(String phonenumber) {
        return db.query(DATABASE_TABLE_RECORDLIST, new String[]{
                        KEY_USERTIME,
                        KEY_PHONENUMBER,
                        KEY_USERRECORD},
                KEY_PHONENUMBER + "=" + phonenumber,
                null,
                null,
                null,
                null,
                null
        );
    }
//---检索一个指定标题---

    public Cursor getUser(String phonenumber) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE_USERLIST, new String[]{
                        KEY_USERTIME,
                        KEY_PHONENUMBER,
                        KEY_USERNAME,
                        KEY_USERLEVEL,
                        KEY_USEREV,
                        KEY_USERRECORD},
                KEY_PHONENUMBER + "=" + phonenumber,
                null,
                null,
                null,
                null,
                null
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean checkUser(String phonenumber) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE_USERLIST, new String[]{
                        KEY_PHONENUMBER
                },
                KEY_PHONENUMBER + "=" + phonenumber,
                null,
                null,
                null,
                null,
                null
        );
        if (mCursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //---更新一个标题---
    public boolean updateUser(String usertime, String username, String phonenumber,
                              String userlevel, String userev, String userrecord) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERTIME, usertime);
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PHONENUMBER, phonenumber);
        initialValues.put(KEY_USERLEVEL, userlevel);
        initialValues.put(KEY_USEREV, userev);
        initialValues.put(KEY_USERRECORD, userrecord);
        return db.update(DATABASE_TABLE_USERLIST, initialValues,
                KEY_PHONENUMBER + "=" + phonenumber, null) > 0;
    }
}