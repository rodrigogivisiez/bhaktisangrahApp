package com.goldenant.bhaktisangrah;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenant.bhaktisangrah.common.ui.DrawerArrowDrawable;
import com.goldenant.bhaktisangrah.common.ui.DrawerListAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.fragment.AboutUs;
import com.goldenant.bhaktisangrah.fragment.Downloads;
import com.goldenant.bhaktisangrah.fragment.FeedBack;
import com.goldenant.bhaktisangrah.fragment.HomeFragment;
import com.goldenant.bhaktisangrah.fragment.Notification;
import com.goldenant.bhaktisangrah.fragment.Share;
import com.goldenant.bhaktisangrah.gcm.ApplicationConstants;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.NavDrawerItem;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.goldenant.bhaktisangrah.service.MediaPlayerService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;

import static android.view.Gravity.START;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.goldenant.bhaktisangrah.common.util.Constants.Key_user_fcm_id;
import static com.goldenant.bhaktisangrah.service.MediaPlayerService.NOTIFICATION_ID;

public class MainActivity extends MasterActivity implements MusicStateListener {

    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private ListView mDrawerList;
    private ImageView imageView;
    private DrawerLayout drawer;
    private Resources resources;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private DrawerListAdapter adapter;
    private Context mContext;
    private ArrayList<NavDrawerItem> navDrawerItems;
    public FragmentManager fragmentManager = getSupportFragmentManager();
    private Typeface font;
    public ImageButton drawer_back;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.goldenant.bhaktisangrah.PlayNewAudio";
    public Intent playerIntent;
    TextView title;

    //GCM registration start
    String reg_id, register_id;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private NotificationManager mNotificationManager;


    //Rergistration end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Fabric.with(this, new Crashlytics());
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getString(R.string.app_id));

        if (playerIntent == null) {
            playerIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        mPlaybackStatus = new PlaybackStatus(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_back = (ImageButton) findViewById(R.id.drawer_back);
        imageView = (ImageView) findViewById(R.id.drawer_indicator);
        mDrawerList = (ListView) findViewById(R.id.drawer_content);
        resources = getResources();
        mContext = getApplicationContext();
        this.font = Typeface.createFromAsset(mContext.getAssets(), "ProximaNova-Light.otf");

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(Color.WHITE);
        imageView.setImageDrawable(drawerArrowDrawable);
        title = (TextView) findViewById(R.id.title_text);
        title.setTypeface(font);
        title.setText("Home Page");

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerList.setItemChecked(0, true);


        // Custom Header ...
        /*mHeader = getLayoutInflater().inflate(
                R.layout.navigation_list_header, mDrawerList, false);
        TextView txtProfileName = (TextView) mHeader
                .findViewById(R.id.txtProfileName);

        txtProfileName.setText("Profile Name");
        ImageView imgProfileImage = (ImageView) mHeader
                .findViewById(R.id.imgProfileImage);


        mDrawerList.addHeaderView(mHeader);*/
        // Custom Header End...

        navMenuTitles = getResources().getStringArray(
                R.array.nav_drawer_title);

        navMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
                .getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
                .getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
                .getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
                .getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
                .getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
                .getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons
                .getResourceId(6, -1)));
        navMenuIcons.recycle();


        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or
                // 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(START)) {
                    drawer.closeDrawer(START);
                } else {
                    drawer.openDrawer(START);
                }
            }
        });

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                switch (position) {
                    case 0:
                        Fragment homeFragment = new HomeFragment();
                        ReplaceFragement(homeFragment);
                        break;

                    case 1:
                        Fragment Downloads = new Downloads();
                        ReplaceFragement(Downloads);

                        break;

                    case 2:
                        Fragment uinFragment = new Notification();
                        ReplaceFragement(uinFragment);
                        break;

                    case 3:
                        Fragment share = new Share();
                        ReplaceFragement(share);

                        break;

                    case 4:
                        rate(mContext);
                        break;

                    case 5:
                        Fragment feedback = new FeedBack();
                        ReplaceFragement(feedback);

                        break;

                    case 6:
                        Fragment aboutus = new AboutUs();
                        ReplaceFragement(aboutus);

                        break;

                }

                drawer.closeDrawers();
            }
        });

        adapter = new DrawerListAdapter(MainActivity.this, navDrawerItems);
        mDrawerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (savedInstanceState == null) {
            Fragment homeFragment = new HomeFragment();
            ReplaceFragement(homeFragment);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {

                        Log.d("MainActivity", "BackStack count::"
                                + fragmentManager.getBackStackEntryCount());
                        Fragment f = getSupportFragmentManager()
                                .findFragmentById(R.id.fragment_container);

                        if (fragmentManager.getBackStackEntryCount() == 0) {
                            finish();
                        } else {

                        }

                    }
                });

        //FOR GCM REGISTRATION

        if (checkPlayServices()) {
            createToken();
            getGCMCallRequest();
        }

        if (loadPrefs() == null || !loadPrefs().equalsIgnoreCase(reg_id)) {
            getGCMCallRequest();
        }
        // END GCM
    }


    static boolean isNetConnected(Context paramContext) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService("connectivity");
        return (localConnectivityManager.getActiveNetworkInfo() != null) && (localConnectivityManager.getActiveNetworkInfo().isAvailable()) && (localConnectivityManager.getActiveNetworkInfo().isConnected());
    }

    static void rate(Context paramContext) {
        if (isNetConnected(paramContext)) {
            paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + paramContext.getPackageName())).addFlags(268435456));
            return;
        }
        Toast.makeText(paramContext, "Please enable wifi or data from settings", Toast.LENGTH_LONG).show();
    }

    static void share(Context paramContext) {
        if (isNetConnected(paramContext)) {
            String str = paramContext.getResources().getString(R.string.share);
            Intent localIntent1 = new Intent("android.intent.action.SEND");
            localIntent1.setType("text/plain");
            localIntent1.putExtra("android.intent.extra.SUBJECT", str);
            localIntent1.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + paramContext.getPackageName());
            Intent localIntent2 = Intent.createChooser(localIntent1, "Share using");
            localIntent2.addFlags(268435456);
            paramContext.startActivity(localIntent2);
            return;
        }
        Toast.makeText(paramContext, "Please enable wifi or data from settings", Toast.LENGTH_LONG).show();
    }

    public static void feedback(Context paramContext, String name, String mobile, String dis) {
        if (isNetConnected(paramContext)) {
            String str = "Name: " + name + "\n" + "Mobile: " + mobile + "\n" + "Dis: " + dis;

            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("plain/text");
            localIntent.addFlags(268435456);
            localIntent.putExtra("android.intent.extra.EMAIL", new String[]{"goldenant.apps@gmail.com"});
            localIntent.putExtra("android.intent.extra.SUBJECT", "Feedback for Bhakti Sangrah");
            localIntent.putExtra("android.intent.extra.TEXT", str);
            paramContext.startActivity(Intent.createChooser(localIntent, "Send mail...").addFlags(268435456));
            return;
        }
        Toast.makeText(paramContext, "Please enable wifi or data from settings", Toast.LENGTH_LONG).show();
    }

    public void ReplaceFragement(Fragment loginFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container, loginFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //GCM registration start
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        this,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            // This device supports Play services, App will work normally.
        }
        return true;
    }


    public void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void createToken() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("test");
            reg_id = FirebaseInstanceId.getInstance().getToken();
            Log.e("refreshedToken", "" + reg_id);
            savePrefs(Constants.Key_user_fcm_id, reg_id);
        } catch (Exception e) {
            Log.e("token error", e.toString());
            e.printStackTrace();
        }
    }

    private void getGCMCallRequest() {

        NetworkRequest deliveryLocationRequest = new NetworkRequest(
                MainActivity.this);
        deliveryLocationRequest.sendRequest(Constants.API_GCM_ID_URL,
                getGCMData(), GCMCallback);
    }

    private List<NameValuePair> getGCMData() {
        List<NameValuePair> gcmDATA = new ArrayList<NameValuePair>();

        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("deviceId", "" + deviceId);

        gcmDATA.add(new BasicNameValuePair(Constants.GCM_ID, loadPrefs()));
        gcmDATA.add(new BasicNameValuePair(Constants.DEVICE_ID, deviceId));

        return gcmDATA;
    }

    NetworkRequest.NetworkRequestCallback GCMCallback = new NetworkRequest.NetworkRequestCallback() {
        @Override
        public void OnNetworkResponseReceived(JSONObject response) {
            try {

                JSONObject jGCMResult = new JSONObject(response.toString());
                String message = jGCMResult.getString("msg");
                Log.d("GCM Message. ", message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void OnNetworkErrorReceived(String error) {
            // showWaitIndicator(false);
        }
    };

    public String loadPrefs() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        register_id = sp.getString(Constants.Key_user_fcm_id, null);

        return register_id;
    }
    //GCM registration end

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }
    }

    public void showNotification(String fileImage) {
        // new MusicNotification(this,fileImage);
        //finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        cancelNotification();
    }


    public void cancelNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID); // Notification ID to cancel
    }

    @Override
    protected void onStart() {
        super.onStart();

        final IntentFilter filter = new IntentFilter();
        // Update song status
        filter.addAction(MediaPlayerService.UPDATE_SONG_STATUS);
        // Track changes Play and pause changes
        filter.addAction(MediaPlayerService.META_CHANGED);
        // Update progressbar
        filter.addAction(MediaPlayerService.REFRESH);
        // Stop progressbar callbacks
        filter.addAction(MediaPlayerService.STOP_PROGRESS);
        // If there is an error playing a track
        filter.addAction(MediaPlayerService.TRACK_ERROR);

        registerReceiver(mPlaybackStatus, filter);

    }

    public void setMusicStateListenerListener(final MusicStateListener status) {
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }

    //Binding this Client to the AudioPlayer Service
    public static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void restartLoader() {
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.restartLoader();
            }
        }
    }

    @Override
    public void stopProgressHandler() {
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.stopProgressHandler();
            }
        }
    }

    @Override
    public void onMetaChanged() {
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    private final class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<MainActivity> mReference;


        public PlaybackStatus(final MainActivity activity) {
            mReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            MainActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MediaPlayerService.META_CHANGED)) {
                    baseActivity.onMetaChanged();
                } else if (action.equals(MediaPlayerService.UPDATE_SONG_STATUS)) {
                        requestSongStatusChange();
                } else if (action.equals(MediaPlayerService.REFRESH)) {
                    baseActivity.restartLoader();
                } else if (action.equals(MediaPlayerService.STOP_PROGRESS)) {
                    baseActivity.stopProgressHandler();
                } else if (action.equals(MediaPlayerService.TRACK_ERROR)) {
                    final String errorMsg = context.getString(R.string.error_playing_track,
                            intent.getStringExtra(MediaPlayerService.TrackErrorExtra.TRACK_NAME));
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void requestSongStatusChange() {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            ArrayList<SubCategoryModel> audioList = storage.loadAudio();
            int playedAudioIndex = storage.loadPlayedAudioIndex();
            Log.e("playedAudioIndex",String.valueOf(playedAudioIndex));
            if (audioList == null || playedAudioIndex==-1) {
                return;
            }
            String itemId = audioList.get(playedAudioIndex).getItem_id();
            updateSongStatus(itemId,1);
        }
    }


    public void updateSongStatus(String item_id, int flag) {
      String  songId=item_id;
       // new UpdateSongStatus(songId, flag).execute();
    }




    private class UpdateSongStatus extends AsyncTask {

        String songId;
        int flag;

        public UpdateSongStatus(String itemId, int i) {
            songId = itemId;
            flag = i;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            NetworkRequest.NetworkRequestCallback catCallback = new NetworkRequest.NetworkRequestCallback() {
                @Override
                public void OnNetworkResponseReceived(JSONObject response) {
                    Log.d("UPDATE_STATUS_API ", "" + response.toString());

                    try {
                        if (response != null) {
                            JSONObject jObject = new JSONObject(response.toString());
                            String status = jObject.getString("status");
                            Log.e("UPDATE_STATUS_API", response.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void OnNetworkErrorReceived(String error) {

                }
            };
            NetworkRequest dishRequest = new NetworkRequest(MainActivity.this);
            List<NameValuePair> carData = new ArrayList<NameValuePair>(2);
            carData.add(new BasicNameValuePair(Constants.item_id, songId));
            carData.add(new BasicNameValuePair(Constants.FLAG, Integer.toString(flag)));
            dishRequest.sendRequest(Constants.API_UPDATE_SONG_STATUS_URL,
                    carData, catCallback);


            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}
