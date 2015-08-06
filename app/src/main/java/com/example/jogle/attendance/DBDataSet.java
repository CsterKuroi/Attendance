package com.example.jogle.attendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBDataSet extends SQLiteOpenHelper {
	
    private static final String DATABASE_NAME = "record.db";
    private static final int DATABASE_VERSION = 1;
    private static String sql = "create table record (" + "_id integer primary key autoincrement, "
            + "type integer, " + "user_id integer, " + "user_name text, " + "time text, "
            + "position text, " + "content text, " + "time_stamp text)";

	public DBDataSet(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
