package com.notely.pratiksha.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pratiksha on 4/11/18.
 */

//fetches data from local storage; singleton class
public class DataManager {
    public static final String TAG = "DataManager";

    private static DataManager dataManager = null;
    private SQLiteDatabase db;
    private DataHelper dataHelper;

    public static List<Notely> allNotes;

    private DataManager(Context context){
        dataHelper = new DataHelper(context);
        allNotes = new ArrayList<>();
    }

    public static DataManager getInstance(Context context){
        if(dataManager == null){
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    public synchronized Notely insertNote(Notely note){

        ContentValues values = getContentValuesFronNotely(note);
        db = dataHelper.getWritableDatabase();
        long sequenceNumber = db.insertWithOnConflict( DataHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE );
        note.setId(sequenceNumber);
        db.close();

        if(allNotes != null){
            allNotes.add(note);
        }

        return note;
    }

    public synchronized int deleteNote(long noteId){
        db = dataHelper.getWritableDatabase();
        int numOfRows = db.delete( DataHelper.TABLE_NAME, DataHelper.COLUMN_SEQNUM + "=" + noteId, null );
        db.close();

        if(allNotes != null){
            for(Notely note: allNotes){
                if (note.getId() == (noteId)){
                    Log.d("demo", "allNotes befire size:"+allNotes.size()+ " id:"+ noteId);
                    allNotes.remove(note);
                    Log.d("demo", "allNotes after size:"+allNotes.size());
                }
            }
        }

        return numOfRows;


    }

    public synchronized int updateNote(Notely note){

        ContentValues newValues = getContentValuesFronNotely(note);
        db = dataHelper.getWritableDatabase();
        int numOfRows = db.updateWithOnConflict( DataHelper.TABLE_NAME, newValues, DataHelper.COLUMN_SEQNUM + "=" + note.getId(), null, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        if(allNotes != null){
            Iterator<Notely> it = allNotes.iterator();
            while(it.hasNext()){
                Notely notely = it.next();

                if (notely.getId() == (note.getId())){
                    notely.setTitle(note.getTitle())
                            .setGist(note.getGist())
                            .setFavourite(note.isFavourite())
                            .setStarred(note.isStarred())
                            .setLastUpdated(note.getLastUpdated());

                }
            }

        }

        return numOfRows;

    }

    public synchronized void fetchAllNotes(){
        if(allNotes == null){
            allNotes = new ArrayList<>();
        }
        allNotes.clear();
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            db = dataHelper.getReadableDatabase();
            Cursor cursor=db.query(dataHelper.TABLE_NAME, null, null, null, null, null, null);
            cursor.moveToLast();
           // Log.d("update", "count : " + cursor.getCount());
            while (cursor.isBeforeFirst() == false) {
                long noteId=cursor.getLong(0);
                String noteTitle = cursor.getString(1);
                String noteGist = cursor.getString(2);
                boolean isFav =( cursor.getInt(3)==1? true:false);
                boolean isStar = (cursor.getInt(4)==1? true:false);
                String lastUpdated = cursor.getString(5);
                Notely note = new Notely( noteTitle, noteGist, isFav, isStar, lastUpdated);
                note.setId(noteId);
                allNotes.add(note);
                cursor.moveToPrevious();
            }
            cursor.close();
            db.close();
        } catch (Exception e){

            e.printStackTrace();
        } finally {
            if (db!=null){
                if (db.isOpen()){
                    db.close();
                }
            }
        }

    }

    public Notely saveNote(Notely note, boolean isUpdate){
            if(isUpdate) {
                updateNote(note);
                return null;
            }
            return insertNote(note);

    }

    public List<Notely> getAllNotes(){
        return allNotes;
    }

    private ContentValues getContentValuesFronNotely(Notely note){

        ContentValues values = new ContentValues();
        values.put(DataHelper.COLUMN_TITLE, note.getTitle());
        values.put(DataHelper.COLUMN_GIST, note.getGist());
        values.put(DataHelper.COLUMN_IS_FAVOURITE, note.isFavourite()? 1:0);
        values.put(DataHelper.COLUMN_IS_STARRED, note.isStarred() ? 1:0);
        values.put(DataHelper.COLUMN_LAST_UPDATED, note.getLastUpdated());
        return values;

    }




}

