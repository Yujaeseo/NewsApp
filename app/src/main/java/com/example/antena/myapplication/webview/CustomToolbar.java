package com.example.antena.myapplication.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Outline;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.antena.myapplication.R;

import java.util.Calendar;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CustomToolbar extends Toolbar {


    private  static  final int MAX_CLICK_DURATION = 85;
    private long startClickTime;

    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchY = -1;
    private int pointerIndex ;

    private Context context;

    private CustomWebViewBottom bottomWebView;
    private SearchView searchView;

    public CustomToolbar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }


    @SuppressLint("ClickableViewAccessibility")
    public void init(){
        LayoutInflater inflater =  (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.customtoolbar,this,true);

        searchView = v.findViewById(R.id.searchView);
        searchView.isSubmitButtonEnabled();

//        bottomWebView.setProgressBar((ProgressBar) v.findViewById(R.id.bottomprogressBar));

        if (bottomWebView == null)
            Log.w("progre","s");
/*
        bottomWebView.setWebChromeClient(new WebChromeClient(){
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


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getActionMasked()){

            case MotionEvent.ACTION_DOWN:{

                startClickTime = Calendar.getInstance().getTimeInMillis();

                pointerIndex = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(ev.getActionIndex());

                // Save the ID of this pointer (for dragging)
                // Remember where we started (for dragging)
                mLastTouchY = ev.getRawY();
                break;
            }

            case MotionEvent.ACTION_UP:{
                break;
            }

            case MotionEvent.ACTION_MOVE:{

                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                if (clickDuration < MAX_CLICK_DURATION) {
                    //주어진 시간보다 짧으면 클릭이벤트 실행 (child view)
                    return false;
                }

                else {
                    //주어진 시간보다 길면 드래그 이벤트 실행 (this)
                    return true;
                }
            }

        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        // view root (activity)를 기준으로 뷰의 좌표를 얻음.
        int pos [] = new int [2];
        this.getLocationInWindow(pos);

        switch ( event.getActionMasked() ) {

            case MotionEvent.ACTION_DOWN:{
                break;
            }

            case MotionEvent.ACTION_MOVE:{

                LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams )(bottomWebView.getLayoutParams());

                final float y = event.getY(pointerIndex);
                float dy = ( y + pos[1] ) - mLastTouchY;

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int height = displayMetrics.heightPixels;

                // 허용 범위를 벋어 나면 이동을 중지
                if ((layoutParams.height - (int)dy <= 0) || (layoutParams.height - (int)dy >= height - 200) )
                    break;

                layoutParams.height -= (int)dy;

                bottomWebView.findViewById(R.id.bottomWebView).setLayoutParams(layoutParams);
                bottomWebView.findViewById(R.id.bottomWebView).invalidate();

                mLastTouchY = pos[1] + y;
                break;
            }

            case MotionEvent.ACTION_UP:{
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            //https://stackoverflow.com/questions/4268426/android-difference-between-action-up-and-action-pointer-up
            //https://stackoverflow.com/questions/5240719/whats-the-difference-between-action-cancel-and-action-up-in-motionevent

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

    public void setBottomWebView(CustomWebViewBottom bottomWebView) {
        this.bottomWebView = bottomWebView;
    }
}