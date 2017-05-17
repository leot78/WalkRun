package com.example.antoine.walkrun;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.Context;

import java.net.URI;

/**
 * Created by Antoine on 15/05/2017.
 */

public class DataBase {
    public DbOpenHelper dbOpenHelper;
    public SQLiteDatabase sqLiteDatabase;

    public DataBase(Context context){
        dbOpenHelper = new DbOpenHelper(context,"activity.db",null,1);
        sqLiteDatabase = dbOpenHelper.getWritableDatabase();
    }

    public void addActivity(String path, String name, long time, float distance,long date){
        ContentValues cv = new ContentValues();
        cv.put("PATH",path);
        cv.put("NAME", name);
        cv.put("TIME",time);
        cv.put("DIST",distance);
        cv.put("DATE",date);
        sqLiteDatabase.insert("activity",null,cv);
        Log.i("DBOPEN","add name:"+name);
        Log.i("DBOPEN","add path:"+path);
    }

    public void deleteActivity(String path){
        sqLiteDatabase.delete("activity","PATH = "+"\'"+path+"\'", null);
    }

}
