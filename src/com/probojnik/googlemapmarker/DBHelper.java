package com.probojnik.googlemapmarker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "DataBase", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE markers (_id INTEGER PRIMARY KEY AUTOINCREMENT, latitude DOUBLE, longitude DOUBLE, title TEXT, snippet TEXT, mid TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}