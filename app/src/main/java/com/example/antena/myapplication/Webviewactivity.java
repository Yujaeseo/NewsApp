package com.example.antena.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class Webviewactivity extends AppCompatActivity {

    private CustomWebView myWebView;
    private CustomWebViewBottom bottomWebView;
    private Toolbar mToolbar;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchY = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewactivity);
        
        String url = getIntent().getStringExtra("newsUrl");
        myWebView = (CustomWebView) findViewById(R.id.testWebView);
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

        // other settings
        // https://stackoverflow.com/questions/48403090/webview-not-working-properly
        myWebView.loadUrl(url);
        //webview.loadUrl("javascript:highlightSelection()");
        bottomWebView.loadUrl("http://dic.naver.com/");
        mToolbar = findViewById(R.id.bottomWebViewToolbar);
        mToolbar.setOnTouchListener(new ToolbarTouchListener());
        bottomWebView.requestDisallowInterceptTouchEvent(true);

    }

    private class MycustomView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    public class ToolbarTouchListener implements View.OnTouchListener {

        //float mLastTouchX = 0 ;
        //float mLastTouchY = 0 ;
        //int mActivePointerId = 0;// 초기화 하라고 해서..

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int pos [] = new int [2];
            mToolbar.getLocationInWindow(pos);

            switch ( event.getActionMasked() ) {

                case MotionEvent.ACTION_DOWN:{
                    mActivePointerId = event.getPointerId(event.getActionIndex());
                    final int pointerIndex = event.getActionIndex();
                    final float y = event.getY(pointerIndex);

                    //Log.w("touch",pos[0] + "," + pos[1]);
                    // Remember where we started (for dragging)
                    mLastTouchY = pos[1] + y;

                    // Save the ID of this pointer (for dragging)
                    //bottomWebView.mActivePointerId = event.getPointerId(0);
                    break;
                }

                case MotionEvent.ACTION_MOVE:{

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)(bottomWebView.getLayoutParams());

                    final int pointerIndex = event.findPointerIndex(mActivePointerId);
                    final float y = event.getY(pointerIndex);

                    float dy = ( y + pos[1] ) - mLastTouchY;

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;

                    // position change
                    if (layoutParams.height - (int)dy <= 100 || layoutParams.height - (int)dy >= height - 200)
                        break;
                    layoutParams.height -= (int)dy;
                    bottomWebView.setLayoutParams(layoutParams);
                    bottomWebView.invalidate();

                    mLastTouchY = pos[1] + y;

                    break;
                }

                case MotionEvent.ACTION_UP:{
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                //https://stackoverflow.com/questions/4268426/android-difference-between-action-up-and-action-pointer-up
                //https://stackoverflow.com/questions/5240719/whats-the-difference-between-action-cancel-and-action-up-in-motionevent
                //parent를 벗어났을 때

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP:{

                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int pointerIndex = event.getActionIndex();
                    final int pointerId = event.getPointerId(pointerIndex);

                    if (pointerId == mActivePointerId){
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchY = pos [1];
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }

                    break;
                }
            }

            return true;
        }

    }
}
