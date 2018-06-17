package com.example.antena.myapplication.webview;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CustomWebViewBottom extends WebView {

    private Context context;
    static  SearchView searchView;
    private String word = "";
    private String meaning = "";

    // Javascript로 단어를 가져오는 부분 ->  2가지 경우 : 단어가 여러개 존재하는 경우, 1가지 의미만 적혀있는 경우
    private String javascriptFunction =  "function getFirstMeaning () {" +
            "var tagList = document.getElementsByClassName('desc_lst')[0];" +
            "var liList = tagList.getElementsByTagName('li');" +
            "var i;" +
            "var meaningStr = '';" +
            "for (i = 0; i < liList.length; i++) {" +
            "if (liList[i].getElementsByClassName('num').length == 0){"+
            "meaningStr += liList[i].innerText;}"+
            "else{"+
            "meaningStr += liList[i].children[0].innerText + ' ' + liList[i].children[1].innerText;}" +
            "if (i != liList.length -1){" +
            "meaningStr += '\n';" +
            "}" +
            "}"+
            "return meaningStr;} ";

    private String getMeaningJavascript = "javascript:JavaScriptInterface.getMeaning ( (function() {" + javascriptFunction + "return getFirstMeaning(); }) () );";

    //https://stackoverflow.com/questions/8374016/how-to-execute-javascript-on-android
    // "Uncaught SyntaxError: missing ) after argument list 해결
    // https://stackoverflow.com/questions/45993571/highlight-the-selected-text-in-webview-android
    public CustomWebViewBottom(Context context) {
        super(context);
        this.context = context;
    }

    public CustomWebViewBottom (Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.setWebContentsDebuggingEnabled(true);
        }

        this.addJavascriptInterface(new JavaScriptInterface(), "JavaScriptInterface");

    }

    public class JavaScriptInterface {

        @JavascriptInterface
        public void getWord(String text){
            word += text;
            Log.w("test",text);
        }

        @JavascriptInterface
        public void getMeaning(String text) {
            meaning += text;
            Log.w("test",text);
        }

    }

    public void executeFindwordScript (){
        // 단어를 가져오는 자바스크립트를 실행시킨다.

       this.loadUrl("javascript:JavaScriptInterface.getWord(document.querySelector(\"html body#bodyClass div#content div.entry_search_word.top div.h_word._tipSkipItem strong.target\").innerHTML)");

       //자바스크립트 li 리스트 스트링으로 가져오는 부분 구현하기
        //참고 : https://stackoverflow.com/questions/2250917/passing-a-javascript-object-using-addjavascriptinterface-on-android
        //참고 li 요소 list로 받아오기 : https://stackoverflow.com/questions/4019894/get-all-li-elements-in-array

        // 의미를 가져오는 자바스크립트를 실행시킨다.
       this.loadUrl(getMeaningJavascript);
    }


    public String getWord(){
        return this.word;
    }

    public String getMeaning(){
        return this.meaning;
    }

    public void setWordEmpty(){
        this.word = "";
    }

    public void setMeaningEmpty (){
        this.meaning = "";
    }

}

  /*
    @Override
    public boolean dispatchTouchEvent (MotionEvent motion){

        int toolbarHt = getChildAt(0).getHeight();


        if (motion.getY() <= toolbarHt && motion.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mActivePointerId = motion.getPointerId(motion.getActionIndex());
        }

        if (mActivePointerId == motion.getPointerId(motion.getActionIndex()) )
            return getChildAt(0).dispatchTouchEvent(motion);

        else {
            return super.dispatchTouchEvent(motion);
        }

    }
    */

