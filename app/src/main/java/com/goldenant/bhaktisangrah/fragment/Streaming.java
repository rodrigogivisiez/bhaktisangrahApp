package com.goldenant.bhaktisangrah.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularSeekBar;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.common.util.Utilities;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Random;

import com.facebook.ads.*;

import static com.goldenant.bhaktisangrah.common.util.Constants.*;

/**
 * Created by Adite on 03-01-2016.
 */
public class Streaming extends MasterFragment implements MusicStateListener {
    private View rootView;
    private ImageView backgroundImageView;
    private TextView artistNameView;
    private TextView albumNameView;
    private ImageView trackImageView;
    private TextView trackNameView;
    private TextView currentDuration;
    private CircularSeekBar seekBarView;
    private TextView finalDuration;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton repeatButton;
    private ImageButton shuffleButton;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private ProgressBar progressBar;
  //  private AsyncRunner mTask;
    private Boolean isPlaying;
    String imageUrl;
    private Bundle bundle;
    //    protected static FFmpegMediaPlayer freePlayer;
    MainActivity mContext;
    private boolean isRepeat = false;
    private boolean isShuffle = false;
    ArrayList<SubCategoryModel> ListItem = new ArrayList<SubCategoryModel>();
    ArrayList<String> ListItemFile = new ArrayList<String>();
    ArrayList<String> ListItemName = new ArrayList<String>();
    ArrayList<String> ListItemImage = new ArrayList<String>();
    int ListPosition;
    HomeModel homeModel;
    private static final float BLUR_RADIUS = 25f;
    String item_description = null, trackUrl = null, item_id = null, item_image = null, item_name = null;
    private int mode;
    ProgressDialog mProgressDialog;

    private InterstitialAd interstitial;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private SubCategoryModel currentlyPlaying;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get root view
        mContext = (MainActivity) getMasterActivity();
        rootView = inflater.inflate(R.layout.streaming_player, container, false);

        mContext.hideDrawer();
        mContext.showDrawerBack();

        utils = new Utilities();
        ((MainActivity) getActivity()).setMusicStateListenerListener(this);
        MasterActivity.playScreen = MasterActivity.playScreen + 1;
        if (MasterActivity.playScreen == 3) {
            MasterActivity.playScreen = 0;
            loadBigAds();
        }

        bundle = getArguments();

        if (bundle != null) {

            mode = bundle.getInt("mode");
            if (mode == 1) {
                ListItemFile = (ArrayList<String>) bundle.getSerializable("item_file");
                ListItemName = (ArrayList<String>) bundle.getSerializable("item_name");
                ListItemImage = (ArrayList<String>) bundle.getSerializable("item_image");
                ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
                ListPosition = bundle.getInt("position");

                Log.d("ListItemImage", ListItemImage + " ");
            } else if (mode == 0) {
                ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
                ListPosition = bundle.getInt("position");

                Log.d("ListPosition", "" + ListPosition);
                homeModel = (HomeModel) bundle.getSerializable("CAT_ID");

                if (isVisible()) {

                    mContext.setTitle(" " + ListItem.get(ListPosition).getItem_name());
                }
            }else if (mode == 44) {   //from Home Fragment
                ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
                ListPosition = bundle.getInt("position");

                Log.d("ListPosition", "" + ListPosition);
            }
        }

        // initialize ui elements
        backgroundImageView = (ImageView) rootView.findViewById(R.id.backgroundImage);
        albumNameView = (TextView) rootView.findViewById(R.id.albumName);
        trackImageView = (ImageView) rootView.findViewById(R.id.trackImage);
        trackNameView = (TextView) rootView.findViewById(R.id.trackName);
        currentDuration = (TextView) rootView.findViewById(R.id.currentDuration);
        seekBarView = (CircularSeekBar) rootView.findViewById(R.id.seekBar);
        finalDuration = (TextView) rootView.findViewById(R.id.finalDuration);
        prevButton = (ImageButton) rootView.findViewById(R.id.prevButton);
        playButton = (ImageButton) rootView.findViewById(R.id.playButton);
        nextButton = (ImageButton) rootView.findViewById(R.id.nextButton);

        repeatButton = (ImageButton) rootView.findViewById(R.id.repeatButton);
        shuffleButton = (ImageButton) rootView.findViewById(R.id.shuffleButton);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        adView = new AdView(mContext, Bottom_Banner_placement_id, AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = rootView.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();


        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == 1) {
                    Downloads downloads = new Downloads();
                    mContext.ReplaceFragement(downloads);
                } else if (mode == 0) {
                    CategoryList categoryList = new CategoryList();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", homeModel);
                    categoryList.setArguments(bundle);

                    mContext.ReplaceFragement(categoryList);
                }
            }
        });


        if (ListItem != null || !ListItem.isEmpty()) {
            updateView(ListItem.get(ListPosition));
        }


        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing

                if (mContext.mPlayer != null) {

                    if (mContext.isPlaying()) {
                        mContext.pauseSong();
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_action_play);
                        // }
                    } else {
                        // Resume song
                        mContext.startPlaying();
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.pause);
                        // }
                    }
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mContext.setNextTrack();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mContext.setPreviousTrack();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat OFF");
                    repeatButton.setBackgroundResource(R.drawable.repeat_off);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat ON");
                    // make shuffle to false
                    isShuffle = false;
                    mContext.setShuffleMode(isShuffle);
                    repeatButton.setBackgroundResource(R.drawable.repeat_on);
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
                }
                mContext.setRepeatMode(isRepeat);
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle OFF");
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle ON");
                    // make shuffle to false
                    isRepeat = false;
                    mContext.setRepeatMode(isRepeat);
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_on);
                    repeatButton.setBackgroundResource(R.drawable.repeat_off);
                }
                mContext.setShuffleMode(isShuffle);
            }
        });


        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                //int totalDuration = mContext.mPlayer.getDuration();
                int totalDuration = (int) mContext.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                progressBar.setVisibility(View.VISIBLE);

                //showWaitIndicator(true);
                // forward or backward to certain seconds
                mContext.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
                //   }
            }
        });


        return rootView;
    }

    private void updateView(SubCategoryModel currentlyPlaying) {
        progressBar.setVisibility(View.GONE);
        Log.e("update view", "UPDATE");
        albumNameView.setText(currentlyPlaying.getItem_description());
        trackNameView.setText(currentlyPlaying.getItem_name());
        if (isVisible()) {
            if (currentlyPlaying.getItem_name() != null) {
                mContext.setTitle(" " + currentlyPlaying.getItem_name());
            }
        }
        if (mode == 1) {
            albumNameView.setText("");
            if (currentlyPlaying.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentlyPlaying.getItem_image());
                BitmapDrawable d = new BitmapDrawable(bitmap);
                backgroundImageView.setImageBitmap(bitmap);
                trackImageView.setImageBitmap(bitmap);
            }
        } else if (mode == 0) {
            Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).transform(new BlurTransformation(getActivity())).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
            Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);
        }

        if (mContext.isPlayerPrepared()) {
            // Changing button image to play button
            if (!mContext.isPlaying()) {
                playButton.setImageResource(R.drawable.ic_action_play);

            } else {
                playButton.setImageResource(R.drawable.pause);
            }
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
        if (mContext.isPlayerPrepared()) {
            Log.e("restartLoader", "UPDATE_PROGRESS");
            updateProgressBar();
        }
    }

    private void loadBigAds() {
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

    @Override
    public void restartLoader() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.e("restartLoader", "UPDATE_PROGRESS");
        if (mContext.isPlaying()) {
            updateProgressBar();
        }
    }

    @Override
    public void stopProgressHandler() {
        Log.e("stopProgressHandler", "REMOVE_CALLBACKS");
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onMetaChanged() {
        //   updateNowplayingCard();
        //  updateState();
        Log.e("onMetaChanged", "UPDATE_VIEW");
        updateView(mContext.getActiveAudio());
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            try {
                // if(mContext.mPlayer != null){

                long totalDuration = mContext.getDuration();
                long currentDurations = mContext.getCurrentPosition();

                // Displaying Total Duration time
                long reduse = totalDuration - currentDurations;
                finalDuration.setText("-" + utils.milliSecondsToTimer(reduse));
                // Displaying time completed playing
                currentDuration.setText("" + utils.milliSecondsToTimer(currentDurations));

                // Updating progress bar
                int progress = (int) (utils.getProgressPercentage(currentDurations, totalDuration));
                //Log.d("Progress", ""+progress);
                seekBarView.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
                //  }
            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    };


    public void updateProgressBar() {

        try {

            // if (mContext.mPlayer != null) {
            mHandler.postDelayed(mUpdateTimeTask, 100);
            //    }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

  /*  public void showWaitIndicator(boolean state) {
        showWaitIndicator(state, "");
    }

    public void showWaitIndicator(boolean state, String message) {
        if (state) {
            mProgressDialog = new ProgressDialog(mContext,R.style.TransparentProgressDialog);
            mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progressbar));
            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (mode == 1) {
                        Downloads downloads = new Downloads();
                        mContext.ReplaceFragement(downloads);
                    } else if (mode == 0) {
                        CategoryList categoryList = new CategoryList();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("CAT_ID", homeModel);
                        categoryList.setArguments(bundle);

                        mContext.ReplaceFragement(categoryList);
                    }
                }
                return false;
            }
        });
    }


    public class BlurTransformation implements Transformation {

        RenderScript rs;

        public BlurTransformation(Context context) {
            super();
            rs = RenderScript.create(context);
        }

        @Override
        public Bitmap transform(Bitmap bitmap) {
            // Create another bitmap that will hold the results of the filter.
            Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(5f);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            bitmap.recycle();

            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blur";
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }


}