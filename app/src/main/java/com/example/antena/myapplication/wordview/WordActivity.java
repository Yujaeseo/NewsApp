package com.example.antena.myapplication.wordview;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.antena.myapplication.R;
import com.example.antena.myapplication.mainview.MainActivity;

public class WordActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("단어장");


/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wordfragmentmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.range_item :{
                show();
                break;
            }

            case android.R.id.home :{
                Intent i = new Intent(WordActivity.this,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void show(){

        final Dialog d = new Dialog(this);
        d.setTitle("단어 범위 설정");
        d.setContentView(R.layout.numberpickdialog);


        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberpicker);

        Button y_button = (Button) d.findViewById(R.id.yesbutton);
        Button n_button = (Button) d.findViewById(R.id.nobutton);

        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);

        y_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터 다시 불러오기
                //WordActivityFragment fragment =  (WordActivityFragment) getFragmentManager().findFragmentById(R.id.fragment);
                //fragment.firebaseGetWord();
                d.dismiss();
            }
        });

        n_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }
}
