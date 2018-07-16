package com.example.antena.myapplication.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.antena.myapplication.R;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;

public class CustomWebView extends ObservableWebView {

    private Context context;
    private ActionMode mActionmode;
    private ActionMode.Callback mActionModeCallback;
    private SearchView searchView;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    static int AddWordAvailable = 1;

    /*
    public String Highlightscript = " <script type=\"text/javascript\">" +
            "function highlightSelection(){" +
            "var userSelection = window.getSelection();" +
            "for(var i = 0; i < userSelection.rangeCount; i++)"
            + "  highlightRange(userSelection.getRangeAt(i));" +
            "}" +
            "function highlightRange(range){"+
            "span = document.createElement(\"span\");"+
            "span.appendChild(range.extractContents());"+
            "span.setAttribute(\"style\",\"display:block;background:#ffc570;\");"+
            "range.insertNode(span);}"+
            "</script> ";
    */

    public CustomWebView(Context context){
        super(context);
        this.context = context;
    }

    public CustomWebView(Context context, AttributeSet attrs){

        super(context,attrs);
        this.context = context;
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);

        this.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                return false;
            }
        });
/*
        this.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);

                if (newProgress == 100){
                    progressBar.setVisibility(GONE);
                }

                super.onProgressChanged(view,newProgress);

            }
        });
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.setWebContentsDebuggingEnabled(true);
        }

        this.addJavascriptInterface(new JavaScriptInterface(), "JavaScriptInterface");
        Toast.makeText(context,"단어를 검색하기 위해 화면을 길게 눌러주세요.",Toast.LENGTH_LONG).show();
/*
        this.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {

                if (mActionmode != null)
                    return false;

                mActionmode = startActionMode(mActionModeCallback);
                v.setSelected(true);
                return true;
            }
        });
*/

    }

    public class JavaScriptInterface {

        @JavascriptInterface
        public void getText (String text){
            Activity activity = (Activity) context;
            final CharSequence cs = text;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    searchView.setQuery (cs,true);
                    searchView.clearFocus();

                }
            });
        }
    }

/*
    @Override
    public ActionMode startActionMode (ActionMode.Callback callback){

        ViewParent parent = getParent();
        if (parent == null)
            return null;

        mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this,mActionModeCallback);
    }
*/

/*
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
                    CustomWebView.this.loadUrl("javascript:JavaScriptInterface.getText(window.getSelection().toString())");
                    break;

                case R.id.addWordButton:

                    if (CustomWebView.AddWordAvailable == 1)
                        ((Webviewactivity)CustomWebView.this.context).saveWordAndMeaning();
                    else {
                        Log.w("test", "단어 추가 중");
                        Toast.makeText(context,"단어를 추가하는 중 입니다.",Toast.LENGTH_LONG).show();
                    }
                    //CustomWebView.this.loadUrl("javascript:JavaScriptInterface.getMeaning(document.querySelector(\"div#content span.fnt_e30 strong\").innerText)");
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearFocus();
            mActionmode = null;
        }
    }*/

    public void setSearchView (SearchView searchView) {
        this.searchView = searchView;
    }

    public void setToolbar (Toolbar toolbar){
        this.toolbar = toolbar;
    }

    public void setProgressBar (ProgressBar progressBar) {this.progressBar = progressBar;}
}
