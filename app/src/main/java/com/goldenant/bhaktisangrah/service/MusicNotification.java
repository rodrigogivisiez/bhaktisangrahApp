package com.goldenant.bhaktisangrah.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import java.io.IOException;
import java.net.URL;

/**
 * Created by JD Android on 15-Jun-18.
 */

public class MusicNotification extends Notification {

    private Context ctx;
    private NotificationManager mNotificationManager;
    private Bitmap artwork;


    public void buildNotification(Context mContext, SubCategoryModel songDetail) {
        final String albumName = songDetail.getItem_name();
        final String description = songDetail.getItem_description();

        String text = TextUtils.isEmpty(description)
                ? albumName : description;

     /*
      final boolean isPlaying = isPlaying();
      int playButtonResId = isPlaying
                ? R.drawable.ic_play_arrow : R.drawable.ic_pause;*/
        Intent nowPlayingIntent = getNowPlayingIntent(mContext);
        PendingIntent clickIntent = PendingIntent.getActivity(mContext, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            URL url = new URL(songDetail.getItem_image());
            artwork = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            if (artwork == null) {
                artwork = BitmapFactory.decodeResource(mContext.getResources(),
                        R.drawable.no_image);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

       // mNotificationPostTime = System.currentTimeMillis();

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(mContext, MusicService.CHANNEL_ID);
        notificationBuilder
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setColor(ContextCompat.getColor(mContext, R.color.theme_color))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(clickIntent)
                .setContentTitle(albumName)
                .setContentText(text)
                .setSubText("")
                .setLargeIcon(artwork)
                .addAction(R.drawable.ic_skip_previous,
                        "",
                        retrievePlaybackAction(mContext,MusicService.PREVIOUS_ACTION))
                .addAction(R.drawable.ic_play_arrow, "",
                        retrievePlaybackAction(mContext,MusicService.TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.ic_skip_next,
                        "",
                        retrievePlaybackAction(mContext,MusicService.NEXT_ACTION));
        ;
                                /*.setCancelButtonIntent(
                                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                                mContext, PlaybackStateCompat.ACTION_STOP)))*/


        //  .setLargeIcon(MusicLibrary.getAlbumBitmap(mContext, description.getMediaId()))

        Notification n = notificationBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, n);

    }


    private final PendingIntent retrievePlaybackAction(Context context,final String action) {
        final ComponentName serviceName = new ComponentName(context, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(context, 0, intent, 0);
    }


    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MusicService.ACTION_PLAY);
        return intent;
    }

  /*  public MusicNotification(Context ctx, String fileImage){
        super();
        this.ctx=ctx;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(ctx);

        RemoteViews mContentView = new RemoteViews(ctx.getPackageName(), R.layout.now_playing_card);
        mContentView.setImageViewResource(R.id.image, R.drawable.no_image);
        mContentView.setTextViewText(R.id.title, "Custom notification");
        mContentView.setTextViewText(R.id.text, fileImage);
        android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Content Title")
                .setContentText("Content Text")
                .setContent(mContentView)
                .setPriority(NotificationCompat.PRIORITY_MIN);
        final Notification notification = mBuilder.build();
        setListeners(mContentView);
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);

      *//*  @SuppressWarnings("deprecation")
        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon= R.drawable.ic_launcher;

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.now_playing_card);

        //set the button listeners
        setListeners(contentView);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        mNotificationManager.notify(548853, notification);*//*
    }*/

    public void setListeners(RemoteViews view){
        //radio listener
        Intent radio=new Intent(ctx,HelperActivity.class);
        radio.putExtra("DO", "radio");
        PendingIntent pRadio = PendingIntent.getActivity(ctx, 0, radio, 0);
        view.setOnClickPendingIntent(R.id.radio, pRadio);

        //top listener
        Intent top=new Intent(ctx, HelperActivity.class);
        top.putExtra("DO", "top");
        PendingIntent pTop = PendingIntent.getActivity(ctx, 3, top, 0);
        view.setOnClickPendingIntent(R.id.top, pTop);

        //app listener
        Intent app=new Intent(ctx,HelperActivity.class);
        app.putExtra("DO", "play_pause");
        PendingIntent pApp = PendingIntent.getActivity(ctx, 4, app, 0);
        view.setOnClickPendingIntent(R.id.play_pause, pApp);
    }
}
