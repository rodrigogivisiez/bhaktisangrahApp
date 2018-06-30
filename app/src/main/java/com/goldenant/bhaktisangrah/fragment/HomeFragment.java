package com.goldenant.bhaktisangrah.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.HomeAdapter;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.HomeModel;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.facebook.ads.*;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.goldenant.bhaktisangrah.common.util.Constants.Bottom_Banner_placement_id;


/**
 * Created by Ajay on 11-09-2015.
 */
public class HomeFragment extends MasterFragment implements MusicStateListener {

    MainActivity mContext;

    ListView listView_category;

    ArrayList<HomeModel> CatArray = new ArrayList<HomeModel>();

    private AdView adView;

    private ImageView mPlayPause;
    public RelativeLayout topContainer;
    private ProgressBar mProgress;
    private SeekBar mSeekBar;
    private int overflowcounter = 0;
    private TextView mTitle, mArtist;
    private View rootView, playPauseWrapper;
    private CircularImageView mAlbumArt;
    private View nowPlayingCard;

    private final View.OnClickListener mPlayPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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


    };

    private final View.OnClickListener playingCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                if (mContext.isPlaying()) {
                    Fragment investProgramDetail = new Streaming();
                    StorageUtil storage = new StorageUtil(getApplicationContext());
                    Bundle bundle = new Bundle();
                    bundle.putInt("mode", 44);
                    bundle.putSerializable("data", storage.loadAudio());
                    bundle.putInt("position",storage.loadAudioIndex());
                    investProgramDetail.setArguments(bundle);
                    mContext.ReplaceFragement(investProgramDetail);
                }


        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = (MainActivity) getMasterActivity();

        View V = inflater.inflate(R.layout.home_fragment, container, false);
        ((MainActivity) getActivity()).setMusicStateListenerListener(this);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adView = new AdView(mContext, Bottom_Banner_placement_id, AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = view.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();


        //mini player view
        nowPlayingCard = view.findViewById(R.id.now_playing_card);
        mPlayPause = (ImageView) view.findViewById(R.id.play_pause);
        playPauseWrapper = view.findViewById(R.id.play_pause_wrapper);
        playPauseWrapper.setOnClickListener(mPlayPauseListener);
        mProgress = (ProgressBar) view.findViewById(R.id.song_progress_normal);
        // mSeekBar = (SeekBar) view.findViewById(R.id.song_progress);
        mTitle = (TextView) view.findViewById(R.id.title);
        mArtist = (TextView) view.findViewById(R.id.artist);
        // mArtist.setMovementMethod(new ScrollingMovementMethod());
        mAlbumArt = view.findViewById(R.id.album_art_nowplayingcard);
        topContainer = (RelativeLayout) view.findViewById(R.id.topContainer);
        nowPlayingCard.setOnClickListener(playingCardClickListener);
        mContext.showDrawer();
        mContext.hideDrawerBack();
        mContext.setTitle("Categories");

        listView_category = (ListView) view.findViewById(R.id.listView_category);


        if (MasterActivity.CatArray.size() > 0) {
            HomeAdapter Adapter = new HomeAdapter(mContext, R.layout.category_item, MasterActivity.CatArray);
            listView_category.setAdapter(Adapter);
        } else {
            if (mContext.isInternet == true) {
                getCategory();
            } else {
                ToastUtil.showLongToastMessage(mContext, "No internet connection found");
            }
        }

        updateBottomPlayer();
        listView_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                if (MasterActivity.CatArray.size() > 0) {
                    Fragment investProgramDetail = new CategoryList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", MasterActivity.CatArray.get(position));
                    investProgramDetail.setArguments(bundle);
                    mContext.ReplaceFragement(investProgramDetail);
                } else {
                    Fragment investProgramDetail = new CategoryList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", CatArray.get(position));
                    investProgramDetail.setArguments(bundle);
                    mContext.ReplaceFragement(investProgramDetail);
                }
            }
        });
    }

    private void getCategory() {
        mContext.showWaitIndicator(true);
        NetworkRequest dishRequest = new NetworkRequest(getMasterActivity());
        dishRequest.sendRequest(Constants.API_GET_CATEGORY_URL,
                null, catCallback);
    }

    NetworkRequest.NetworkRequestCallback catCallback = new NetworkRequest.NetworkRequestCallback() {
        @Override
        public void OnNetworkResponseReceived(JSONObject response) {
            Log.d("CATEGORY_API ", "" + response.toString());
            mContext.showWaitIndicator(false);
            try {
                if (response != null) {
                    JSONObject jObject = new JSONObject(response.toString());
                    String status = jObject.getString("status");

                    CatArray = new ArrayList<HomeModel>();

                    JSONArray data = jObject.getJSONArray("data");

                    Log.d("data.length()", "" + data.length());

                    for (int i = 0; i < data.length(); i++) {
                        HomeModel home = new HomeModel();

                        home.setCategory_id(data.getJSONObject(i).getString(Constants.CATEGORY_ID));
                        home.setCategory_image(data.getJSONObject(i).getString(Constants.CATEGORY_IMAGE));
                        home.setCategory_name(data.getJSONObject(i).getString(Constants.CATEGORY_NAME));

                        CatArray.add(home);
                    }

                    MasterActivity.CatArray = CatArray;

                    HomeAdapter Adapter = new HomeAdapter(mContext, R.layout.category_item, CatArray);
                    listView_category.setAdapter(Adapter);
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
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {

            private boolean doubleBackToExitPressedOnce;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (doubleBackToExitPressedOnce) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        mContext.cancelNotification();
                        System.exit(1);
                    }

                    this.doubleBackToExitPressedOnce = true;
                    ToastUtil.showLongToastMessage(mContext, getString(R.string.exit_title));

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);

                    return true;
                }
                return false;
            }
        });
    }

    private void updateBottomPlayer() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        ArrayList<SubCategoryModel> audioList = storage.loadAudio();
        int audioIndex = storage.loadAudioIndex();
        int mode = storage.loadMode();
        if (audioList == null) {
            nowPlayingCard.setVisibility(View.GONE);
            return;
        }
        updateView(audioList.get(audioIndex), mode);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
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
        Log.e("onMetaChanged", "UPDATE_VIEW");
        updateBottomPlayer();
    }

    private void updateView(SubCategoryModel currentlyPlaying, int mode) {
        if (currentlyPlaying == null) {
            nowPlayingCard.setVisibility(View.GONE);
            return;
        }
        nowPlayingCard.setVisibility(View.VISIBLE);
        Log.e("update view", "UPDATE");
        mTitle.setText(currentlyPlaying.getItem_description());
        mArtist.setText(currentlyPlaying.getItem_name());
        if (mode == 1) {
            if (currentlyPlaying.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentlyPlaying.getItem_image());
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mAlbumArt.setImageBitmap(bitmap);
            }
        } else {
            Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).placeholder(R.drawable.no_image).fit().into(mAlbumArt);
        }

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
