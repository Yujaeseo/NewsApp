package com.example.antena.myapplication;

import android.content.Context;
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
    private String wordAndMeaning;
    private String javascriptFunction =  "function getFirstMeaning () {" +
            "var tagList = document.getElementsByClassName('desc_lst')[0];" +
            "var liList = tagList.getElementsByTagName('li');" +
            "var i;" +
            "var meaningStr = '';" +
            "for (i = 0; i < liList.length; i++) {" +
            "meaningStr += liList[i].children[0].innerText + ' ' + liList[i].children[1].innerText;" +
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
        wordAndMeaning = "";
        this.context = context;
    }

    public CustomWebViewBottom (Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
        wordAndMeaning = "";
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        this.addJavascriptInterface(new JavaScriptInterface(), "JavaScriptInterface");

    }

    public class JavaScriptInterface {

        @JavascriptInterface
        public void getWord(String text){
            wordAndMeaning += text;
            Log.w("test",text);
        }

        @JavascriptInterface
        public void getMeaning(String text) {
            Log.w("test",text);
        }

    }

    public void executeFindwordScript (){
       this.loadUrl("javascript:JavaScriptInterface.getWord(document.querySelector(\"html body#bodyClass div#content div.entry_search_word.top div.h_word._tipSkipItem strong.target\").innerHTML)");
       //자바스크립트 li 리스트 스트링으로 가져오는 부분 구현하기
        //참고 : https://stackoverflow.com/questions/2250917/passing-a-javascript-object-using-addjavascriptinterface-on-android
        //참고 li 요소 list로 받아오기 : https://stackoverflow.com/questions/4019894/get-all-li-elements-in-array
       this.loadUrl(getMeaningJavascript);
    }

    public String getWordAndMeaning (){
        String temp = wordAndMeaning;
        wordAndMeaning = "";
        return temp;
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

