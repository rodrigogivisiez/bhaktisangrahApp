package com.goldenant.bhaktisangrah.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;


import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.PreferencesUtility;
import com.goldenant.bhaktisangrah.common.util.Utilities;
import com.goldenant.bhaktisangrah.helpers.MediaButtonIntentReceiver;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

/**
 * Created by JD Android on 15-Jun-18.
 */

public class MusicService extends Service {
    //media player
    private MultiPlayer mPlayer;
    //song list
    private ArrayList<SubCategoryModel> songs;
    //current position
    private int currentSongPosition;

    private final IBinder musicBind = new MusicBinder();
    public static String ACTION_PLAY = "com.goldenant.bhaktisangrah.action.PLAY";
    private long mNotificationPostTime;
    private Bitmap artwork;
    private static boolean mIsSupposedToBePlaying = false;
    private NotificationManagerCompat mNotificationManager;
    private int notificationId;
    public static String CHANNEL_ID = "BhaktiSangrah";

    private static final int NOTIFY_MODE_NONE = 0;
    private static final int NOTIFY_MODE_FOREGROUND = 1;
    private static final int NOTIFY_MODE_BACKGROUND = 2;
    private int mNotifyMode;
    private static final int IDLE_DELAY = 5 * 60 * 1000;
    private static final long REWIND_INSTEAD_PREVIOUS_THRESHOLD = 3000;
    private static long mLastPlayedTime;

    private static AlarmManager mAlarmManager;

    private static PendingIntent mShutdownIntent;
    private static boolean mShutdownScheduled;
    private static Context mService;
    private MediaSessionCompat.Token token;
    private MediaSessionCompat mMediaSession;

    public static final String PLAYSTATE_CHANGED = "com.naman14.timber.playstatechanged";
    public static final String POSITION_CHANGED = "com.naman14.timber.positionchanged";
    public static final String META_CHANGED = "com.naman14.timber.metachanged";
    public static final String QUEUE_CHANGED = "com.naman14.timber.queuechanged";
    public static final String PLAYLIST_CHANGED = "com.naman14.timber.playlistchanged";
    public static final String REPEATMODE_CHANGED = "com.naman14.timber.repeatmodechanged";
    public static final String SHUFFLEMODE_CHANGED = "com.naman14.timber.shufflemodechanged";
    public static final String TRACK_ERROR = "com.naman14.timber.trackerror";
    public static final String TIMBER_PACKAGE_NAME = "com.naman14.timber";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String SERVICECMD = "com.naman14.timber.musicservicecommand";
    public static final String TOGGLEPAUSE_ACTION = "com.naman14.timber.togglepause";
    public static final String PAUSE_ACTION = "com.naman14.timber.pause";
    public static final String STOP_ACTION = "com.naman14.timber.stop";
    public static final String PREVIOUS_ACTION = "com.naman14.timber.previous";
    public static final String PREVIOUS_FORCE_ACTION = "com.naman14.timber.previous.force";
    public static final String NEXT_ACTION = "fcom.naman14.timber.next";
    public static final String REPEAT_ACTION = "com.naman14.timber.repeat";
    public static final String SHUFFLE_ACTION = "com.naman14.timber.shuffle";
    public static final String FROM_MEDIA_BUTTON = "frommediabutton";
    public static final String REFRESH = "com.naman14.timber.refresh";
    public static final String UPDATE_LOCKSCREEN = "com.naman14.timber.updatelockscreen";
    public static final String CMDNAME = "command";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDSTOP = "stop";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPLAY = "play";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String CMDNOTIF = "buttonId";
    public static final String UPDATE_PREFERENCES = "updatepreferences";
    public static final int NEXT = 2;
    public static final int LAST = 3;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public static final int MAX_HISTORY_SIZE = 1000;
    private static final String TAG = "MusicPlaybackService";
    private static final boolean D = false;
    private static final String SHUTDOWN = "com.naman14.timber.shutdown";
    private static final int IDCOLIDX = 0;
    private static final int TRACK_ENDED = 1;
    private static final int TRACK_WENT_TO_NEXT = 2;
    private static final int RELEASE_WAKELOCK = 3;
    private static final int SERVER_DIED = 4;
    private static final int FOCUSCHANGE = 5;
    private static final int FADEDOWN = 6;
    private static final int FADEUP = 7;
    private MusicPlayerHandler mPlayerHandler;
    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
            mPlayerHandler.obtainMessage(FOCUSCHANGE, focusChange, 0).sendToTarget();
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String command = intent.getStringExtra(CMDNAME);
            handleCommandIntent(intent);

        }
    };
    private HandlerThread mHandlerThread;
    private MediaStoreObserver mMediaStoreObserver;
    private PowerManager.WakeLock mWakeLock;
    private boolean mServiceInUse = false;
    private AudioManager mAudioManager;
    private ComponentName mMediaButtonReceiverComponent;
    private MediaSessionCompat mSession;
    private boolean mPausedByTransientLossOfFocus;
    private int mRepeatMode, mShuffleMode;
    private int mNextPlayPos;
    private MediaControllerCompat.TransportControls transportControls;


    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();

        if (action == null) {
            Log.d(TAG, "handleCommandIntent: action = " + action);
            return;
        }
        final String command = SERVICECMD.equals(action) ? intent.getStringExtra(CMDNAME) : null;

        if (D) Log.d(TAG, "handleCommandIntent: action = " + action + ", command = " + command);

     /*   if (NotificationHelper.checkIntent(intent)) {
            goToPosition(currentSongPosition + NotificationHelper.getPosition(intent));
            return;
        }*/

        if (CMDNEXT.equals(command) || NEXT_ACTION.equals(action)) {
            gotoNext(true);

        } else if (CMDPREVIOUS.equals(command) || PREVIOUS_ACTION.equals(action)
                || PREVIOUS_FORCE_ACTION.equals(action)) {
            // prev(PREVIOUS_FORCE_ACTION.equals(action));
        } else if (CMDTOGGLEPAUSE.equals(command) || TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pauseSong();

                mPausedByTransientLossOfFocus = false;
            } else {
                startPlaying();

            }
        } else if (CMDPAUSE.equals(command) || PAUSE_ACTION.equals(action)) {
            pauseSong();
            mPausedByTransientLossOfFocus = false;
        } else if (CMDPLAY.equals(command)) {
            playSong();
        } else if (CMDSTOP.equals(command) || STOP_ACTION.equals(action)) {
            pauseSong();
            mPausedByTransientLossOfFocus = false;
            seekTo(0);
            releaseServiceUiAndStop();
        } else if (REPEAT_ACTION.equals(action)) {
            //cycleRepeat();
        } else if (SHUFFLE_ACTION.equals(action)) {
            // cycleShuffle();
        } else if (UPDATE_PREFERENCES.equals(action)) {
            // onPreferencesUpdate(intent.getExtras());
        } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
            if (PreferencesUtility.getInstance(getApplicationContext()).pauseEnabledOnDetach()) {
                pauseSong();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound, intent = " + intent);
        mServiceInUse = true;
        return musicBind;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mPlayer.stop();
        mServiceInUse = false;
        mPlayer.release();

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String action = intent.getAction();
        // if (action.equals(ACTION_PLAY))
        // processPlayRequest();
        /*if (SHUTDOWN.equals(intent.getAction())) {
            mShutdownScheduled = false;
            releaseServiceUiAndStop();
            return START_NOT_STICKY;
        }*/
        if (intent != null) {
            final String action = intent.getAction();

            if (SHUTDOWN.equals(action)) {
                mShutdownScheduled = false;
                releaseServiceUiAndStop();
                return START_NOT_STICKY;
            }
            handleCommandIntent(intent);
        }

        return START_STICKY;
    }

    private void releaseServiceUiAndStop() {
        if (isPlaying()) {
            return;
        }

        Log.d(TAG, "Nothing is playing anymore, releasing notification");
        cancelNotification();
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.setActive(false);

        if (!mServiceInUse) {
            //  saveQueue(true);
            //  stopSelf(mServiceStartId);
        }
    }

    private void cancelNotification() {
        stopForeground(true);
        mNotificationManager.cancel(hashCode());
        mNotificationPostTime = 0;
        mNotifyMode = NOTIFY_MODE_NONE;
    }

    public void processPlayRequest() {
    }

    public void onCreate() {
        Log.e("onCreate", "*");
        mService = this;
        //create the service
        super.onCreate();

        notificationId = hashCode();
        mNotificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();

        mHandlerThread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaButtonReceiverComponent = new ComponentName(getPackageName(),
                MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(mMediaButtonReceiverComponent);

        setUpMediaSession();

        mPlayerHandler = new MusicPlayerHandler(this, mHandlerThread.getLooper());

        //create player

        mPlayer = new MultiPlayer(this);
        mPlayer.setHandler(mPlayerHandler);

        // Initialize the intent filter and each action
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICECMD);
        filter.addAction(TOGGLEPAUSE_ACTION);
        filter.addAction(PAUSE_ACTION);
        filter.addAction(STOP_ACTION);
        filter.addAction(NEXT_ACTION);
        filter.addAction(PREVIOUS_ACTION);
        filter.addAction(PREVIOUS_FORCE_ACTION);
        filter.addAction(REPEAT_ACTION);
        filter.addAction(SHUFFLE_ACTION);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // Attach the broadcast listener
        registerReceiver(mIntentReceiver, filter);

        mMediaStoreObserver = new MediaStoreObserver(mPlayerHandler);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI, true, mMediaStoreObserver);
        getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mMediaStoreObserver);

        // Initialize the wake lock
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.setReferenceCounted(false);


        final Intent shutdownIntent = new Intent(this, MusicService.class);
        shutdownIntent.setAction(SHUTDOWN);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);

        scheduleDelayedShutdown();

      /*  reloadQueueAfterPermissionCheck();
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);*/

    }


    @Override
    public void onRebind(final Intent intent) {
        cancelShutdown();
        mServiceInUse = true;
    }

    private void cancelShutdown() {
        if (D) Log.d(TAG, "Cancelling delayed shutdown, scheduled = " + mShutdownScheduled);
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }

    public void setList(ArrayList<SubCategoryModel> theSongs) {
        songs = theSongs;
        Log.e("songs", songs.toString());
    }

    public int getSongPosition() {
        return currentSongPosition;
    }

    public void setSongPosn(int songPosn) {
        this.currentSongPosition = songPosn;
    }

    public void setSong(int songIndex) {
        currentSongPosition = songIndex;
    }

    public static boolean isPlaybackServiceConnected() {
        if (mService != null) {
            return true;
        } else return false;
    }

    public void pauseSong() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    public long getDuration() {
        return mPlayer.duration();
    }

    public long getCurrentPosition() {
        return mPlayer.position();
    }

    public void startPlaying() {
        mPlayer.start();
    }

    public void seekTo(int currentPosition) {
        mPlayer.seek(currentPosition);
    }

    public void setNextTrack() {
        setNextTrack(getNextPosition(false));
    }

    private void setNextTrack(int position) {
        mNextPlayPos = position;
        if (D) Log.d(TAG, "setNextTrack: next play position = " + mNextPlayPos);
        if (mNextPlayPos >= 0 && songs != null && mNextPlayPos < songs.size()) {
            final String songPath = songs.get(mNextPlayPos).getItem_file();
            mPlayer.setNextDataSource(songPath);
            updateNotification(mNextPlayPos);
        } else {
            mPlayer.setNextDataSource(null);
        }
    }


    public void gotoNext(final boolean force) {
        if (D) Log.d(TAG, "Going to next track");
        synchronized (this) {
            if (songs.size() <= 0) {
                if (D) Log.d(TAG, "No play queue");
                scheduleDelayedShutdown();
                return;
            }
            int pos = getSongPosition();
            if (pos < 0) {
                pos = getNextPosition(force);
            }

            if (pos < 0) {
                setIsSupposedToBePlaying(false, true);
                return;
            }
            setNextTrack();
            //  notifyChange(META_CHANGED);
        }
    }

    private int getNextPosition(final boolean force) {
        if (songs == null || songs.isEmpty()) {
            return -1;
        }
        if (!force && mRepeatMode == REPEAT_CURRENT) {
            if (currentSongPosition < 0) {
                return 0;
            }
            return currentSongPosition;
        } else if (mShuffleMode == SHUFFLE_NORMAL) {
            final int numTracks = songs.size();


            final int[] trackNumPlays = new int[numTracks];
            for (int i = 0; i < numTracks; i++) {
                trackNumPlays[i] = 0;
            }


          /*  final int numHistory = mHistory.size();
            for (int i = 0; i < numHistory; i++) {
                final int idx = mHistory.get(i).intValue();
                if (idx >= 0 && idx < numTracks) {
                    trackNumPlays[idx]++;
                }
            }*/

            if (currentSongPosition >= 0 && currentSongPosition < numTracks) {
                trackNumPlays[currentSongPosition]++;
            }

            int minNumPlays = Integer.MAX_VALUE;
            int numTracksWithMinNumPlays = 0;
            for (int i = 0; i < trackNumPlays.length; i++) {
                if (trackNumPlays[i] < minNumPlays) {
                    minNumPlays = trackNumPlays[i];
                    numTracksWithMinNumPlays = 1;
                } else if (trackNumPlays[i] == minNumPlays) {
                    numTracksWithMinNumPlays++;
                }
            }


            if (minNumPlays > 0 && numTracksWithMinNumPlays == numTracks
                    && mRepeatMode != REPEAT_ALL && !force) {
                return -1;
            }

/*
            int skip = mShuffler.nextInt(numTracksWithMinNumPlays);
            for (int i = 0; i < trackNumPlays.length; i++) {
                if (trackNumPlays[i] == minNumPlays) {
                    if (skip == 0) {
                        return i;
                    } else {
                        skip--;
                    }
                }
            }*/

            if (D)
                Log.e(TAG, "Getting the next position resulted did not get a result when it should have");
            return -1;
        } /*else if (mShuffleMode == SHUFFLE_AUTO) {
            doAutoShuffleUpdate();
            return currentSongPosition + 1;
        }*/ else {
            if (currentSongPosition >= songs.size() - 1) {
                if (mRepeatMode == REPEAT_NONE && !force) {
                    return -1;
                } else if (mRepeatMode == REPEAT_ALL || force) {
                    return 0;
                }
                return -1;
            } else {
                return currentSongPosition + 1;
            }
        }
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        Log.e("prepareAsync", "*");

        //play a song

        //  startForeground(notificationId, buildNotification());
        //get song
        SubCategoryModel playSong = songs.get(currentSongPosition);
        mPlayer.setDataSource(songs.get(currentSongPosition).getItem_file());
        mPlayer.start();
        updateNotification(currentSongPosition);
       /* if (mPlayer.isPlaying()) {
            startForeground(notificationId, buildNotification());
        } else {
            mNotificationManager.notify(notificationId, buildNotification());
        }*/


       /* Uri trackUri = Uri.parse(songs.get(songPosn).getItem_file().replace(" ", "%20"));
        try {
            Log.e("trackUri", String.valueOf(trackUri));
            mPlayer.setDataSource(getApplicationContext(),trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        mPlayer.prepareAsync();*/
    }

    private Notification buildNotification(int position) {
        final String albumName = songs.get(position).getItem_name();
        final String description = songs.get(position).getItem_description();
        final boolean isPlaying = isPlaying();
        String text = TextUtils.isEmpty(description)
                ? albumName : description;

        int playButtonResId = !isPlaying
                ? R.drawable.ic_play_arrow : R.drawable.ic_pause;
        Intent nowPlayingIntent = getNowPlayingIntent(this);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            if (songs.get(position).getItem_image() == null) {
                artwork = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.no_image);
            } else {
                URL url = new URL(songs.get(position).getItem_image());
                artwork = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if (artwork == null) {
                    artwork = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.no_image);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        mNotificationPostTime = System.currentTimeMillis();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID);
        notificationBuilder
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mSession.getSessionToken())
                                .setShowCancelButton(true))
                .setColor(ContextCompat.getColor(this, R.color.theme_color))
                .setSmallIcon(R.drawable.ic_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentIntent(clickIntent)
                .setContentTitle(albumName)
                .setContentText(text)
                .setSubText("")
                .setLargeIcon(artwork)
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        mService, PlaybackStateCompat.ACTION_STOP))
                .addAction(R.drawable.ic_skip_previous,
                        "",
                        retrievePlaybackAction(PREVIOUS_ACTION))
                .addAction(playButtonResId, "",
                        retrievePlaybackAction(TOGGLEPAUSE_ACTION))
                .addAction(R.drawable.ic_skip_next,
                        "",
                        retrievePlaybackAction(NEXT_ACTION));
        ;
                                /*.setCancelButtonIntent(
                                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                                                this, PlaybackStateCompat.ACTION_STOP)))*/


        //  .setLargeIcon(MusicLibrary.getAlbumBitmap(mContext, description.getMediaId()))

        Notification n = notificationBuilder.build();
        return n;
    }

    private static void scheduleDelayedShutdown() {
        Log.v(TAG, "Scheduling shutdown in " + IDLE_DELAY + " ms");
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }


    private static void setIsSupposedToBePlaying(boolean value, boolean notify) {
        if (mIsSupposedToBePlaying != value) {
            mIsSupposedToBePlaying = value;


            if (!mIsSupposedToBePlaying) {
                scheduleDelayedShutdown();
                mLastPlayedTime = System.currentTimeMillis();
            }

           /* if (notify) {
                notifyChange(PLAYSTATE_CHANGED);
            }*/
        }
    }

    private boolean recentlyPlayed() {
        return isPlaying() || System.currentTimeMillis() - mLastPlayedTime < IDLE_DELAY;
    }

    private void updateNotification(int position) {
        final int newNotifyMode;
        if (isPlaying()) {
            newNotifyMode = NOTIFY_MODE_FOREGROUND;
        } else if (recentlyPlayed()) {
            newNotifyMode = NOTIFY_MODE_BACKGROUND;
        } else {
            newNotifyMode = NOTIFY_MODE_NONE;
        }

        int notificationId = hashCode();
        if (mNotifyMode != newNotifyMode) {
            if (mNotifyMode == NOTIFY_MODE_FOREGROUND) {
                if (Utilities.isLollipop())
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE);
                else
                    stopForeground(newNotifyMode == NOTIFY_MODE_NONE || newNotifyMode == NOTIFY_MODE_BACKGROUND);
            } else if (newNotifyMode == NOTIFY_MODE_NONE) {
                mNotificationManager.cancel(notificationId);
                mNotificationPostTime = 0;
            }
        }

        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            startForeground(notificationId, buildNotification(position));
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            mNotificationManager.notify(notificationId, buildNotification(position));
        }

        mNotifyMode = newNotifyMode;
    }

    public boolean isPlaying() {
        return mIsSupposedToBePlaying;
    }


    private final PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(this, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);

        return PendingIntent.getService(this, 0, intent, 0);
    }


    public static Intent getNowPlayingIntent(Context context) {

        final Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(ACTION_PLAY);
        return intent;
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

    private static final class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

        private final WeakReference<MusicService> mService;

        private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

        private MediaPlayer mNextMediaPlayer;

        private Handler mHandler;

        private boolean mIsInitialized = false;

        private String mNextMediaPath;
        private String TAG = "MusicService";


        public MultiPlayer(final MusicService service) {
            mService = new WeakReference<MusicService>(service);
            mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);

        }

        public void setDataSource(final String path) {
            try {
                mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
                if (mIsInitialized) {
                    setNextDataSource(null);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        private boolean setDataSourceImpl(final MediaPlayer player, final String path) {
            try {
                player.reset();
                player.setOnPreparedListener(null);
                if (path.contains("http:")) {
                    Uri trackUri = Uri.parse(path.replace(" ", "%20"));
                    Log.e("trackUri", String.valueOf(trackUri));
                    player.setDataSource(mService.get(), trackUri);
                } else {
                    player.setDataSource(path);
                }
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
            mNextMediaPath = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mCurrentMediaPlayer.setNextMediaPlayer(null);
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
            mNextMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            try {
                if (setDataSourceImpl(mNextMediaPlayer, path)) {
                        mNextMediaPath = path;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
                    }
                   // mNextMediaPlayer.start();
                } else {
                    if (mNextMediaPlayer != null) {
                        mNextMediaPlayer.release();
                        mNextMediaPlayer = null;

                    }
                }
            } catch (IllegalStateException e) {
                Log.e("setNextDataSource",e.toString());
                e.printStackTrace();
            }
        }


        public void setHandler(final Handler handler) {
            mHandler = handler;
        }


        public boolean isInitialized() {
            return mIsInitialized;
        }


        public void start() {
            mCurrentMediaPlayer.start();
            setIsSupposedToBePlaying(true, true);
        }


        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            setIsSupposedToBePlaying(false, false);
        }


        public void release() {
            mCurrentMediaPlayer.release();
        }


        public void pause() {
            mCurrentMediaPlayer.pause();
            setIsSupposedToBePlaying(false, true);
        }


        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }

        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }


        public long seek(final long whereto) {
            mCurrentMediaPlayer.seekTo((int) whereto);
            return whereto;
        }


        public void setVolume(final float vol) {
            try {
                mCurrentMediaPlayer.setVolume(vol, vol);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
        }

        public void setAudioSessionId(final int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        @Override
        public boolean onError(final MediaPlayer mp, final int what, final int extra) {
            Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    final MusicService service = mService.get();
                    mIsInitialized = false;
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = new MediaPlayer();
                    mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                    Message msg = mHandler.obtainMessage(SERVER_DIED, "error playing audio");
                    mHandler.sendMessageDelayed(msg, 2000);
                    return true;
                default:
                    break;
            }
            return false;
        }


        @Override
        public void onCompletion(final MediaPlayer mp) {
            if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = mNextMediaPlayer;
                mNextMediaPath = null;
                mNextMediaPlayer = null;
                mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
            } else {
                mService.get().mWakeLock.acquire(30000);
                mHandler.sendEmptyMessage(TRACK_ENDED);
                mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {

        }

        public void gotoNext(boolean b) {
        }

        public void prev(boolean b) {
        }

        public boolean isPlaying() {
            return mCurrentMediaPlayer.isPlaying();
        }
    }

    private static final class MusicPlayerHandler extends Handler {
        private final WeakReference<MusicService> mService;
        private float mCurrentVolume = 1.0f;


        public MusicPlayerHandler(final MusicService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<MusicService>(service);
        }


        @Override
        public void handleMessage(final Message msg) {
            final MusicService service = mService.get();
            if (service == null) {
                return;
            }

            synchronized (service) {
                switch (msg.what) {
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }


    private class MediaStoreObserver extends ContentObserver implements Runnable {

        private static final long REFRESH_DELAY = 500;
        private Handler mHandler;

        public MediaStoreObserver(Handler handler) {
            super(handler);
            mHandler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {


            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, REFRESH_DELAY);
        }

        @Override
        public void run() {

            Log.e("ELEVEN", "calling refresh!");
            refresh();
        }
    }

    public void refresh() {
        //notifyChange(REFRESH);
    }

    private static final class TrackErrorInfo {
        public long mId;
        public String mTrackName;

        public TrackErrorInfo(long id, String trackName) {
            mId = id;
            mTrackName = trackName;
        }
    }

    @Override
    public void onDestroy() {
        if (D) Log.d(TAG, "Destroying service");
        super.onDestroy();

        mAlarmManager.cancel(mShutdownIntent);

        mPlayerHandler.removeCallbacksAndMessages(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else mHandlerThread.quit();

        mPlayer.release();
        mPlayer = null;

        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSession.release();

        getContentResolver().unregisterContentObserver(mMediaStoreObserver);

/*

        unregisterReceiver(mIntentReceiver);
        if (mUnmountReceiver != null) {
            unregisterReceiver(mUnmountReceiver);
            mUnmountReceiver = null;
        }
*/

        mWakeLock.release();
    }

    private void setUpMediaSession() {
        mSession = new MediaSessionCompat(this, "Timber");
        //Get MediaSessions transport controls
        transportControls = mSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPause() {
                mPlayer.pause();
                mPausedByTransientLossOfFocus = false;
            }

            @Override
            public void onPlay() {

                mPlayer.start();
            }

            @Override
            public void onSeekTo(long pos) {
                mPlayer.seek(pos);
            }

            @Override
            public void onSkipToNext() {
                mPlayer.gotoNext(true);
            }

            @Override
            public void onSkipToPrevious() {
                mPlayer.prev(false);
            }

            @Override
            public void onStop() {
                mPlayer.pause();
                mPausedByTransientLossOfFocus = false;
                mPlayer.seek(0);
                releaseServiceUiAndStop();
            }
        });
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
    }
}
