package com.goldenant.bhaktisangrah.gcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.SplashActivity;

import org.json.JSONObject;

@SuppressLint("NewApi")
public class GCMDialogActivity extends Activity {
	Context context;
	Bundle bundleObject;
	TextView txtGCMText;
	RelativeLayout GCMView;
	JSONObject jGCM = new JSONObject();
	String msg;
	Bundle b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gcm_alert_dialog);

		b = getIntent().getExtras();
		txtGCMText = (TextView) findViewById(R.id.GCMMessage);

		if (b != null) {
			txtGCMText.setText(b.getString("msg"));
		}

//		btnClose = (ImageButton) findViewById(R.id.btnClose);
//		btnClose.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				intent.addCategory(Intent.CATEGORY_HOME);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				startActivity(intent);
//				System.exit(1);
//			}
//		});

		GCMView = (RelativeLayout) findViewById(R.id.GCMView);
		GCMView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(GCMDialogActivity.this,
						SplashActivity.class));
			}
		});
	}
}
