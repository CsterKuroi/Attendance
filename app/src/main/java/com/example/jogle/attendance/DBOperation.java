package com.example.jogle.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperation{
	
	private DBDataSet database = null;

	public DBOperation(Context context){
		database = new DBDataSet(context);
	}
	
	public boolean save(DataSet dataSet){
		SQLiteDatabase db = database.getWritableDatabase();
		if(dataSet != null){
			ContentValues value = new ContentValues();
			value.put("type", dataSet.getType());
			value.put("user_id", dataSet.getUserID());
            value.put("user_name", dataSet.getUserName());
            value.put("time", dataSet.getTime());
			value.put("position", dataSet.getPosition());
			value.put("content", dataSet.getContent());
			value.put("time_stamp", dataSet.getTimeStamp());

			db.insertOrThrow("record", null, value);
			db.close();
			return true;
		}
		else{
			return false;
		}
	}

	public List getAll(){
		List list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from record";
		Cursor cursor = db.rawQuery(sql, null);
		
		list = new ArrayList();
		while(cursor.moveToNext()){
			DataSet dataSet = new DataSet();
			dataSet.setType(cursor.getInt(1));
            dataSet.setUserID(cursor.getInt(2));
            dataSet.setUserName(cursor.getString(3));
            dataSet.setTime(cursor.getString(4));
			dataSet.setPosition(cursor.getString(5));
			dataSet.setContent(cursor.getString(6));
			dataSet.setTimeStamp(cursor.getString(7));
			list.add(dataSet);
		}
		cursor.close();
		db.close();
		return list;
	}
}


