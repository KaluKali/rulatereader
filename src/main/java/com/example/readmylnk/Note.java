package com.example.readmylnk;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Note implements Serializable {

    private int noteId;
    private String noteTitle;
    private String noteContent;
    private String noteImageContent;

    public Note(){

    }

    public Note(String noteTitle, String noteContent, String noteImageContent) {
        this.noteTitle= noteTitle;
        this.noteContent= noteContent;
        this.noteImageContent = noteImageContent;
    }

    public Note(int noteId, String noteTitle, String noteContent, String noteImageContent) {
        this.noteId= noteId;
        this.noteTitle= noteTitle;
        this.noteContent= noteContent;
        this.noteImageContent = noteImageContent;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }


    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteImageContent() {
        return noteImageContent;
    }

    public void setNoteImageContent(String noteImageContent) {
        this.noteImageContent = noteImageContent;
    }


    @NonNull
    @Override
    public String toString()  {
        return this.noteTitle;
    }

}
