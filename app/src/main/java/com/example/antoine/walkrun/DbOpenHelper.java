package com.example.antoine.walkrun;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.net.URI;

/**
 * Created by Antoine on 12/05/2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String create_table = "create table activity("+
            "ID integer primary key autoincrement, " +
            "NAME string, "+
            "PATH string,"+
            "TIME long,"+
            "DIST float,"+
            "DATE long"+
            ")";

    private static final String drop_table = "drop table activity";

    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
