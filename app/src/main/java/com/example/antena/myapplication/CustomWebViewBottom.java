package com.example.antena.myapplication;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class CustomWebViewBottom extends WebView {

    private Context context;

    public CustomWebViewBottom(Context context) {

        super(context);
        this.context = context;
    }

    public CustomWebViewBottom (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
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

