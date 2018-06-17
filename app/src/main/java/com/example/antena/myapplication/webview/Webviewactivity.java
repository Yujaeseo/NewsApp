package com.example.antena.myapplication.webview;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.antena.myapplication.R;
import com.example.antena.myapplication.wordview.Word;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class Webviewactivity extends AppCompatActivity {

    private CustomToolbar customToolbar;
    private CustomWebView myWebView;
    private CustomWebViewBottom bottomWebView;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchY = -1;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private AsyncTaskJavascript myAsync;

    private static final String TAG = "WebviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewactivity);

        myAsync = new AsyncTaskJavascript();

        //파이어베이스 연결, 현재 접속 중인 해당 유저에 대한 reference를 가져온다.
        rootRef = FirebaseDatabase.getInstance().getReference();
        // 접속중인 유저에 대한 정보를 가져온다.
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // 해당 유저의 데이터 레퍼런스를 가져온다.
        userRef = rootRef.child("users").child(mFirebaseUser.getUid());

        // 메인액티비티에서 전달한 클릭한 news의 url을 전달받는다.
        String url = getIntent().getStringExtra("newsUrl");

        myWebView = (CustomWebView) findViewById(R.id.testWebView);
        myWebView.setSearchView((android.widget.SearchView)findViewById(R.id.searchView));
        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return false;
            }
        });

        bottomWebView = findViewById(R.id.bottomWebView);
        bottomWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                return false;
            }
        });

        // https://stackoverflow.com/questions/48403090/webview-not-working-properly

        // 주 화면을 구성하는 웹뷰에 클릭한 기사를 띄우고, bottom 웹뷰에는 네이버 사전을 띄운다.
        myWebView.loadUrl(url);
        bottomWebView.loadUrl("http://endic.naver.com/?sLn=kr");

        // 툴바를 설정해준다.
        customToolbar  = findViewById(R.id.customToolbar);
        customToolbar.setBottomWebView(bottomWebView);
        // 검색창을 설정해준다.
        //CustomWebViewBottom.searchView = findViewById(R.id.searchView);
        //CustomWebViewBottom.searchView.isSubmitButtonEnabled();

        /*
        CustomWebViewBottom.searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.w("test","wef");
                String query = (String)CustomWebViewBottom.searchView.getQuery();
                String redirectUrl = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=" + query;
                bottomWebView.loadUrl(redirectUrl);
                return false;
            }
        });*/
/*
        CustomWebViewBottom.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //String redirectUrl = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=" + query;
                //bottomWebView.loadUrl(redirectUrl);
                String redirectUrl = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=" + query;
                bottomWebView.loadUrl(redirectUrl);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //String redirectUrl = "http://endic.naver.com/search.nhn?sLn=kr&dicQuery="+newText+"&x=0&y=0&query="+newText+ "&target=endic&ie=utf8&query_utf=&isOnlyViewEE=N";
                //String redirectUrl = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=" + newText;
                //bottomWebView.loadUrl(redirectUrl);
                return true;
            }

        });
        */
        //test = findViewById(R.id.test);
        //test.setVisibility(View.GONE);
        // layoutparameter 설정해 주어야 한다.
    }


    private class MycustomView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    public void saveWordAndMeaning (){


        myAsync = new AsyncTaskJavascript();
        myAsync.execute();
        // 네이버 사전의 단어 의미와 뜻을 찾는다.
        //bottomWebView.executeFindwordScript();
        // 해당 유저 데이터베이스에 단어를 저장한다.
        //firebaseSaveWord();
    }

    public void firebaseSaveWord(){

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 단어장 존재
                if (dataSnapshot.child("word").exists()){
                    // 단어장에 이미 단어가 추가되어있는 경우
                    if (dataSnapshot.child("word").child(bottomWebView.getWord()).exists()) {
                        if (bottomWebView.getWord() == ""){
                            Toast.makeText(Webviewactivity.this,"인식되지 않는 단어입니다.",Toast.LENGTH_LONG).show();
                            Log.w("test","인식되지 않음");
                        }
                        else{
                            Toast.makeText(Webviewactivity.this,"단어장에 이미 존재합니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                    // 단어장에 해당 단어가 없는 경우 - > 추가
                    else {
                        firebaseAddWord();
                    }
                }
                // 존재하지 않는 경우  -> 단어장 노드, 단어 추가
                else{
                    userRef.child("word").setValue(" ");
                    firebaseAddWord();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bottomWebView.setWordEmpty();
        bottomWebView.setMeaningEmpty();
    }

    public void firebaseAddWord () {

        if (bottomWebView.getMeaning().equals("")){
            Toast.makeText(Webviewactivity.this,"단어 뜻을 직접 입력해야 합니다.",Toast.LENGTH_LONG).show();
            // dialogue 띄우기.

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("단어 뜻을 입력해주세요.");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                   Word word = new Word(input.getText().toString(),0);

                    userRef.child("word").child(bottomWebView.getWord()).setValue(word).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Webviewactivity.this,"내 단어장에 추가되었습니다.",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            builder.show();

        }
        else{
            Word word = new Word(bottomWebView.getMeaning(),0);

            userRef.child("word").child(bottomWebView.getWord()).setValue(word).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Webviewactivity.this,"내 단어장에 추가되었습니다.",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public class AsyncTaskJavascript extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            // 네이버 사전의 단어 의미와 뜻을 찾는다.
            bottomWebView.post(new Runnable() {
                @Override
                public void run() {
                    bottomWebView.executeFindwordScript();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 해당 유저 데이터베이스에 단어를 저장한다.
            firebaseSaveWord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myAsync != null)
            myAsync.cancel(true);
    }
}
