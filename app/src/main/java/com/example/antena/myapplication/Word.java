package com.example.antena.myapplication;

public class Word {

    private String word;
    private String meaning;

    public Word(){}

    public Word (String word,String meaning){
        this.word = word;
        this.meaning = meaning;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getWord() {
        return word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
