package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.DownloadAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.facebook.ads.*;
import com.goldenant.bhaktisangrah.common.util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ankita on 1/2/2016.
 */
public class Downloads extends MasterFragment
{
    ListView listView_downloads;

    MainActivity mContext;

    ArrayList<String> songsName = new ArrayList<String>();
    ArrayList<String> songsImage = new ArrayList<String>();
    ArrayList<String> songs = new ArrayList<String>();

    LinearLayout error_layout_downloads;

    TextView textView_download;
    InterstitialAd interstitialAd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = (MainActivity) getMasterActivity();
        return inflater.inflate(R.layout.download_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.hideDrawer();
        mContext.showDrawerBack();
        mContext.setTitle("Downloads");

        MasterActivity.playScreen = MasterActivity.playScreen + 1;
        if(MasterActivity.playScreen == 3) {
            MasterActivity.playScreen = 0;
            loadBigAds();
        }

        listView_downloads = (ListView) view.findViewById(R.id.listView_downloads);

        error_layout_downloads = (LinearLayout) view.findViewById(R.id.error_layout_downloads);

        textView_download = (TextView) view.findViewById(R.id.textView_download);

        try
        {
            //Get path of song name
            File myFile = new File("/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/BhaktiSagar.txt");
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
//            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
//                aBuffer += aDataRow + "\n";
                songsName.add(aDataRow);
            }

            myReader.close();

            //Get path of images
            File file = new File("/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/BhaktiSagarImage.txt");
            FileInputStream fileIn = new FileInputStream(file);
            BufferedReader Reader = new BufferedReader(
                    new InputStreamReader(fileIn));
            String DataRow = "";
//            String Buffer = "";
            while ((DataRow = Reader.readLine()) != null) {
//                Buffer += DataRow + "\n";
                songsImage.add(DataRow);
            }

            Reader.close();

            Log.d("songsImage", "" + songsImage);

            //Get path of songs

            File fileSong = new File("/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/BhaktiSagarSongs.txt");
            FileInputStream fileInSong = new FileInputStream(fileSong);
            BufferedReader ReaderSong = new BufferedReader(
                    new InputStreamReader(fileInSong));
            String DataRowSong = "";
//            String Buffer = "";
            while ((DataRowSong = ReaderSong.readLine()) != null) {
//                Buffer += DataRow + "\n";
                songs.add(DataRowSong);
            }

            ReaderSong.close();
        }
        catch (Exception e)
        {
            Log.d("ERROR",""+e.getMessage());
        }

        if(songsName.size() > 0)
        {
            DownloadAdapter adapter = new DownloadAdapter(mContext,R.layout.downloads_list_item,songsName,songsImage,songs);
            listView_downloads.setAdapter(adapter);
        }
        else
        {
            textView_download.setTypeface(mContext.getTypeFace());
            error_layout_downloads.setVisibility(View.VISIBLE);
        }

        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment home = new HomeFragment();
                mContext.ReplaceFragement(home);
            }
        });

    }

    private void loadBigAds()
    {
        interstitialAd = new InterstitialAd(mContext, Constants.Big_Banner_placement_id);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

//    public void getFromSdcard()
//    {
////        File file = new File(android.os.Environment.getExternalStorageDirectory(),"/Bhakti sagar/images/");
//
//        File file = new File(Environment.getExternalStorageDirectory()
//                + File.separator + "/Bhakti sagar/images/" +getItem(position).getItem_image_name()););
//
//        Log.d("file",""+file);
//
//        if (file.isDirectory())
//        {
//            listFile = file.listFiles();
//            Log.d("listFile",""+listFile);
//
//            for (int i = 0; i < listFile.length; i++)
//            {
//                songsImage.add(listFile[i].getAbsolutePath());
//            }
//        }
//    }

    @Override
    public void onResume()
    {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy()
    {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}
