package com.example.domin;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class DatabaseHandler {

	private SQLiteDatabase db;
	private static final String path = "/data/data/ie.trinity.database/displayDB";
	private static final String TABLE = "dealinfo";
	
	public DatabaseHandler() {
		try {
			db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
			createTable();
		} catch (SQLiteException e) {
			//Toast.makeText(this, e.getMessage(), 1).show();
		}
	}

	public void createTable() {
		
		if(tableIsExist(TABLE) == false){
			db.execSQL("create table "+ TABLE +" ("
					+ " ID integer primary key autoincrement,"
					+ " 'title' VARCHAR(45)," + " 'iconUri' VARCHAR(45),"
					+ " 'category' VARCHAR(45)," + " 'latitude' VARCHAR(45),"
					+ " 'longitude' VARCHAR(45)," + " 'distance' VARCHAR(45) );");
		}
	}

	public void writetInfo(ArrayList<DisplayItem> itemList) {
		clearTable();
		
		for(int i = 0; i < itemList.size(); i++){
			db.execSQL("insert into " + TABLE + " ('ID', 'title', 'iconUri', 'category', 'latitude', 'longitude', 'distance') values ('" + itemList.get(i).getId() + "', '" + itemList.get(i).getTitle() + "', '" + itemList.get(i).getIconUri() + "', '" + itemList.get(i).getCategory() + "', '" + itemList.get(i).getLatitude() + "', '" + itemList.get(i).getLongitude() + "', '" + itemList.get(i).getDistance() + "');");
		}
	}

	public boolean tableIsExist(String tableName) {
		boolean result = false;
		
		if (tableName == null) {
			return false;
		}
		
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public ArrayList<DisplayItem> getItemList(){
		ArrayList<DisplayItem> itemList = new ArrayList<DisplayItem>();
		DisplayItem item = new DisplayItem();
		
		if(tableIsExist(TABLE)){
			Cursor c = db.rawQuery("select * from " + TABLE + "", null);
			
			while (c.moveToNext()) {
				for (int i = 0; i < c.getColumnCount(); i++) {
					if(i == 0){
						item.setId(c.getInt(i));
					}else if(i == 1){
						item.setTitle(c.getString(i));
					}else if(i == 2){
						item.setIconUri(c.getString(i));
					}else if(i == 3){
						item.setCategory(c.getString(i));
					}else if(i == 4){
						item.setLatitude(c.getString(i));
					}else if(i == 5){
						item.setLongitude(c.getString(i));
					}else if(i == 6){
						item.setDistance(c.getString(i));
						itemList.add(item);
					}
				}
			}
		}
		
		return itemList;
	}
	
	public void close(){
		db.close();
	}
	
	public void clearTable(){
		db.delete(TABLE, null, null);
	}
}
