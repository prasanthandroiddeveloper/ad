package com.tripnetra.tnadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tripnetra.tnadmin.utils.Utils;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getSupportActionBar() != null;
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        new Thread(){
            @Override
            public void run(){
                try {
                    sleep(1000);
                    startActivity(new Intent(getApplicationContext(),LoginMainActivity.class));
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(SplashScreen.this,"System Busy","Ok",false);
                }
                finish();
            }
        }.start();
    }


}
