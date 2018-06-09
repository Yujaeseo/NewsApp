package com.example.antena.myapplication;

public class Item {

    public static final int IMAGE_TYPE = 1;
    public static final int TEXT_TYPE = 2;

    private String topic;
    private String title;
    private String pubdate;
    private String press;
    private String thumbnail;
    private String newsLink;
    private String summary;
    private String author;
    private int viewType;

    //https://stackoverflow.com/questions/48405839/com-google-firebase-database-databaseexception
    public Item (){}

    public Item (String topic, String title, String pubdate, String press, String thumbnail, String url,String summary,String author, int viewType){

        this.topic = topic;
        this.title = title;
        this.pubdate = pubdate;
        this.press = press;
        this.thumbnail = thumbnail;
        this.newsLink = url;
        this.summary = summary;
        this.author = author;
        this.viewType = viewType;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setNewsLink(String newlink) {
        this.newsLink = newlink;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setauthor(String author) {
        this.author = author;
    }
    public String getTopic() {
        return topic;
    }

    public String getTitle(){
        return title;
    }

    public String getPubdate(){
        return pubdate;
    }

    public String getPress() {
        return press;
    }

    public String getThumbnail(){
        return thumbnail;
    }
    public String getNewsLink(){
        return newsLink;
    }
    public int getViewType (){
        return viewType;
    }

    public String getSummary() {
        return summary;
    }

    public String getauthor() {
        return author;
    }
}
