package com.example.jogle.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperation{
	
	private DBDataSet database = null;
	//���캯��
	public DBOperation(Context context){
		database = new DBDataSet(context);
	}
	
	public boolean save(DataSet dataSet){
		SQLiteDatabase db = database.getWritableDatabase();
		if(dataSet != null){
			ContentValues value = new ContentValues();
			value.put("position", dataSet.getPosDescription());
			value.put("time", dataSet.getTime());
			value.put("thumbnail", dataSet.getThumbnailString());
			value.put("context", dataSet.getContext());
			value.put("pic_name", dataSet.getPicName());

			db.insertOrThrow("record", null, value);
			db.close();
			return true;
		}
		else{
			return false;
		}
	}

	//ȫ����ѯ
	public List getAll(){
		List list = null;
		SQLiteDatabase db = database.getReadableDatabase();
		String sql = "select * from record";
		Cursor cursor = db.rawQuery(sql, null);
		
		list = new ArrayList();
		while(cursor.moveToNext()){
			DataSet dataSet = new DataSet();
			dataSet.setPosDescription(cursor.getString(1));
			dataSet.setTime(cursor.getString(2));
			dataSet.setThumbnail(cursor.getString(3));
			dataSet.setContext(cursor.getString(4));
			dataSet.setPicName(cursor.getString(5));
			list.add(dataSet);
		}
		cursor.close();
		db.close();
		return list;
	}

	//����
//	public boolean update(DataSet dataSet){
//		if(dataSet != null){
//			SQLiteDatabase db = database.getWritableDatabase();
//
//			ContentValues value = new ContentValues();
//			value.put("number", dataSet.getNumber());
//			value.put("name", dataSet.getName());
//			value.put("phone", dataSet.getPhone());
//			value.put("customer", dataSet.getCustomer());
//			value.put("address", dataSet.getAddress());
//			value.put("date", dataSet.getDate());
//			value.put("relationship", dataSet.getRelationship());
//			value.put("remark", dataSet.getRemark());
//			db.update("contact", value, "_id=?", new String[]{String.valueOf(dataSet.getId())});
//			db.close();
//			return true;
//		}
//		else{
//			return false;
//		}
//	}
}


