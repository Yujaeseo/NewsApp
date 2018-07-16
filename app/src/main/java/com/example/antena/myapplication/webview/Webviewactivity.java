package com.example.antena.myapplication.webview;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.support.v7.widget.SearchView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.antena.myapplication.R;
import com.example.antena.myapplication.wordview.Word;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
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

public class Webviewactivity extends AppCompatActivity implements ObservableScrollViewCallbacks{

    private CustomToolbar customToolbar;
    private CustomWebView myWebView;
    private CustomWebViewBottom bottomWebView;
    private Toolbar toolbar;
    private RelativeLayout toolbarRelativeLayout;
    private ProgressBar progressBar;
    private ProgressBar topProgressBar;
    private AppBarLayout bottomAppBarLayout;
    private FrameLayout frameLayout;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchY = -1;

    private ActionMode.Callback modeCallback;
    private ActionMode mActionmode;

    private FrameLayout.LayoutParams params;
    private int initTopMargin;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private static final String TAG = "WebviewActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewactivity);

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
        myWebView.setScrollViewCallbacks(this);
        myWebView.setProgressBar((ProgressBar)findViewById(R.id.progressBar));
        toolbarRelativeLayout = findViewById(R.id.toolbarRelativeLayout);

        topProgressBar = findViewById(R.id.progressBar);
        topProgressBar.setMax(100);

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                topProgressBar.setVisibility(View.VISIBLE);
                topProgressBar.setProgress(newProgress);

                if (newProgress == 100){
                    topProgressBar.setVisibility(View.GONE);
                }

                super.onProgressChanged(view,newProgress);

            }
        });

        bottomWebView = findViewById(R.id.bottomWebView);

        // https://stackoverflow.com/questions/48403090/webview-not-working-properly

        // 주 화면을 구성하는 웹뷰에 클릭한 기사를 띄우고, bottom 웹뷰에는 네이버 사전을 띄운다.
        myWebView.loadUrl(url);

        bottomWebView.loadUrl("http://endic.naver.com/?sLn=kr");

        //bottomWebView.setProgressBar((ProgressBar)findViewById(R.id.bottomprogressBar));

        toolbar = findViewById(R.id.webview_activity_toolbar);
        setSupportActionBar(toolbar);
        modeCallback = new CustomActionModeCallback();

        myWebView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {

                if (mActionmode != null)
                    return false;

                mActionmode = startActionMode(modeCallback);
                v.setSelected(true);
                return true;
            }
        });

        params = (FrameLayout.LayoutParams) myWebView.getLayoutParams();
        initTopMargin = params.topMargin;
        frameLayout = findViewById(R.id.webview_frame_layout);

        //bottomview 툴바를 설정해준다.
        customToolbar = findViewById(R.id.customToolbar);
        customToolbar.setBottomWebView(bottomWebView);

        bottomAppBarLayout = findViewById(R.id.bottomAppBarLayout);

        // appbar round shape shadow 로 변경
        bottomAppBarLayout.setOutlineProvider(new ZoftinoCustomOutlineProvider(8));
        bottomAppBarLayout.setClipToOutline(true);

        progressBar = findViewById(R.id.bottomprogressBar);
        progressBar.setMax(100);

        bottomWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }

                super.onProgressChanged(view,newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());

                Toast.makeText(Webviewactivity.this,"단어 추가에 실패하였습니다.",Toast.LENGTH_LONG).show();
                CustomWebView.AddWordAvailable = 1;
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    public void saveWordAndMeaning (){

        CustomWebView.AddWordAvailable = 0;

        bottomWebView.post(new Runnable() {
            @Override
            public void run() {
                bottomWebView.executeFindwordScript();
            }
        });
        // 네이버 사전의 단어 의미와 뜻을 찾는다.
        //bottomWebView.executeFindwordScript();
        // 해당 유저 데이터베이스에 단어를 저장한다.
        //firebaseSaveWord();
    }


    public void firebaseSaveWord(final String wordName, final String wordMeaning){

        Log.w("test","here");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("test",wordName + "," + wordMeaning);

                // 단어장 존재
                if (dataSnapshot.child("word").exists()){
                    // 단어장에 이미 단어가 추가되어있는 경우
                    if (dataSnapshot.child("word").child(wordName).exists()) {

                        if (wordName.equals("") && wordMeaning.equals("")){
                            Toast.makeText(Webviewactivity.this,"단어가 인식되지 않았습니다.",Toast.LENGTH_LONG).show();
                            Log.w("test","인식되지 않음" + ":" + wordName);
                        }
                        else if(!wordName.equals("")) {
                            Toast.makeText(Webviewactivity.this,"단어장에 이미 존재합니다.",Toast.LENGTH_LONG).show();
                        }

                        CustomWebView.AddWordAvailable = 1;
                    }
                    // 단어장에 해당 단어가 없는 경우 - > 추가
                    else {
                        // ************************************************ 궁금한점 : 또 쓰레드가 분기 되나 ?? ***************************************************************************
                        firebaseAddWord(wordName,wordMeaning);
                    }
                }
                // 존재하지 않는 경우  -> 단어장 노드, 단어 추가
                else{
                    userRef.child("word").setValue(" ");
                    firebaseAddWord(wordName,wordMeaning);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 단어를 단어장에 추가
    public void firebaseAddWord (final String wordName, String wordMeaning) {


        if (wordMeaning.equals("")){
            Toast.makeText(Webviewactivity.this,"단어 뜻을 직접 입력해야 합니다.",Toast.LENGTH_LONG).show();
            // dialogue 띄우기.

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("단어 뜻을 입력해주세요.");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CustomWebView.AddWordAvailable = 1;
                }
            });

            builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                   Word word = new Word(input.getText().toString(),0);

                    userRef.child("word").child(wordName).setValue(word).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Webviewactivity.this,"내 단어장에 추가되었습니다.",Toast.LENGTH_LONG).show();
                            CustomWebView.AddWordAvailable = 1;
                        }
                    });
                }
            });

            builder.show();
        }
        else{
            Word word = new Word(wordMeaning,0);

            userRef.child("word").child(wordName).setValue(word).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Webviewactivity.this,"내 단어장에 추가되었습니다.",Toast.LENGTH_LONG).show();
                    CustomWebView.AddWordAvailable = 1;
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        /*
        toolbar.animate().cancel();

        int scrollDelta = scrollY - oldScrollY;
        oldScrollY = scrollY;

        float currentYTranslation = -toolbar.getTranslationY();
        float targetYTranslation = Math.min(Math.max(currentYTranslation + scrollDelta, 0), toolbarHeight);
        toolbar.setTranslationY(-targetYTranslation);
        */
    }

    @Override
    public void onDownMotionEvent() {
    }

    //http://www.devexchanges.info/2015/09/android-showhide-actionbar-when.html
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.w("test","scroll");

        if (scrollState == ScrollState.UP){
            if (toolbarIShown())
                hideToolbar();
        }

        else if (scrollState == ScrollState.DOWN){
            if (toolbarIsHidden()){
                showToolbar();
            }
        }

    }

    private boolean toolbarIShown(){
        return toolbarRelativeLayout.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden (){
        return toolbarRelativeLayout.getTranslationY() == - toolbarRelativeLayout.getHeight();
    }

    private void showToolbar(){
        moveToolbar(0);
    }

    private void hideToolbar(){
        moveToolbar(-toolbarRelativeLayout.getHeight());
    }

    private void moveToolbar(final float toTranslationY){

        float curTranslationY = toolbarRelativeLayout.getTranslationY();

        if (curTranslationY == toTranslationY){
            return;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(curTranslationY,toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float translationY = (float) animation.getAnimatedValue();
                toolbarRelativeLayout.setTranslationY(translationY);
            }
        });
        animator.start();

        //final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View)myWebView).getLayoutParams();

        ValueAnimator marginAnimator = ValueAnimator.ofInt(params.topMargin,(int) toTranslationY + initTopMargin).setDuration(200);
        marginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.topMargin = (Integer) animation.getAnimatedValue();
                myWebView.requestLayout();
            }
        });
        marginAnimator.start();
    }

    private class CustomActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.custommenu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){

                case R.id.searchButton:
                    myWebView.loadUrl("javascript:JavaScriptInterface.getText(window.getSelection().toString())");
                    break;

                case R.id.addWordButton:

                    if (CustomWebView.AddWordAvailable == 1)
                        Webviewactivity.this.saveWordAndMeaning();
                    else {
                        Log.w("test", "단어 추가 중");
                        Toast.makeText(Webviewactivity.this,"단어를 추가하는 중 입니다.",Toast.LENGTH_LONG).show();
                    }
                    //CustomWebView.this.loadUrl("javascript:JavaScriptInterface.getMeaning(document.querySelector(\"div#content span.fnt_e30 strong\").innerText)");
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            myWebView.clearFocus();
            mActionmode = null;
        }
    }

    public class ZoftinoCustomOutlineProvider extends ViewOutlineProvider {

        int roundCorner;

        public ZoftinoCustomOutlineProvider(int round) {
            roundCorner = round;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), roundCorner);
        }
    }
}
