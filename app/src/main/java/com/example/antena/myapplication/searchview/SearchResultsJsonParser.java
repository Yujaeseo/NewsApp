package com.example.antena.myapplication.searchview;

import com.example.antena.myapplication.mainview.Item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsJsonParser {

    private ItemParser parser = new ItemParser();

    private class ItemParser {

        public Item parse (JSONObject jsonObject){

            if (jsonObject == null)
                return null;

            String topic = jsonObject.optString("label");
            String title = jsonObject.optString("title");
            String pubdate = jsonObject.optString("pubdate");
            long pubdate_ms = jsonObject.optLong("pubdate_ms");
            String press = jsonObject.optString("press");
            String thumbnail = jsonObject.optString("thumbnail");
            String newsLink = jsonObject.optString("link");
            String summary = jsonObject.optString("summary");
            String author = jsonObject.optString("author");
            int viewType = thumbnail.equals("None") ? 2 : 1;

            return new Item(topic,title,pubdate,pubdate_ms,press,thumbnail,newsLink,summary,author,viewType);

        }

    }

    public List<Item> parseResults (JSONObject jsonObject) {

        if (jsonObject == null)
            return null;

        List<Item> result = new ArrayList<>();
        JSONArray hits = jsonObject.optJSONArray("hits");

        if (hits == null)
            return null;

        for (int i = 0 ; i < hits.length(); ++i){
            JSONObject hit = hits.optJSONObject(i);

            if (hit == null)
                continue;

            Item article = parser.parse(hit);

            if (article == null)
                continue;

            result.add(article);
        }

        return result;

    }

}
