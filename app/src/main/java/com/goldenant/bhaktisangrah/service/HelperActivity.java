package com.goldenant.bhaktisangrah.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by JD Android on 15-Jun-18.
 */

public class HelperActivity  extends Activity {

    private HelperActivity ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ctx = this;
        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("radio")) {
            //Your code
        } else if (action.equals("volume")) {
            //Your code
        } else if (action.equals("reboot")) {
            //Your code
        } else if (action.equals("top")) {
            //Your code
        } else if (action.equals("play_pause")) {
            //Your code
            Log.e("clicked" ,"Play");
        }

        if (!action.equals("reboot"))
            finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}