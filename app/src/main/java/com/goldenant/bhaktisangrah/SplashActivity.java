package com.goldenant.bhaktisangrah;

import android.content.Intent;
import android.os.Bundle;

import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;


public class SplashActivity extends MasterActivity {
    protected int splashTime = 4000;
    private Thread splashTread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isInternet = new InternetStatus().isInternetOn(this);

        final SplashActivity sPlashScreen = this;
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(splashTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally

                {
                    Intent i = new Intent();
                    i.setClass(sPlashScreen,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        splashTread.start();
    }

}
