package com.example.readmylnk;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

class DBInterface {
    private SQLHelpDB db;

    DBInterface(Context context){
        db = new SQLHelpDB(context);
    }

    void addBook(@NonNull String title, @NonNull String link_book, String link_pre_img){
        Note item = new Note();
        item.setNoteId(db.getNotesCount()+1);
        item.setNoteTitle(title.trim());
        item.setNoteContent(link_book.trim());
        if (link_pre_img !=null){
            item.setNoteImageContent(link_pre_img.trim());
        }else {
            item.setNoteImageContent(null);
        }
        db.addNote(item);
    }

    List<Note> getAllBooks(){
        return db.getAllNotes();
    }
}
