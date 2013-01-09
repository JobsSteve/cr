package com.gts.cr.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.gts.cr.main.R;

public class SplashScreenActivity extends Activity {
	 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.splash_screen);
 
        Handler handler = new Handler();
 
        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {
 
            public void run() {
 
                // make sure we close the splash screen so the user won't come back when it presses back key
 
                finish();
                // start the home screen
 
                Intent intent = new Intent(SplashScreenActivity.this, StrategicSplashActivity.class);
                SplashScreenActivity.this.startActivity(intent);
 
            }
 
        }, 2000); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
 
    }
 
}