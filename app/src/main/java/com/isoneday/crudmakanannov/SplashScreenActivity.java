package com.isoneday.crudmakanannov;

import android.os.Bundle;
import android.os.Handler;

import com.isoneday.crudmakanannov.helper.SessionManager;

public class SplashScreenActivity extends SessionManager {


    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        manager = new SessionManager(this);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                   manager.checkLogin();
               finish();
            }
        },3000);
    }
}
