package com.example.quiz_application;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ResultsDao {

    @Insert
    void insertResults(Results result);

    @Update
    void updateResults(Results result);

    @Delete
    void deleteResults(Results result);

    @Query("select * from results")
    List<Results> getResults();
}
