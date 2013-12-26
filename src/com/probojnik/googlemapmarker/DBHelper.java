package com.probojnik.googlemapmarker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "DataBase";
	public static final String DB_TABLE = "markers";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE " + DB_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				Tables.LATITUDE.getTable() + " DOUBLE, " + 
				Tables.LONGITUDE.getTable() + " DOUBLE, " + 
				Tables.TITLE.getTable() + " TEXT, " + 
				Tables.SNIPPET.getTable() + " TEXT, " + 
				Tables.MID.getTable() + " TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public enum Tables {
		LATITUDE("latitude"),
		LONGITUDE("longitude"),
		TITLE("title"),
		SNIPPET("snippet"),
		MID("mid");
		
		private final String table;

		Tables(String t) {
			table = t;
		}
		
		public String getTable() {
			return table;
		}
	}
}