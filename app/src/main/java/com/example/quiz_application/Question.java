package com.example.quiz_application;

// Question class with question and its answer
public class Question {
    String question;
    boolean ans;

    public Question(String question, boolean ans) {
        this.question = question;
        this.ans = ans;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isAns() {
        return ans;
    }

    public void setAns(boolean ans) {
        this.ans = ans;
    }
}
