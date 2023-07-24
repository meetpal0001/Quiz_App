package com.example.quiz_application;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Results.class},version = 1)
public abstract class ResultsDatabase extends RoomDatabase{
    public abstract ResultsDao resultsDao();
}
