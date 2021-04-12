package com.example.readmylnk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class SQLHelpDB extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";

    // влияет на содержимое дб
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Note_Manager";

    private static final String TABLE_NOTE = "Note ";

    private static final String COLUMN_NOTE_ID ="Note_Id";
    private static final String COLUMN_NOTE_TITLE ="Note_Title";
    private static final String COLUMN_NOTE_CONTENT = "Note_Content";
    private static final String COLUMN_NOTE_IMAGE_CONTENT = "Note_Sub_Content";

    public SQLHelpDB(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "SQLHelpDB.onCreate ... ");
        String script = "CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT,"
                + COLUMN_NOTE_IMAGE_CONTENT + " TEXT" + ")";
        db.execSQL(script);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "SQLHelpDB.onUpgrade ... ");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);

        // Create tables again
        onCreate(db);
    }

    public void addNote(Note note) {

        if (!isEqualsNote(note)){
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
            values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
            values.put(COLUMN_NOTE_IMAGE_CONTENT, note.getNoteImageContent());

            // Inserting Row
            db.insert(TABLE_NOTE, null, values);

            // Closing database connection
            db.close();
            Log.i(TAG, "SQLHelpDB.addNote ... " + note.getNoteTitle());
        }else {
            Log.i(TAG, "SQLHelpDB.addNote ... " + "this Note now in database");
        }
    }


    public Note getNote(int id) {
        Log.i(TAG, "SQLHelpDB.getNote ... " + id);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTE, new String[] { COLUMN_NOTE_ID,
                        COLUMN_NOTE_TITLE, COLUMN_NOTE_CONTENT, COLUMN_NOTE_IMAGE_CONTENT}, COLUMN_NOTE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return note
        return new Note(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }


    public List<Note> getAllNotes() {
        Log.i(TAG, "SQLHelpDB.getAllNotes ... " );

        List<Note> noteList = new ArrayList<Note>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setNoteId(Integer.parseInt(cursor.getString(0)));
                note.setNoteTitle(cursor.getString(1));
                note.setNoteContent(cursor.getString(2));
                note.setNoteImageContent(cursor.getString(3));
                // Adding note to list
                noteList.add(note);
            } while (cursor.moveToNext());
        }

        // return note list
        return noteList;
    }

    public int getNotesCount() {
        Log.i(TAG, "SQLHelpDB.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }


    public int updateNote(Note note) {
        Log.i(TAG, "SQLHelpDB.updateNote ... "  + note.getNoteTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
        values.put(COLUMN_NOTE_IMAGE_CONTENT, note.getNoteImageContent());

        // updating row
        return db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
                new String[]{String.valueOf(note.getNoteId())});
    }

    public void deleteNote(Note note) {
        Log.i(TAG, "SQLHelpDB.updateNote ... " + note.getNoteTitle() );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, COLUMN_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.getNoteId()) });
        db.close();
    }

    public void clear(){
        List<Note> noteList = getAllNotes();
        for (int i=0;i<noteList.size();i++){
            deleteNote(noteList.get(i));
        }
    }

    public boolean isEqualsNote(Note note){
        List<Note> noteListNow = getAllNotes();
        for (int i=0;i<noteListNow.size();i++){
            if(noteListNow.get(i).getNoteContent().trim().equals(note.getNoteContent().trim())){
                return true;
            }
        }
        return false;
    }

}