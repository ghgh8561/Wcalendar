package com.wcalendar.klp.wcalendar;


import android.provider.BaseColumns;

public class Database {

    public static final class CreateDB implements BaseColumns{
        public static final String _TABLENAME0 = "MEMO";
        public static final String _TABLENAME1 = "WIFI";
        public static final String MAC = "mac";
        public static final String TITLE = "title";
        public static final String CONTENTS = "contents";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY = "day";
        public static final String NAME = "name";
        public static final String _CREATE0 = "create table if not exists " + _TABLENAME0 + "("
                +_ID + " Integer primary key autoincrement, "
                +MAC + " text, "
                +TITLE + " text, "
                +CONTENTS + " text, "
                +YEAR + " text not null, "
                +MONTH + " text not null, "
                +DAY + " text not null );";
        public static final String _CREATE1 = "create table if not exists " + _TABLENAME1 + "("
                +_ID + " Integer primary key autoincrement, "
                +MAC + " text not null, "
                +NAME + " text not null );";
        public static final String _SELECT0 = "select _id, mac, title, contents, year, month, day from " + _TABLENAME0;
        public static final String _SELECT1 = "select _id, mac, name from " + _TABLENAME1 + ";";
        public static final String _SELECT2 = "select _id, mac, name from " + _TABLENAME1;

    }
}
