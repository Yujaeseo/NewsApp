package com.example.antena.myapplication.loginview;

import com.example.antena.myapplication.mainview.Item;
import com.example.antena.myapplication.wordview.Word;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userName;
    private String userEmail;
    private List<Word> vocabulary = new ArrayList<>();
    private List<Item> articles = new ArrayList<>();

    public User(){}

    public User (String userName,String userEmail,List<Word> vocabulary,List<Item> articles){
        this.userName = userName;
        this.userEmail = userEmail;
        this.vocabulary = vocabulary;
        this.articles = articles;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public List<Word> getVocabulary() {
        return vocabulary;
    }

    public List<Item> getArticles() {
        return articles;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setVocabulary(List<Word> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setArticles(List<Item> articles) {
        this.articles = articles;
    }
}
