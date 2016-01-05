package com.goldenant.bhaktisangrah;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.goldenant.bhaktisangrah.model.NavDrawerItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.START;

public class MainActivity extends MasterActivity {

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

    TextView title;

    //GCM registration start
    String reg_id,register_id;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    GoogleCloudMessaging gcmObj;
    //Rergistration end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imageView = (ImageView) findViewById(R.id.drawer_indicator);
        mDrawerList = (ListView) findViewById(R.id.drawer_content);
        resources = getResources();
        mContext = getApplicationContext();
        this.font = Typeface.createFromAsset(mContext.getAssets(), "ProximaNova-Light.otf");

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(Color.WHITE);
        imageView.setImageDrawable(drawerArrowDrawable);
        title =(TextView) findViewById(R.id.title_text);
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

                switch (position){
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
            registerInBackground();
        }

        if(loadPrefs() == null || !loadPrefs().equalsIgnoreCase(reg_id))
        {
            getGCMCallRequest();
        }
        // END GCM
    }


    static boolean isNetConnected(Context paramContext)
    {
        ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
        return (localConnectivityManager.getActiveNetworkInfo() != null) && (localConnectivityManager.getActiveNetworkInfo().isAvailable()) && (localConnectivityManager.getActiveNetworkInfo().isConnected());
    }

    static void rate(Context paramContext)
    {
        if (isNetConnected(paramContext))
        {
            paramContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + paramContext.getPackageName())).addFlags(268435456));
            return;
        }
        Toast.makeText(paramContext, "Please enable wifi or data from settings", Toast.LENGTH_LONG).show();
    }
    static void share(Context paramContext)
    {
        if (isNetConnected(paramContext))
        {
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

    public static void feedback(Context paramContext,String name,String mobile,String dis)
    {
        if (isNetConnected(paramContext))
        {
            String str = "Name: "+name+"\n"+"Mobile: "+mobile+"\n"+"Dis: "+dis;

            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("plain/text");
            localIntent.addFlags(268435456);
            localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "shethconstructiongroup@gmail.com" });
            localIntent.putExtra("android.intent.extra.SUBJECT", "Feedback for Bhakti sagar");
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
    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
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

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(MainActivity.this);
                    }
                    reg_id = gcmObj.register(ApplicationConstants.GOOGLE_PROJ_ID);

                    savePrefs("GCM", reg_id);
                    Log.i("GCD Registered ID. ", "" + reg_id);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                if (!TextUtils.isEmpty(reg_id))
                {
                }
                else
                {
                    Log.d("ERROR: ", "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."+ msg);
//					Toast.makeText(
//							SplashActivity.this,
//							"Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
//									+ msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    public void savePrefs(String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private void getGCMCallRequest() {

        NetworkRequest deliveryLocationRequest = new NetworkRequest(
                MainActivity.this);
        deliveryLocationRequest.sendRequest(Constants.API_GCM_ID_URL,
                getGCMData(), GCMCallback);
    }

    private List<NameValuePair> getGCMData()
    {
        List<NameValuePair> gcmDATA = new ArrayList<NameValuePair>();

        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("deviceId",""+deviceId);

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

    public String loadPrefs()
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        register_id = sp.getString("GCM", null);

        return register_id;
    }
    //GCM registration end

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }
    }
}
