package com.example.antena.myapplication.wordview;

public class Word {

    private String meaning;
    private int count;

    public Word(){}

    public Word (String meaning,int count){
        this.meaning = meaning;
        this.count = count;
    }

    public String getMeaning() {return this.meaning; }

    public int getCount () {return this.count;}

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setCount (int count){this.count = count;}

}
