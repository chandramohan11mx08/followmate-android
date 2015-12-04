package com.example.chandramohanr.followmate.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.chandramohanr.followmate.app.Constants.AppConstants;
import com.example.chandramohanr.followmate.app.network.ContactProviderContract;

public class ContactsDbHelper extends SQLiteOpenHelper {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";

    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE " + ContactProviderContract.TABLE_NAME + " (" +
                    ContactProviderContract.CONTACT_ID + " INTEGER PRIMARY KEY," +
                    ContactProviderContract.DISPLAY_NAME + TYPE_TEXT + COMMA_SEP +
                    ContactProviderContract.MOBILE_NUMBER  + TYPE_TEXT + ")";

    /** SQL statement to drop "entry" table. */
    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + ContactProviderContract.TABLE_NAME;

    public ContactsDbHelper(Context context) {
        super(context, AppConstants.DATABASE_NAME, null, AppConstants.DATABASE_VERSION);
    }

//    public ContactsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

//    public ContactsDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
//        super(context, name, factory, version, errorHandler);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONTACTS);
        onCreate(db);
    }
}
