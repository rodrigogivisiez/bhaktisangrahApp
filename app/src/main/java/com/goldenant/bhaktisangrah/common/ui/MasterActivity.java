package com.goldenant.bhaktisangrah.common.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;

/**
 * Created by Adite on 02-01-2016.
 */
public class MasterActivity extends AppCompatActivity {

    public boolean isInternet;

    public static Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternet = new InternetStatus().isInternetOn(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setTitle(String title) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(title);

    }

    public void setTextSize(int title_text_size) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setTextSize(title_text_size);
    }

    public Typeface getTypeFace() {
        font = Typeface.createFromAsset(getAssets(), "ProximaNova-Light.otf");
        return font;
    }
}
