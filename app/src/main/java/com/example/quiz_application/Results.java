package com.example.quiz_application;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Results which  store a key, total correct answer and total attempted questions
@Entity
public class Results {
    @PrimaryKey
    int key;
    int correct;
    int total;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
