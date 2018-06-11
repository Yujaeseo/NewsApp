package com.example.antena.myapplication.wordview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.antena.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class WordCardAdapter extends ArrayAdapter<Wordcard> {

    private Context mContext;
    private List<Wordcard> wordcardList = new ArrayList<>();

    public WordCardAdapter(@NonNull Context context, int resource, @NonNull List<Wordcard> list) {
        super(context, resource, list);
        mContext = context;
        wordcardList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;

        if (listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.wordcard,parent,false);
        }

        Wordcard currentCard = wordcardList.get(position);

        if (currentCard != null){

            TextView meaningView = (TextView) listItem.findViewById(R.id.word_meaning);
            TextView wordView = (TextView) listItem.findViewById(R.id.word);
            TextView countView = (TextView) listItem.findViewById(R.id.word_count);

            if (meaningView!= null)
                meaningView.setText(currentCard.getMeaning());

            if (wordView != null)
                wordView.setText(currentCard.getWord());

            if (countView != null)
                countView.setText(Integer.toString(currentCard.getCount()));

        }

        return listItem ;
    }
}
