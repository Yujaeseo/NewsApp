package com.example.antena.myapplication.webview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.antena.myapplication.R;

public class CustomWebView extends WebView {

    private Context context;
    private ActionMode mActionmode;
    private ActionMode.Callback mActionModeCallback;

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
        this.addJavascriptInterface(new JavaScriptInterface(), "JavaScriptInterface");
        Toast.makeText(context,"단어를 검색하기 위해 화면을 길게 눌러주세요.",Toast.LENGTH_LONG).show();

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
    }

    public class JavaScriptInterface {

        @JavascriptInterface
        public void getText (String text){
            Activity activity = (Activity) context;
            final CharSequence cs = text;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    CustomWebViewBottom.searchView.setQuery (cs,false);
                    CustomWebViewBottom.searchView.clearFocus();

                }
            });
        }
    }

    @Override
    public ActionMode startActionMode (ActionMode.Callback callback){
        ViewParent parent = getParent();
        if (parent == null)
            return null;
        //mActionModeCallback = new CustomActionModeCallback();
        mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this,mActionModeCallback);
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
                case R.id.addwordButton:
                    CustomWebView.this.loadUrl("javascript:JavaScriptInterface.getText(window.getSelection().toString())");
                    break;
                case R.id.highlightButton:
                    ((Webviewactivity)CustomWebView.this.context).saveWordAndMeaning();
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
    }
}
