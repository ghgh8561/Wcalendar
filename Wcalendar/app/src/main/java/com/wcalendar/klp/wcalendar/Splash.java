package com.wcalendar.klp.wcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 1000);

    }

    private class splashHandler implements  Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
