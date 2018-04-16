package com.notely.pratiksha.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pratiksha on 4/11/18.
 */

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notely1.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "notely";
    public static final String COLUMN_SEQNUM = "seq_num";
    public static final String COLUMN_ID = "note_id";
    public static final String COLUMN_TITLE = "note_title";
    public static final String COLUMN_GIST = "note_gist";
    public static final String COLUMN_IS_FAVOURITE = "note_isFavourite";
    public static final String COLUMN_IS_STARRED = "note_isStarred";
    public static final String COLUMN_LAST_UPDATED = "note_timeUpdated";

    private static final String TABLE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "("
            + COLUMN_SEQNUM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            //+ COLUMN_ID + " TEXT UNIQUE ON CONFLICT REPLACE, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_GIST + " TEXT, "
            + COLUMN_IS_FAVOURITE + " INTEGER, "
            + COLUMN_IS_STARRED + " INTEGER, "
            + COLUMN_LAST_UPDATED + " DATETIME "
            + ");";


    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
