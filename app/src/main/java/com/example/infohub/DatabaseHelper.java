package com.example.infohub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.invoke.CallSite;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NewsDatabase";
    private static final String TABLE_NAME = "FavoritesTable";
    public static final String COL0 = "ID";
    public static final String COL1 = "TITLE";
    public static final String COL2 = "PHOTO_URL";
    public static final String COL3 = "URL";




    public DatabaseHelper(Context context) {
        //Dont care about version so set default
        super(context, DATABASE_NAME, null, 1);
    }

    //Create the database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ( " +
                COL0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT )";
        db.execSQL(sql);
    }

    //Dont need this but required by SQLite helper
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addFavorite(ListViewDetails listViewDetails){
        SQLiteDatabase db = this.getWritableDatabase();

        //Dont need to use SQL to insert, use ContentValues
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,listViewDetails.getTitle()); //title
        contentValues.put(COL2,listViewDetails.getPicture()); //picture
        contentValues.put(COL3,listViewDetails.getLink()); //url

        //Returns the id upon insert
        long result = db.insert(TABLE_NAME, null, contentValues);
        //If insert unsuccessful
        if(result == -1){ return false; } else { return true; }

        }

    //Return data frm database
    public Cursor getFavorites(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getID(ListViewDetails listViewDetails){
        SQLiteDatabase db = this.getWritableDatabase();
        String title = listViewDetails.getTitle().replace("'","''");
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +
                COL1 + " = '" + title + "'" +
                " AND " + COL2 + " = '" + listViewDetails.getPicture() + "'";

        return db.rawQuery(sql,null);
    }

    //Refers to ID in database
    //Returns the number of rows deleted - 1 row
    public Integer delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{String.valueOf(id)});
    }

}


