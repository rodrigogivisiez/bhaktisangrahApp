package com.goldenant.bhaktisangrah;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
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
import com.goldenant.bhaktisangrah.fragment.AboutUs;
import com.goldenant.bhaktisangrah.fragment.Downloads;
import com.goldenant.bhaktisangrah.fragment.FeedBack;
import com.goldenant.bhaktisangrah.fragment.HomeFragment;
import com.goldenant.bhaktisangrah.fragment.Notification;
import com.goldenant.bhaktisangrah.fragment.Share;
import com.goldenant.bhaktisangrah.model.NavDrawerItem;

import java.util.ArrayList;

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
    private View mHeader;
    private Context mContext;
    private ArrayList<NavDrawerItem> navDrawerItems;
    public FragmentManager fragmentManager = getSupportFragmentManager();

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imageView = (ImageView) findViewById(R.id.drawer_indicator);
        mDrawerList = (ListView) findViewById(R.id.drawer_content);
        resources = getResources();
        mContext = getApplicationContext();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(Color.WHITE);
        imageView.setImageDrawable(drawerArrowDrawable);
        title =(TextView) findViewById(R.id.title_text);
        title.setText("Home Page");

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerList.setItemChecked(0, true);


        // Custom Header ...
        mHeader = getLayoutInflater().inflate(
                R.layout.navigation_list_header, mDrawerList, false);
        TextView txtProfileName = (TextView) mHeader
                .findViewById(R.id.txtProfileName);

        txtProfileName.setText("Profile Name");
        ImageView imgProfileImage = (ImageView) mHeader
                .findViewById(R.id.imgProfileImage);


        mDrawerList.addHeaderView(mHeader);
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
                    case 1:
                        Fragment homeFragment = new HomeFragment();
                        ReplaceFragement(homeFragment);
                        break;

                    case 2:
                        Fragment Downloads = new Downloads();
                        ReplaceFragement(Downloads);

                        break;

                    case 3:
                        Fragment uinFragment = new Notification();
                        ReplaceFragement(uinFragment);
                        break;

                    case 4:
                        Fragment share = new Share();
                        ReplaceFragement(share);

                        break;

                    case 5:
                        rate(mContext);
                        break;

                    case 6:
                        Fragment feedback = new FeedBack();
                        ReplaceFragement(feedback);

                        break;

                    case 7:
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

    static void feedback(Context paramContext)
    {
        String str = paramContext.getResources().getString(R.string.feedback_sub);
        if (isNetConnected(paramContext))
        {
            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("plain/text");
            localIntent.addFlags(268435456);
            localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "shethconstructiongroup@gmail.com" });
            localIntent.putExtra("android.intent.extra.SUBJECT", "Feedback for " + str);
            localIntent.putExtra("android.intent.extra.TEXT", "Write your feedback");
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
}
