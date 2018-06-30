package com.goldenant.bhaktisangrah.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.DatabaseHelper;
import com.goldenant.bhaktisangrah.gcm.ApplicationConstants;
import com.goldenant.bhaktisangrah.gcm.GCMDialogActivity;
import com.goldenant.bhaktisangrah.model.NotificationRecord;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;



public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static final int notifyID = 9001;
    DatabaseHelper mDbHelper, dbAdapters;
    SQLiteDatabase mDb;
    static int version_val = 1;

    NotificationRecord nr = new NotificationRecord();

    public void prepareDB() {
        mDbHelper = new DatabaseHelper(this, "Database.sqlite", null, version_val);
        dbAdapters = DatabaseHelper.getDBAdapterInstance(this);

        try {
            dbAdapters.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        prepareDB();
        Log.d("FCM", remoteMessage.getData().toString() + " 123");
        Log.e("remoteMessage", "" + remoteMessage.getData());

        String message = remoteMessage.getData().get(ApplicationConstants.MSG_KEY);

        Log.e(TAG, "message: " + message);

        sendNotification(message);
    }

    private void sendNotification(String msg) {
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Insert db end
    }

}
