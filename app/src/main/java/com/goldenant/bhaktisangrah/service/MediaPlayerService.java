package com.goldenant.bhaktisangrah.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.helpers.PlaybackStatus;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.crashlytics.android.beta.Beta.TAG;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {
    private static final String BHAKTISANGRAH_PACKAGE_NAME ="com.goldenant.bhaktisangrah" ;
    private static final String MUSIC_PACKAGE_NAME="com.android.music";
    public static String CHANNEL_ID = "BhaktiSangrah";
    public static final String PLAYSTATE_CHANGED ="com.goldenant.bhaktisangrah.PLAYSTATE_CHANGED" ;
    public static final String REFRESH = "com.goldenant.bhaktisangrah.REFRESH";
    public static final String PLAYLIST_CHANGED ="com.goldenant.bhaktisangrah.PLAYLIST_CHANGED" ;
    public static final String ACTION_PLAY = "com.goldenant.bhaktisangrah.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.goldenant.bhaktisangrah.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.goldenant.bhaktisangrah.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.goldenant.bhaktisangrah.ACTION_NEXT";
    public static final String ACTION_STOP = "com.goldenant.bhaktisangrah.ACTION_STOP";
    public static final String META_CHANGED="com.goldenant.bhaktisangrah.META_CHANGED";
    public static final String TRACK_ERROR="com.goldenant.bhaktisangrah.trackerror";

    private MediaPlayer mediaPlayer;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    public static final int NOTIFICATION_ID = 101;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    //AudioFocus
    private AudioManager audioManager;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //List of available Audio files
    private ArrayList<SubCategoryModel> audioList=new ArrayList<>();
    private int audioIndex = -1;
    private SubCategoryModel activeAudio; //an object on the currently playing audio


    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private Bitmap albumArt;
    private Bitmap largeIcon;
    public boolean isPlayerPrepared =false;
    private NotificationManager mNotificationManager;
    private MediaPlayer mNextMediaPlayer;
    public boolean isRepeat;
    private Boolean isShuffle;

    public boolean isPlayerPrepared() {
        return isPlayerPrepared;
    }

    /**
     * Service lifecycle methods
     */
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Notification channel for android O
        createNotificationChannel();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void playSong() {
        try {
            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(mediaSession!=null) {
            mediaSession.release();
        }
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        audioList=null;
        audioIndex=-1;
        //clear cached playlist
       // new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();

    }

    private void notifyChange(final String what) {
       Log.d(TAG, "notifyChange: what = " + what);

        final Intent intent = new Intent(what);
        intent.putExtra("id", activeAudio.getItem_id());
        intent.putExtra("artist", activeAudio.getItem_name());
        intent.putExtra("album", activeAudio.getItem_name());
        intent.putExtra("albumid",activeAudio.getItem_id());
        intent.putExtra("track", activeAudio.getItem_file());
        intent.putExtra("playing",isMediaPlaying());

        sendStickyBroadcast(intent);
        final Intent musicIntent = new Intent(intent);
        musicIntent.setAction(what.replace(BHAKTISANGRAH_PACKAGE_NAME, MUSIC_PACKAGE_NAME));
        sendStickyBroadcast(musicIntent);
    }

    public void playNextTrack() {
        skipToNext();
       // updateMetaData();
       // buildNotification(PlaybackStatus.PLAYING);
       // transportControls.skipToNext();
    }

    public void playPreviousTrack() {
        skipToPrevious();
       // updateMetaData();
       // buildNotification(PlaybackStatus.PLAYING);
       // transportControls.skipToPrevious();
    }

    public void resumeMediaPlay() {
        resumeMedia();
      //  updateMetaData();
      //  buildNotification(PlaybackStatus.PLAYING);
       // transportControls.play();
    }

    public void pauseMediaPlay() {
        pauseMedia();
       // updateMetaData();
       // buildNotification(PlaybackStatus.PAUSED);
       // transportControls.pause();
    }

    public long getDuration() {
        long duration = 0;
        if(mediaPlayer!=null || isPlayerPrepared) {
            duration= mediaPlayer.getDuration();
        }
        return duration;
    }

    public long getCurrentPosition() {
        long position = 0;
        if(mediaPlayer!=null || isPlayerPrepared) {
            position= mediaPlayer.getCurrentPosition();
        }
        return position;
    }

    public void seekTo(int currentPosition) {
        if(mediaPlayer!=null || isPlayerPrepared) {
            mediaPlayer.seekTo(currentPosition);
        }
    }
    public boolean isMediaPlaying(){
        boolean isPlaying=false;
        if(mediaPlayer!=null || isPlayerPrepared) {
            isPlaying= mediaPlayer.isPlaying();
        }
        return isPlaying;
    }


    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerService.this;
        }
    }


    /**
     * MediaPlayer callback methods
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(isRepeat){
            mp.setLooping(true);
            mp.start();
        }
        if(mp != null && mp.isPlaying()){
            stopMedia();
            stopSelf();
            playNextTrack();
        }
        if(!mp.isPlaying()){
            stopMedia();
            stopSelf();
        }
       /* //Invoked when playback of a media source has completed.
        if(mediaPlayer==null)return;
        stopMedia();
        stopSelf();
        mediaPlayer=null;
        playNextTrack();
       // removeNotification();
        //stop the service
        //stopSelf();
        if (mp == mediaPlayer && mNextMediaPlayer != null) {
            //stopMedia();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
           // mediaPlayer = mNextMediaPlayer;
           // mNextMediaPlayer = null;
            playNextTrack();
           // mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
        } else {
            //mService.get().mWakeLock.acquire(30000);
            stopMedia();
            stopSelf();
     //   }*/
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        isPlayerPrepared =true;
        playMedia();
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }


    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        if (audioManager != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }
    return false;
    }


    /**
     * MediaPlayer actions
     */
    private void initMediaPlayer() {
       /* if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;
        }*/
       if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();//new MediaPlayer instance
         //  mediaPlayer = MediaPlayer.create(this, Uri.parse(activeAudio.getItem_file()));

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();
        isPlayerPrepared =false;
        updateMetaData();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            Log.e("file : ",activeAudio.getItem_file());
            mediaPlayer.setDataSource(activeAudio.getItem_file());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }


    private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
        try {
            player.reset();
            player.setOnPreparedListener(null);
            player.setDataSource(path);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            player.prepare();
        } catch (final IOException todo) {

            return false;
        } catch (final IllegalArgumentException todo) {

            return false;
        }
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        return true;
    }

    public void setNextDataSource(final String path) {
        //mNextMediaPath = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mediaPlayer.setNextMediaPlayer(null);
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "Next media player is current one, continuing");
        } catch (IllegalStateException e) {
            Log.e(TAG, "Media player not initialized!");
            return;
        }
        if (mNextMediaPlayer != null) {
            mNextMediaPlayer.release();
            mNextMediaPlayer = null;
        }
        if (path == null) {
            return;
        }
        mNextMediaPlayer = new MediaPlayer();
       // mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        //mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
        try {
            if (setDataSourceImpl(mNextMediaPlayer, path)) {
                //mNextMediaPath = path;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                    updateMetaData();
                    buildNotification(PlaybackStatus.PLAYING);
                    notifyChange(META_CHANGED);
                    notifyChange(REFRESH);
                }
            } else {
                if (mNextMediaPlayer != null) {
                    mNextMediaPlayer.release();
                    mNextMediaPlayer = null;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
            notifyChange(META_CHANGED);
            notifyChange(REFRESH);
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
           /* updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
            notifyChange(META_CHANGED);*/
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            updateMetaData();
            buildNotification(PlaybackStatus.PAUSED);
            notifyChange(META_CHANGED);
        }
    }

    public void resumeMedia() {
        if(mediaPlayer==null){
            playSong();
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
            notifyChange(META_CHANGED);
        }
    }

    public void skipToNext() {

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }

        //Update stored index
         new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

       // stopMedia();
        //reset mediaPlayer
      //  mediaPlayer.reset();
        initMediaPlayer();
      //  setNextDataSource(activeAudio.getItem_file());
    }

    public void skipToPrevious() {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Handle PhoneState changes
     */
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();
       // notifyChange(META_CHANGED);

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                /*updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
                notifyChange(META_CHANGED);*/
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                /*updateMetaData();
                buildNotification(PlaybackStatus.PAUSED);
                notifyChange(META_CHANGED);*/
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
               /* updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
                notifyChange(META_CHANGED);*/

            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                /*updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
                notifyChange(META_CHANGED);*/

            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                updateMetaData();
                notifyChange(META_CHANGED);
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        if (activeAudio.getItem_image() != null) {
            URL url = null;
               /* url = new URL(activeAudio.getItem_image());
                albumArt = BitmapFactory.decodeStream(url.openConnection().getInputStream());*/
            Log.e("update_img", activeAudio.getItem_image());
            //DownloadFilesTask downloadFilesTask = new DownloadFilesTask(activeAudio.getItem_image());
            // albumArt = downloadFilesTask.bitmap;

        } else {

            albumArt = BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_image); //replace with medias albumArt
        }
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getItem_id())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getItem_name())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getItem_description())
                .build());

    }
    private void sendErrorMessage(final String trackName) {
        final Intent i = new Intent(TRACK_ERROR);
        i.putExtra(TrackErrorExtra.TRACK_NAME, trackName);
        sendBroadcast(i);
    }
    public interface TrackErrorExtra {

        String TRACK_NAME = "trackname";
    }
    private void buildNotification(PlaybackStatus playbackStatus) {

        /**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */
        if (activeAudio.getItem_image() != null) {
            URL url = null;
            try {
                {
                    url = new URL(activeAudio.getItem_image());
                    Log.e("build notification", activeAudio.getItem_image());
                    largeIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    if (largeIcon == null) {
                        largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.no_image);
                    }
                }
                //  DownloadFilesTask downloadFilesTask = new DownloadFilesTask(activeAudio.getItem_image());
                // largeIcon = downloadFilesTask.bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            largeIcon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_image); //replace with your own image

        }

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        // Create a new Notification

        Intent nowPlayingIntent = getNowPlayingIntent(this);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder notificationBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSession.getSessionToken())
                                .setShowCancelButton(true)
                                .setShowActionsInCompactView(0, 1, 2))
                .setColor(ContextCompat.getColor(this, R.color.theme_color))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC)
               /* .setOnlyAlertOnce(true)*/
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(clickIntent)
                .setSubText("")
                .setLargeIcon(largeIcon)
                .setContentText(activeAudio.getItem_description())
                .setContentTitle(activeAudio.getItem_name())
                /*.setContentInfo(activeAudio.getItem_description())*/
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        getApplicationContext(), PlaybackStateCompat.ACTION_STOP))
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));



        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    public void cancelNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID); // Notification ID to cancel
    }
    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(ACTION_PLAY);
        return intent;
    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;
           if(transportControls==null) return;
        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


    /**
     * Play new Audio
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            //Request audio focus
            if (requestAudioFocus() == false) {
                //Could not gain focus
                stopSelf();
            }

            stopMedia();
           // mediaPlayer.reset();
            if (mediaSessionManager == null) {

                try {
                    initMediaSession();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
            initMediaPlayer();

           /* updateMetaData();
            notifyChange(META_CHANGED);
            buildNotification(PlaybackStatus.PLAYING);*/
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private class DownloadFilesTask extends AsyncTask<String, String, Bitmap> {
        String imgUrl;
        private URL url;
        private Bitmap bitmap;

        public DownloadFilesTask(String url) {
            imgUrl = url;
        }


        @Override
        protected Bitmap doInBackground(String... strings) {

            InputStream inputStream;
            try {
                url = new URL(imgUrl);
                inputStream = url.openConnection().getInputStream();
                largeIcon = BitmapFactory.decodeStream(inputStream);
                albumArt = largeIcon;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Timber";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            manager.createNotificationChannel(mChannel);
        }
    }


    public ArrayList<SubCategoryModel> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<SubCategoryModel> audioList) {
        if(audioList!=null){
            Log.e("songs", audioList.toString());
            this.audioList.clear();
        }
        this.audioList = audioList;
    }

    public int getAudioIndex() {
        return audioIndex;
    }

    public void setAudioIndex(int audioIndex) {
        this.audioIndex = audioIndex;
    }

    public void setShuffleMode(Boolean bool) {
        isShuffle=bool;
    }

    public boolean isShuffle(){
        return isShuffle;
    }
    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public SubCategoryModel getActiveAudio() {
        return activeAudio;
    }

    public void setActiveAudio(SubCategoryModel activeAudio) {
        this.activeAudio = activeAudio;
    }
}
