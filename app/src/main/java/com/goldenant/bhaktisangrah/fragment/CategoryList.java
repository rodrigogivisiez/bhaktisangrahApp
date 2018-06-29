package com.goldenant.bhaktisangrah.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.CategoryAdapter;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.facebook.ads.*;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.goldenant.bhaktisangrah.common.util.Constants.*;

/**
 * Created by Jaydeep Jikadra on 1/2/2018.
 */
public class CategoryList extends MasterFragment implements MusicStateListener
{
    MainActivity mContext;
    ArrayList<SubCategoryModel> CatListItem = new ArrayList<SubCategoryModel>();
    HomeModel homeModel = new HomeModel();
    private ListView mCategoryList;
    private String category_id;
    private Bundle bundle;

    private AdView adView;
    InterstitialAd interstitialAd;
    private ImageView mPlayPause;

    public static RelativeLayout topContainer;
    private ProgressBar mProgress;
    private SeekBar mSeekBar;
    private int overflowcounter = 0;
    private TextView mTitle,mArtist;
    private View rootView,playPauseWrapper;
    private CircularImageView mAlbumArt;

    private final View.OnClickListener mPlayPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mContext.mPlayer != null){
                if (mContext.isPlaying()) {
                    mContext.pauseSong();
                    // Changing button image to play button
                    mPlayPause.setImageResource(R.drawable.ic_play_arrow);
                    // }
                } else {
                    // Resume song
                    mContext.startPlaying();
                    // Changing button image to pause button
                    mPlayPause.setImageResource(R.drawable.ic_pause);
                    // }
                }
            }

        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = (MainActivity) getMasterActivity();

        View V = inflater.inflate(R.layout.category_list_fragment, container, false);
        ((MainActivity) getActivity()).setMusicStateListenerListener(this);
        return V;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext.hideDrawer();
        mContext.showDrawerBack();

        mContext.setTitle("Play song Or Download");
        mCategoryList = (ListView) view.findViewById(R.id.listView_cat_list);

        // Laod Bottom Banner facebook Add
        adView = new AdView(mContext, Bottom_Banner_placement_id, AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = view.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();

        //mini player view
        mPlayPause = (ImageView) view.findViewById(R.id.play_pause);
        playPauseWrapper = view.findViewById(R.id.play_pause_wrapper);
        playPauseWrapper.setOnClickListener(mPlayPauseListener);
        mProgress = (ProgressBar) view.findViewById(R.id.song_progress_normal);
       // mSeekBar = (SeekBar) view.findViewById(R.id.song_progress);
        mTitle = (TextView) view.findViewById(R.id.title);
        mArtist = (TextView) view.findViewById(R.id.artist);
        mAlbumArt =  view.findViewById(R.id.album_art_nowplayingcard);
        topContainer = view.findViewById(R.id.topContainer);
       // mArtist.setMovementMethod(new ScrollingMovementMethod());


        MasterActivity.listScreen = MasterActivity.listScreen + 1;
        if(MasterActivity.listScreen == 5) {
            MasterActivity.listScreen = 0;
            loadBigAds();
        }

        bundle = getArguments();

        if(bundle != null){
            homeModel = (HomeModel) bundle.getSerializable("CAT_ID");
            category_id = homeModel.getCategory_id();
        }

        updateBottomPlayer();

        isInternet = new InternetStatus().isInternetOn(mContext);

        /*if(mContext.bundle.containsKey(category_id)){

            CatListItem = new ArrayList<SubCategoryModel>();
            CatListItem = (ArrayList<SubCategoryModel>) mContext.bundle.getSerializable(category_id);

            CategoryAdapter Adapter = new CategoryAdapter(mContext,R.layout.category_item, CatListItem, homeModel);
            mCategoryList.setAdapter(Adapter);

        }else {
*/
            if(isInternet){

//            if(mContext.MasterCategoryArray.size() > 0){
//                CategoryAdapter Adapter = new CategoryAdapter(mContext,R.layout.category_item, mContext.MasterCategoryArray, homeModel);
//                mCategoryList.setAdapter(Adapter);
//            }
//            else{
                getCategory();
//            }
     //       }
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

    private void getCategory()
    {
        mContext.showWaitIndicator(true);

        NetworkRequest dishRequest = new NetworkRequest(getMasterActivity());
        List<NameValuePair> carData = new ArrayList<NameValuePair>(1);
        carData.add(new BasicNameValuePair(
                Constants.CATEGORY_ID, category_id));
        dishRequest.sendRequest(Constants.API_GET_ITEM_BY_CAT_ID_URL,
                carData, catCallback);
    }

    NetworkRequest.NetworkRequestCallback catCallback = new NetworkRequest.NetworkRequestCallback()
    {
        @Override
        public void OnNetworkResponseReceived(JSONObject response)
        {
            Log.d("SUBCATEGORY_API ", "" + response.toString());
            mContext.showWaitIndicator(false);
            try
            {
                if (response != null)
                {
                    JSONObject jObject = new JSONObject(response.toString());
                    String status = jObject.getString("status");

                    if(status.equalsIgnoreCase("1")){


                        CatListItem = new ArrayList<SubCategoryModel>();

                        JSONArray data = jObject.getJSONArray("data");

                        Log.d("data.length()",""+data.length());

                        for (int i = 0; i < data.length(); i++)
                        {
                            SubCategoryModel categoryModel = new SubCategoryModel();

                            categoryModel.setItem_id(data.getJSONObject(i).getString(Constants.item_id));
                            categoryModel.setItem_name(data.getJSONObject(i).getString(Constants.item_name));
                            categoryModel.setItem_description(data.getJSONObject(i).getString(Constants.item_description));
                            categoryModel.setItem_file(data.getJSONObject(i).getString(Constants.item_file));
                            categoryModel.setItem_image(data.getJSONObject(i).getString(Constants.item_image));
                            categoryModel.setDownload_name(data.getJSONObject(i).getString(Constants.download_name));

                            CatListItem.add(categoryModel);
                        }

                        mContext.bundle.putSerializable(category_id,CatListItem);

                        CategoryAdapter Adapter = new CategoryAdapter(mContext,R.layout.category_item, CatListItem, homeModel);
                        mCategoryList.setAdapter(Adapter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mContext.showWaitIndicator(false);
            }
        }

        @Override
        public void OnNetworkErrorReceived(String error) {
            mContext.showWaitIndicator(false);
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }

    private void updateBottomPlayer() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        ArrayList<SubCategoryModel> audioList = storage.loadAudio();
        int audioIndex = storage.loadAudioIndex();
        int mode=storage.loadMode();
        if(audioList==null){
            //topContainer.setVisibility(View.GONE);
            return;
        }
        updateView(audioList.get(audioIndex),mode);
    }
    @Override
    public void onDestroy()
    {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void stopProgressHandler() {

    }

    @Override
    public void onMetaChanged() {
        Log.e("onMetaChanged","UPDATE_VIEW");
        updateBottomPlayer();
    }

    private void updateView(SubCategoryModel currentlyPlaying, int mode) {
        if (currentlyPlaying == null) {
           // topContainer.setVisibility(View.GONE);
            return;
        }
       // topContainer.setVisibility(View.VISIBLE);
        Log.e("update view","UPDATE");
        mTitle.setText(currentlyPlaying.getItem_description());
        mArtist.setText(currentlyPlaying.getItem_name());

        if (mode == 1) {
            if (currentlyPlaying.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentlyPlaying.getItem_image());
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mAlbumArt.setImageBitmap(bitmap);
            }
        }
        else{Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).placeholder(R.drawable.no_image).fit().into(mAlbumArt);}
        if (mContext.isPlayerPrepared()) {
            // Changing button image to play button
            if (!mContext.isPlaying()) {
                mPlayPause.setImageResource(R.drawable.ic_play_arrow);

            } else {
                mPlayPause.setImageResource(R.drawable.ic_pause);
            }
        }
    }
}
