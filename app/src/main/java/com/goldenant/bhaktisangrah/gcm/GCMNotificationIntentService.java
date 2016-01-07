package com.goldenant.bhaktisangrah.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.DatabaseHelper;
import com.goldenant.bhaktisangrah.model.NotificationRecord;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GCMNotificationIntentService extends IntentService {
	// Sets an ID for the notification, so it can be updated
	public static final int notifyID = 9001;
	NotificationCompat.Builder builder;

	DatabaseHelper mDbHelper, dbAdapters;
	SQLiteDatabase mDb;
	static int version_val = 1;

	NotificationRecord nr = new NotificationRecord();

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();

		mDbHelper = new DatabaseHelper(this, "Database.sqlite", null,version_val);
		dbAdapters = DatabaseHelper.getDBAdapterInstance(this);

		try
		{
			dbAdapters.createDataBase();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				// sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				// sendNotification("Deleted messages on server: "
				// + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				Log.d("EXTRAS",""+extras.toString());

				String msg = extras.get(ApplicationConstants.MSG_KEY)
						.toString();

				sendNotification(msg);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg)
	{
		Intent resultIntent = new Intent(this, GCMDialogActivity.class);
		resultIntent.putExtra("msg", msg);

		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_ONE_SHOT);

		NotificationCompat.Builder mNotifyBuilder;
		NotificationManager mNotificationManager;

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotifyBuilder = new NotificationCompat.Builder(this)
				.setContentTitle("Bhakti Sagar")
				.setContentText("You've received new message.")
				.setSmallIcon(R.drawable.ic_launcher);
		// Set pending intent
		mNotifyBuilder.setContentIntent(resultPendingIntent);

		// Set Vibrate, Sound and Light
		int defaults = 0;
		defaults = defaults | Notification.DEFAULT_LIGHTS;
		defaults = defaults | Notification.DEFAULT_VIBRATE;
		defaults = defaults | Notification.DEFAULT_SOUND;

		mNotifyBuilder.setDefaults(defaults);
		// Set the content for Notification
		mNotifyBuilder.setContentText("You've received new message.");
		// Set autocancel
		mNotifyBuilder.setAutoCancel(true);
		// Post a notification
		mNotificationManager.notify(notifyID, mNotifyBuilder.build());

		//Insert into db start
		try
		{
			Calendar cal = Calendar.getInstance(TimeZone.getDefault());
			Date currentLocalTime = cal.getTime();
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			date.setTimeZone(TimeZone.getDefault());

			String localTime = date.format(currentLocalTime);

			dbAdapters.openDataBase();
			nr.setContent(msg);
			nr.setDate(localTime);

			dbAdapters.Insert_Record(nr);

			dbAdapters.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//Insert db end
	}
}
