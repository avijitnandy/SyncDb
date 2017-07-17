package com.w3xplorers.syncdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL on 5/25/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "create table " + DBContact.TABLE_NAME+
            " (id integer primary key autoincrement,"+DBContact.NAME+" text,"+DBContact.STATUS+" integer);";
    private static final String DROP_TABLE = "drop table if exists "+DBContact.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DBContact.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveToLocalDatabase(String name,int sync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContact.NAME,name);
        contentValues.put(DBContact.STATUS,sync_status);
        database.insert(DBContact.TABLE_NAME,null,contentValues);

    }

    public Cursor readFromLocalDb(SQLiteDatabase database){
        String[] projections = {DBContact.NAME,DBContact.STATUS};
        return (database.query(DBContact.TABLE_NAME,projections,null,null,null,null,null));
    }

    public void updateDatabase(String name,int sync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContact.STATUS,sync_status);
        String selection = DBContact.NAME+" Like ?";
        String[] selection_args = {name};
        database.update(DBContact.TABLE_NAME,contentValues,selection,selection_args);
    }
}
