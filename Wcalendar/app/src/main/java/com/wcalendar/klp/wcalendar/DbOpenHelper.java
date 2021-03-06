package com.wcalendar.klp.wcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDBw;
    public static SQLiteDatabase mDBr;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Database.CreateDB._CREATE0);
            db.execSQL(Database.CreateDB._CREATE1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Database.CreateDB._TABLENAME0);
            db.execSQL("DROP TABLE IF EXISTS " + Database.CreateDB._TABLENAME1);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context) {
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDBw = mDBHelper.getWritableDatabase();
        mDBr = mDBHelper.getReadableDatabase();
        return this;
    }

    public void create() {
        mDBHelper.onCreate(mDBw);
    }

    public void close() {
        mDBw.close();
    }

    public long insertMemoColumn(String mac, String title, String contents, String year, String month, String day) {
        ContentValues values = new ContentValues();
        values.put(Database.CreateDB.MAC, mac);
        values.put(Database.CreateDB.TITLE, title);
        values.put(Database.CreateDB.CONTENTS, contents);
        values.put(Database.CreateDB.YEAR, year);
        values.put(Database.CreateDB.MONTH, month);
        values.put(Database.CreateDB.DAY, day);
        return mDBw.insert(Database.CreateDB._TABLENAME0, null, values);
    }

    public Cursor memoSelectColumn(int year, int month, int day) {
        Cursor c = mDBr.rawQuery(Database.CreateDB._SELECT0 +
                " where year = " + year + " and month = " + month + " and day = " + day + ";", null);
        return c;
    }

    public Cursor memoSelectColumn2() {
        Cursor c = mDBr.rawQuery(Database.CreateDB._SELECT0 + ";", null);
        return c;
    }

    public boolean memoDeleteColumn(long id) {
        return mDBw.delete(Database.CreateDB._TABLENAME0, "_id=" + id, null) > 0;
    }

    public boolean memoUpdateColums(long id, String title, String contents, String mac) {
        ContentValues values = new ContentValues();
        values.put(Database.CreateDB.TITLE, title);
        values.put(Database.CreateDB.CONTENTS, contents);
        values.put(Database.CreateDB.MAC, mac);
        return mDBr.update(Database.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    public long insertWifiColumn(String mac, String name) {
        ContentValues values = new ContentValues();
        values.put(Database.CreateDB.MAC, mac);
        values.put(Database.CreateDB.NAME, name);
        return mDBw.insert(Database.CreateDB._TABLENAME1, null, values);
    }

    public Cursor wifiSelectColumn() {
        Cursor c = mDBr.rawQuery(Database.CreateDB._SELECT1, null);
        return c;
    }

    public Cursor wifiSelectColumn2(String mac) {
        Cursor c = mDBr.rawQuery(Database.CreateDB._SELECT2 +
                " where mac = " + mac + ";", null);
        return c;
    }

    public boolean wifiDeleteColumn(long id) {
        return mDBw.delete(Database.CreateDB._TABLENAME1, "_id=" + id, null) > 0;
    }

    public boolean wifiUpdateColumn(long id, String mac, String name) {
        ContentValues values = new ContentValues();
        values.put(Database.CreateDB.MAC, mac);
        values.put(Database.CreateDB.NAME, name);
        return mDBr.update(Database.CreateDB._TABLENAME1, values, "_id=" + id, null) > 0;
    }
}
