package com.example.antena.myapplication.wordview;

public class Wordcard {

    private String word;
    private String meaning;
    private int count;

    public Wordcard (String word,String meaning,int count){
        this.word = word;
        this.meaning = meaning;
        this.count=count;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public int getCount() {
        return count;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
